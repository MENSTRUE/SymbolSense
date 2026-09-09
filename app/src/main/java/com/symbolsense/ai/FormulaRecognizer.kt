package com.symbolsense.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import java.io.Closeable
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * FormulaRecognition V4 pipeline (CROHME Detector V2):
 *
 * image
 * -> robust class-agnostic detector
 * -> crop quality gate
 * -> StructuralTokenRecognizer (currently '=')
 * -> existing exact-32 SymbolClassifier
 * -> false-positive rejection
 * -> stacked-minus '=' merge fallback
 * -> SpatialParserV2 (linear + superscript)
 *
 * The isolated 32-class classifier model is intentionally NOT retrained or
 * expanded here. times=12 and pi=27 remain unchanged.
 */
class FormulaRecognizer(
    private val context: Context
) : Closeable {

    companion object {
        private const val CLASSIFIER_TOP_K = 3
        private const val CROP_PADDING_RATIO = 0.12f

        private const val MIN_DETECTOR_CONFIDENCE = 0.35f
        private const val MIN_CLASSIFIER_CONFIDENCE = 0.60f
        private const val STRONG_CLASSIFIER_CONFIDENCE = 0.88f
        private const val MIN_COMBINED_SCORE = 0.30f

        private const val MAX_BOXES_TO_CLASSIFY = 96
        private const val MAX_ACCEPTED_SYMBOLS = 64
    }

    private val detector = SymbolDetector(context.applicationContext)
    private val classifier = SymbolClassifier(context.applicationContext)

    @Synchronized
    fun recognize(
        bitmap: Bitmap,
        topK: Int = CLASSIFIER_TOP_K
    ): SymbolRecognitionResult {
        require(bitmap.width > 0 && bitmap.height > 0) {
            "Bitmap formula is empty."
        }

        val startNs = SystemClock.elapsedRealtimeNanos()
        val detection = detector.detect(bitmap)

        if (detection.boxes.isEmpty()) {
            return fallbackToWholeCrop(
                bitmap = bitmap,
                detectorTimeMs = detection.inferenceTimeMs,
                startNs = startNs,
                topK = topK
            )
        }

        val recognized = ArrayList<RecognizedSymbol>()

        val candidateBoxes = detection.boxes
            .filter { it.confidence >= MIN_DETECTOR_CONFIDENCE }
            .sortedByDescending { it.confidence }
            .take(MAX_BOXES_TO_CLASSIFY)

        for (box in candidateBoxes) {
            val crop = cropWithPadding(bitmap, box) ?: continue

            if (!StructuralTokenRecognizer.isMeaningfulCrop(crop)) {
                continue
            }

            // Classifier runs first. The structural '=' recognizer is only allowed
            // to override a plausible horizontal/operator crop, so textured crops
            // cannot all turn into '=' before the classifier gets a chance.
            val classification = classifier.classify(crop, topK)
            var best = classification.best
            var topPredictions = classification.topK

            val equalCandidate = StructuralTokenRecognizer.recognizeEqual(crop)
            val allowEqualOverride = equalCandidate != null && (
                best.name == "minus" ||
                    best.name == "divide" ||
                    (crop.width.toFloat() / kotlin.math.max(1, crop.height).toFloat() >= 1.35f &&
                        best.confidence < 0.78f)
                )

            if (allowEqualOverride) {
                best = equalCandidate!!
                topPredictions = listOf(best)
            }

            val combinedScore = box.confidence * best.confidence

            val classifierAccept =
                best.confidence >= MIN_CLASSIFIER_CONFIDENCE ||
                    best.confidence >= STRONG_CLASSIFIER_CONFIDENCE

            val accept =
                classifierAccept &&
                    combinedScore >= MIN_COMBINED_SCORE

            if (!accept) continue

            recognized += RecognizedSymbol(
                prediction = best,
                topK = topPredictions,
                detectorConfidence = box.confidence,
                boundingBox = normalizedBox(box, bitmap),
                classifierInferenceTimeMs = classification.inferenceTimeMs,
                reliable =
                    (classification.reliable || best.name == "equal") &&
                        box.confidence >= MIN_DETECTOR_CONFIDENCE
            )
        }

        if (recognized.isEmpty()) {
            return fallbackToWholeCrop(
                bitmap = bitmap,
                detectorTimeMs = detection.inferenceTimeMs,
                startNs = startNs,
                topK = topK
            )
        }

        val deduped = removeNearDuplicates(recognized)
        val withEquals = mergeStackedMinusAsEqual(deduped)
        val finalSymbols = withEquals
            .sortedByDescending { it.detectorConfidence * it.prediction.confidence }
            .take(MAX_ACCEPTED_SYMBOLS)

        if (finalSymbols.isEmpty()) {
            return fallbackToWholeCrop(
                bitmap = bitmap,
                detectorTimeMs = detection.inferenceTimeMs,
                startNs = startNs,
                topK = topK
            )
        }

        val parsed = SpatialParserV2.parse(finalSymbols)
        val ordered = parsed.orderedSymbols
        val first = ordered.firstOrNull() ?: finalSymbols.first()

        val endNs = SystemClock.elapsedRealtimeNanos()

        return SymbolRecognitionResult(
            best = first.prediction,
            topK = first.topK,
            inferenceTimeMs = (endNs - startNs) / 1_000_000.0,
            reliable =
                ordered.isNotEmpty() &&
                    ordered.all { it.reliable },
            symbols = ordered,
            structuredDisplay = parsed.display,
            structuredLatex = parsed.latex,
            detectorInferenceTimeMs = detection.inferenceTimeMs,
            mode = RecognitionMode.MULTI_SYMBOL
        )
    }

    fun recognize(
        uri: Uri,
        topK: Int = CLASSIFIER_TOP_K
    ): SymbolRecognitionResult {
        return recognize(
            bitmap = loadBitmap(uri),
            topK = topK
        )
    }

    private fun normalizedBox(
        box: DetectorBox,
        bitmap: Bitmap
    ): RecognitionBoundingBox {
        return RecognitionBoundingBox(
            left = (box.leftPx / bitmap.width).coerceIn(0f, 1f),
            top = (box.topPx / bitmap.height).coerceIn(0f, 1f),
            right = (box.rightPx / bitmap.width).coerceIn(0f, 1f),
            bottom = (box.bottomPx / bitmap.height).coerceIn(0f, 1f)
        )
    }

    private fun removeNearDuplicates(
        symbols: List<RecognizedSymbol>
    ): List<RecognizedSymbol> {
        if (symbols.size <= 1) return symbols

        val sorted = symbols.sortedByDescending {
            it.detectorConfidence * it.prediction.confidence
        }

        val kept = mutableListOf<RecognizedSymbol>()

        for (candidate in sorted) {
            val duplicate = kept.any { existing ->
                normalizedIou(candidate.boundingBox, existing.boundingBox) >= 0.72f
            }

            if (!duplicate) kept += candidate
        }

        return kept
    }

    /**
     * Fallback for detectors that split '=' into two separate horizontal bars.
     * This is structural geometry, not a classifier-label hack.
     */
    private fun mergeStackedMinusAsEqual(
        symbols: List<RecognizedSymbol>
    ): List<RecognizedSymbol> {
        if (symbols.size < 2) return symbols

        val used = BooleanArray(symbols.size)
        val out = mutableListOf<RecognizedSymbol>()

        for (i in symbols.indices) {
            if (used[i]) continue
            val a = symbols[i]

            if (a.prediction.name != "minus") {
                out += a
                used[i] = true
                continue
            }

            var bestJ = -1
            var bestScore = 0f

            for (j in i + 1 until symbols.size) {
                if (used[j]) continue
                val b = symbols[j]
                if (b.prediction.name != "minus") continue

                val aw = a.boundingBox.right - a.boundingBox.left
                val bw = b.boundingBox.right - b.boundingBox.left
                val minW = min(aw, bw)
                val maxW = max(aw, bw)
                if (minW <= 0f || minW / max(maxW, 1e-6f) < 0.62f) continue

                val overlap = max(
                    0f,
                    min(a.boundingBox.right, b.boundingBox.right) -
                        max(a.boundingBox.left, b.boundingBox.left)
                )
                val overlapRatio = overlap / max(minW, 1e-6f)
                if (overlapRatio < 0.65f) continue

                val acy = (a.boundingBox.top + a.boundingBox.bottom) * 0.5f
                val bcy = (b.boundingBox.top + b.boundingBox.bottom) * 0.5f
                val verticalGap = kotlin.math.abs(acy - bcy)

                val avgWidth = (aw + bw) * 0.5f
                if (verticalGap < 0.008f || verticalGap > max(0.09f, avgWidth * 0.55f)) {
                    continue
                }

                val score = overlapRatio * (minW / max(maxW, 1e-6f))
                if (score > bestScore) {
                    bestScore = score
                    bestJ = j
                }
            }

            if (bestJ < 0) {
                out += a
                used[i] = true
                continue
            }

            val b = symbols[bestJ]
            used[i] = true
            used[bestJ] = true

            val equalPrediction = SymbolPrediction(
                id = -100,
                name = "equal",
                display = "=",
                latex = "=",
                confidence = (
                    (a.prediction.confidence + b.prediction.confidence) * 0.5f
                    ).coerceIn(0f, 0.97f)
            )

            out += RecognizedSymbol(
                prediction = equalPrediction,
                topK = listOf(equalPrediction),
                detectorConfidence = max(a.detectorConfidence, b.detectorConfidence),
                boundingBox = RecognitionBoundingBox(
                    left = min(a.boundingBox.left, b.boundingBox.left),
                    top = min(a.boundingBox.top, b.boundingBox.top),
                    right = max(a.boundingBox.right, b.boundingBox.right),
                    bottom = max(a.boundingBox.bottom, b.boundingBox.bottom)
                ),
                classifierInferenceTimeMs =
                    a.classifierInferenceTimeMs + b.classifierInferenceTimeMs,
                reliable = a.reliable && b.reliable
            )
        }

        return out
    }

    private fun normalizedIou(
        a: RecognitionBoundingBox,
        b: RecognitionBoundingBox
    ): Float {
        val left = max(a.left, b.left)
        val top = max(a.top, b.top)
        val right = min(a.right, b.right)
        val bottom = min(a.bottom, b.bottom)

        val iw = max(0f, right - left)
        val ih = max(0f, bottom - top)
        val intersection = iw * ih

        val areaA = max(0f, a.right - a.left) * max(0f, a.bottom - a.top)
        val areaB = max(0f, b.right - b.left) * max(0f, b.bottom - b.top)
        val union = areaA + areaB - intersection

        return if (union <= 0f) 0f else intersection / union
    }

    private fun fallbackToWholeCrop(
        bitmap: Bitmap,
        detectorTimeMs: Double,
        startNs: Long,
        topK: Int
    ): SymbolRecognitionResult {
        val single = classifier.classify(bitmap, topK)
        val endNs = SystemClock.elapsedRealtimeNanos()

        val instance = RecognizedSymbol(
            prediction = single.best,
            topK = single.topK,
            detectorConfidence = 0f,
            boundingBox = RecognitionBoundingBox(
                left = 0f,
                top = 0f,
                right = 1f,
                bottom = 1f
            ),
            classifierInferenceTimeMs = single.inferenceTimeMs,
            reliable = single.reliable
        )

        return single.copy(
            inferenceTimeMs = (endNs - startNs) / 1_000_000.0,
            symbols = listOf(instance),
            structuredDisplay = single.best.display,
            structuredLatex = single.best.latex,
            detectorInferenceTimeMs = detectorTimeMs,
            mode = RecognitionMode.DETECTOR_FALLBACK
        )
    }

    private fun cropWithPadding(
        bitmap: Bitmap,
        box: DetectorBox
    ): Bitmap? {
        val side = max(box.width, box.height)
        val pad = side * CROP_PADDING_RATIO

        val left = floor(box.leftPx - pad).toInt().coerceIn(0, bitmap.width - 1)
        val top = floor(box.topPx - pad).toInt().coerceIn(0, bitmap.height - 1)
        val right = ceil(box.rightPx + pad).toInt().coerceIn(left + 1, bitmap.width)
        val bottom = ceil(box.bottomPx + pad).toInt().coerceIn(top + 1, bitmap.height)

        val width = right - left
        val height = bottom - top

        if (width < 2 || height < 2) return null

        return Bitmap.createBitmap(
            bitmap,
            left,
            top,
            width,
            height
        )
    }

    @Suppress("DEPRECATION")
    private fun loadBitmap(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(
                context.contentResolver,
                uri
            )

            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                uri
            )
        }
    }

    override fun close() {
        detector.close()
        classifier.close()
    }
}
