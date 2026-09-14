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
 * FormulaRecognition V5.6 pipeline (CROHME Detector V2):
 *
 * image
 * -> robust class-agnostic detector
 * -> scan quality guard + optional ROI auto-recovery
 * -> crop quality gate
 * -> StructuralTokenRecognizer ('=' + '÷')
 * -> existing exact-32 SymbolClassifier
 * -> two-stage detector gate (strong symbols + weak-operator rescue)
 * -> stacked-minus '=' merge fallback
 * -> SpatialParserV2 (linear + superscript + context reranking)
 *
 * The isolated 32-class classifier model is intentionally NOT retrained or
 * expanded here. times=12 and pi=27 remain unchanged.
 */
class FormulaRecognizer(
    private val context: Context
) : Closeable {

    companion object {
        private const val CLASSIFIER_TOP_K = 5
        private const val CROP_PADDING_RATIO = 0.12f

        // Keep detector decode permissive so thin operators survive.
        private const val BASE_DETECTOR_CONFIDENCE = 0.35f

        // Ordinary symbols still need the stronger runtime gate.
        private const val STRONG_DETECTOR_CONFIDENCE = 0.60f

        private const val MIN_CLASSIFIER_CONFIDENCE = 0.55f
        private const val MIN_COMBINED_SCORE = 0.30f

        // Low-confidence detector boxes may only survive as operators.
        private const val RESCUE_OPERATOR_MIN_CLASSIFIER = 0.55f
        private const val RESCUE_OPERATOR_MIN_RATIO_TO_BEST = 0.50f
        private const val RESCUE_OPERATOR_MIN_COMBINED = 0.19f

        private const val MAX_BOXES_TO_CLASSIFY = 96
        private const val MAX_ACCEPTED_SYMBOLS = 64

        // Temporary UI diagnostics. Keep small to avoid retaining too many Bitmaps.
        private const val DEBUG_CAPTURE_ENABLED = true
        private const val MAX_DEBUG_CROPS = 24

        private val RESCUE_OPERATOR_NAMES = setOf(
            "plus",
            "minus",
            "times",
            "divide",
            "equal",
            "less_than",
            "greater_than",
            "less_equal",
            "greater_equal"
        )
    }

    private val detector = SymbolDetector(context.applicationContext)
    private val classifier = SymbolClassifier(context.applicationContext)

    private data class WorkingImage(
        val bitmap: Bitmap,
        val offsetX: Int,
        val offsetY: Int,
        val originalWidth: Int,
        val originalHeight: Int,
        val autoRecovered: Boolean
    )

    @Synchronized
    fun recognize(
        bitmap: Bitmap,
        topK: Int = CLASSIFIER_TOP_K
    ): SymbolRecognitionResult {
        require(bitmap.width > 0 && bitmap.height > 0) {
            "Bitmap formula is empty."
        }

        val startNs = SystemClock.elapsedRealtimeNanos()

        if (DEBUG_CAPTURE_ENABLED) {
            FormulaDebugStore.clear()
        }

        val debugCrops =
            ArrayList<FormulaDebugCrop>()

        /*
         * =====================================================
         * V5.6 SCAN QUALITY + AUTO RECOVERY
         * =====================================================
         *
         * Run detector once on the user's crop.
         * If the formula is too small / weak, crop the detected formula ROI
         * and run the detector ONE more time. Detector's fixed resize then
         * effectively gives the formula more pixels without digital camera zoom.
         */
        val initialDetection =
            detector.detect(
                bitmap
            )

        val initialQuality =
            ScanQualityEvaluator.evaluate(
                imageWidth =
                    bitmap.width,
                imageHeight =
                    bitmap.height,
                boxes =
                    initialDetection
                        .boxes
                        .map {
                            it.toScanQualityBox()
                        }
            )

        var working =
            WorkingImage(
                bitmap = bitmap,
                offsetX = 0,
                offsetY = 0,
                originalWidth =
                    bitmap.width,
                originalHeight =
                    bitmap.height,
                autoRecovered =
                    false
            )

        var detection =
            initialDetection

        var detectorTimeMs =
            initialDetection
                .inferenceTimeMs

        if (
            initialQuality.canAutoRecover &&
            initialDetection.boxes.isNotEmpty()
        ) {
            val recoveryRegion =
                ScanQualityEvaluator
                    .buildRecoveryRegion(
                        bitmap = bitmap,
                        boxes =
                            initialDetection
                                .boxes
                                .map {
                                    it.toScanQualityBox()
                                }
                    )

            if (
                recoveryRegion != null
            ) {
                val recoveredBitmap =
                    ScanQualityEvaluator
                        .crop(
                            bitmap = bitmap,
                            region =
                                recoveryRegion
                        )

                val retryDetection =
                    detector.detect(
                        recoveredBitmap
                    )

                detectorTimeMs +=
                    retryDetection
                        .inferenceTimeMs

                val retryQuality =
                    ScanQualityEvaluator
                        .evaluate(
                            imageWidth =
                                recoveredBitmap.width,
                            imageHeight =
                                recoveredBitmap.height,
                            boxes =
                                retryDetection
                                    .boxes
                                    .map {
                                        it.toScanQualityBox()
                                    }
                        )

                if (
                    shouldUseRecoveredDetection(
                        initialQuality =
                            initialQuality,
                        retryQuality =
                            retryQuality
                    )
                ) {
                    working =
                        WorkingImage(
                            bitmap =
                                recoveredBitmap,
                            offsetX =
                                recoveryRegion.left,
                            offsetY =
                                recoveryRegion.top,
                            originalWidth =
                                bitmap.width,
                            originalHeight =
                                bitmap.height,
                            autoRecovered =
                                true
                        )

                    detection =
                        retryDetection
                }
            }
        }

        /*
         * Detector kosong:
         * - masih beri kesempatan isolated symbol yang sangat kuat;
         * - kalau tidak, jangan mengarang hasil formula dari whole-image crop.
         */
        if (
            detection.boxes.isEmpty()
        ) {
            return isolatedFallbackOrThrow(
                bitmap = bitmap,
                detectorTimeMs =
                    detectorTimeMs,
                startNs =
                    startNs,
                topK =
                    topK,
                quality =
                    initialQuality
            )
        }

        val activeQuality =
            ScanQualityEvaluator.evaluate(
                imageWidth =
                    working.bitmap.width,
                imageHeight =
                    working.bitmap.height,
                boxes =
                    detection
                        .boxes
                        .map {
                            it.toScanQualityBox()
                        }
            )

        val recognized =
            ArrayList<RecognizedSymbol>()

        val candidateBoxes = detection.boxes
            .filter { it.confidence >= BASE_DETECTOR_CONFIDENCE }
            .sortedByDescending { it.confidence }
            .take(MAX_BOXES_TO_CLASSIFY)

        for ((detectorIndex, box) in candidateBoxes.withIndex()) {
            val strongDetector =
                box.confidence >= STRONG_DETECTOR_CONFIDENCE

            val crop = cropWithPadding(
                bitmap =
                    working.bitmap,
                box =
                    box
            ) ?: continue

            /*
             * Strong boxes use the normal crop-quality gate.
             * Weak boxes bypass that hard rejection because thin operators
             * (<, >, -, <=, >=, ÷) can contain very little ink.
             *
             * Weak boxes are still tightly restricted below: they may only
             * survive as operator candidates with classifier support.
             */
            val meaningfulCrop =
                StructuralTokenRecognizer.isMeaningfulCrop(crop)

            if (
                strongDetector &&
                !meaningfulCrop
            ) {
                continue
            }

            val classification =
                classifier.classify(
                    crop,
                    topK
                )

            if (
                DEBUG_CAPTURE_ENABLED &&
                debugCrops.size < MAX_DEBUG_CROPS
            ) {
                debugCrops += FormulaDebugCrop(
                    detectorIndex = detectorIndex,
                    rawCrop = crop,
                    canonicalClassifierInput =
                        classifier.preprocessForDebug(crop),
                    detectorConfidence = box.confidence,
                    classifierTopK = classification.topK,
                    boundingBox =
                        normalizedBox(box, working),
                    rawWidth = crop.width,
                    rawHeight = crop.height
                )
            }

            var best =
                classification.best

            var topPredictions =
                classification.topK

            /*
             * Structural divide recovery.
             *
             * A camera-rendered ÷ can lose its two dots in classifier
             * preprocessing and collapse into a very confident "minus".
             *
             * We do NOT globally map minus -> divide.
             * StructuralTokenRecognizer must independently verify:
             *     dot + horizontal bar + dot
             */
            val divideCandidate =
                StructuralTokenRecognizer.recognizeDivide(crop)

            val allowDivideOverride =
                divideCandidate != null &&
                    (
                        best.name == "minus" ||
                            best.name == "divide" ||
                            best.name == "times" ||
                            best.name == "x"
                        )

            var structuralDivideApplied =
                false

            if (allowDivideOverride) {
                val divide =
                    divideCandidate!!

                best =
                    divide

                topPredictions =
                    (
                        listOf(divide) +
                            topPredictions.filter {
                                it.name != "divide"
                            }
                        )
                        .take(topK)

                structuralDivideApplied =
                    true
            }

            /*
             * Existing '=' structural recognition remains active.
             * Do not let '=' overwrite an already verified ÷.
             */
            if (!structuralDivideApplied) {
                val equalCandidate =
                    StructuralTokenRecognizer.recognizeEqual(crop)

                val allowEqualOverride =
                    equalCandidate != null &&
                        (
                            best.name == "minus" ||
                                best.name == "divide" ||
                                (
                                    crop.width.toFloat() /
                                        kotlin.math.max(
                                            1,
                                            crop.height
                                        ).toFloat() >= 1.35f &&
                                        best.confidence < 0.78f
                                    )
                            )

                if (allowEqualOverride) {
                    best =
                        equalCandidate!!

                    topPredictions =
                        listOf(best)
                }
            }

            /*
             * Weak-detector operator rescue.
             *
             * Old V5.3 discarded detector scores < 0.60 before classification.
             * That made thin operators impossible to recover later.
             */
            if (!strongDetector) {
                val operatorCandidate =
                    topPredictions
                        .asSequence()
                        .filter {
                            it.name in RESCUE_OPERATOR_NAMES
                        }
                        .filter {
                            it.confidence >=
                                RESCUE_OPERATOR_MIN_CLASSIFIER
                        }
                        .filter {
                            it.confidence >=
                                classification.best.confidence *
                                RESCUE_OPERATOR_MIN_RATIO_TO_BEST
                        }
                        .maxByOrNull {
                            it.confidence
                        }

                if (
                    operatorCandidate != null &&
                    best.name !in RESCUE_OPERATOR_NAMES
                ) {
                    best =
                        operatorCandidate
                }
            }

            val combinedScore =
                box.confidence *
                    best.confidence

            val accept =
                if (strongDetector) {
                    best.confidence >=
                        MIN_CLASSIFIER_CONFIDENCE &&
                        combinedScore >=
                        MIN_COMBINED_SCORE
                } else {
                    best.name in
                        RESCUE_OPERATOR_NAMES &&
                        best.confidence >=
                        RESCUE_OPERATOR_MIN_CLASSIFIER &&
                        combinedScore >=
                        RESCUE_OPERATOR_MIN_COMBINED
                }

            if (!accept) {
                continue
            }

            recognized +=
                RecognizedSymbol(
                    prediction = best,
                    topK = topPredictions,
                    detectorConfidence =
                        box.confidence,
                    boundingBox =
                        normalizedBox(
                            box =
                                box,
                            working =
                                working
                        ),
                    classifierInferenceTimeMs =
                        classification.inferenceTimeMs,
                    reliable =
                        best.confidence >=
                            MIN_CLASSIFIER_CONFIDENCE &&
                            box.confidence >=
                            BASE_DETECTOR_CONFIDENCE
                )
        }

        if (DEBUG_CAPTURE_ENABLED) {
            FormulaDebugStore.publish(debugCrops)
        }

        if (
            recognized.isEmpty()
        ) {
            throw ScanQualityException(
                ScanQualityEvaluator
                    .lowRecognitionReport(
                        activeQuality
                    )
            )
        }

        val deduped = removeNearDuplicates(recognized)
        val withEquals = mergeStackedMinusAsEqual(deduped)
        val finalSymbols = withEquals
            .sortedByDescending { it.detectorConfidence * it.prediction.confidence }
            .take(MAX_ACCEPTED_SYMBOLS)

        if (
            finalSymbols.isEmpty()
        ) {
            throw ScanQualityException(
                ScanQualityEvaluator
                    .lowRecognitionReport(
                        activeQuality
                    )
            )
        }

        val parsed = SpatialParserV2.parse(finalSymbols)
        val ordered = parsed.orderedSymbols
        val first = ordered.firstOrNull() ?: finalSymbols.first()

        /*
         * Jangan blok scan yang berhasil hanya karena framing tidak ideal.
         *
         * TOO_CLOSE / CUT_OFF baru menjadi error ketika hasil yang tersisa
         * sangat lemah (<= 1 simbol dan tidak reliable).
         */
        val resultReliable =
            ordered.isNotEmpty() &&
                ordered.all {
                    it.reliable
                }

        val severeFramingIssue =
            activeQuality.issue ==
                ScanQualityIssue.TOO_CLOSE ||
                activeQuality.issue ==
                ScanQualityIssue.CUT_OFF

        if (
            severeFramingIssue &&
            ordered.size <= 1 &&
            !resultReliable
        ) {
            throw ScanQualityException(
                activeQuality
            )
        }

        val endNs =
            SystemClock.elapsedRealtimeNanos()

        return SymbolRecognitionResult(
            best = first.prediction,
            topK = first.topK,
            inferenceTimeMs = (endNs - startNs) / 1_000_000.0,
            reliable =
                resultReliable,
            symbols = ordered,
            structuredDisplay = parsed.display,
            structuredLatex = parsed.latex,
            detectorInferenceTimeMs = detectorTimeMs,
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
        working: WorkingImage
    ): RecognitionBoundingBox {

        val left =
            working.offsetX +
                box.leftPx

        val top =
            working.offsetY +
                box.topPx

        val right =
            working.offsetX +
                box.rightPx

        val bottom =
            working.offsetY +
                box.bottomPx

        return RecognitionBoundingBox(
            left =
                (
                    left /
                        working.originalWidth
                    )
                    .coerceIn(
                        0f,
                        1f
                    ),
            top =
                (
                    top /
                        working.originalHeight
                    )
                    .coerceIn(
                        0f,
                        1f
                    ),
            right =
                (
                    right /
                        working.originalWidth
                    )
                    .coerceIn(
                        0f,
                        1f
                    ),
            bottom =
                (
                    bottom /
                        working.originalHeight
                    )
                    .coerceIn(
                        0f,
                        1f
                    )
        )
    }

    private fun DetectorBox.toScanQualityBox(): ScanQualityBox {
        return ScanQualityBox(
            left = leftPx,
            top = topPx,
            right = rightPx,
            bottom = bottomPx,
            confidence = confidence
        )
    }

    /**
     * Retry ROI dipakai jika kualitasnya jelas membaik atau setidaknya
     * mempertahankan jumlah simbol sambil menaikkan kualitas detector.
     */
    private fun shouldUseRecoveredDetection(
        initialQuality: ScanQualityReport,
        retryQuality: ScanQualityReport
    ): Boolean {

        if (
            retryQuality.boxCount <= 0
        ) {
            return false
        }

        if (
            retryQuality.issue ==
                ScanQualityIssue.GOOD &&
            initialQuality.issue !=
                ScanQualityIssue.GOOD
        ) {
            return true
        }

        if (
            retryQuality.boxCount >
            initialQuality.boxCount
        ) {
            return true
        }

        return (
            retryQuality.boxCount >=
                initialQuality.boxCount &&
                retryQuality
                    .meanDetectorConfidence >=
                initialQuality
                    .meanDetectorConfidence -
                    0.05f
            )
    }

    /**
     * Preserve isolated-symbol use cases, but require a very strong result.
     * Formula images are not silently collapsed into one arbitrary class.
     */
    private fun isolatedFallbackOrThrow(
        bitmap: Bitmap,
        detectorTimeMs: Double,
        startNs: Long,
        topK: Int,
        quality: ScanQualityReport
    ): SymbolRecognitionResult {

        if (
            !StructuralTokenRecognizer
                .isMeaningfulCrop(
                    bitmap
                )
        ) {
            throw ScanQualityException(
                ScanQualityEvaluator
                    .noSymbolsReport()
            )
        }

        val aspect =
            bitmap.width.toFloat() /
                max(
                    1,
                    bitmap.height
                ).toFloat()

        val single =
            classifier.classify(
                bitmap,
                topK
            )

        val likelyIsolatedSymbol =
            aspect in
                0.42f..2.40f &&
                single.best.confidence >=
                0.90f

        if (
            !likelyIsolatedSymbol
        ) {
            throw ScanQualityException(
                if (
                    quality.issue ==
                    ScanQualityIssue.GOOD
                ) {
                    ScanQualityEvaluator
                        .lowRecognitionReport(
                            quality
                        )
                } else {
                    quality
                }
            )
        }

        val endNs =
            SystemClock.elapsedRealtimeNanos()

        val instance =
            RecognizedSymbol(
                prediction =
                    single.best,
                topK =
                    single.topK,
                detectorConfidence =
                    0f,
                boundingBox =
                    RecognitionBoundingBox(
                        left = 0f,
                        top = 0f,
                        right = 1f,
                        bottom = 1f
                    ),
                classifierInferenceTimeMs =
                    single.inferenceTimeMs,
                reliable =
                    single.reliable
            )

        return single.copy(
            inferenceTimeMs =
                (
                    endNs -
                        startNs
                    ) /
                    1_000_000.0,
            symbols =
                listOf(
                    instance
                ),
            structuredDisplay =
                single.best.display,
            structuredLatex =
                single.best.latex,
            detectorInferenceTimeMs =
                detectorTimeMs,
            mode =
                RecognitionMode.DETECTOR_FALLBACK
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
