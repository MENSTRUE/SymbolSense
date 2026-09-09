package com.symbolsense.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.SystemClock
import org.json.JSONObject
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * SymbolSense multi-symbol detector runtime V4 — CROHME2019 Detector V2.
 *
 * Main deployment contract:
 * - Uses CROHME2019 Detector V2 FP16 (FLOAT32 I/O).
 * - Decoder mirrors Kaggle evaluation: calibrated threshold 0.35, 3x3 local peaks,
 *   size decode, NMS IoU 0.35, up to 128 detections.
 * - Keeps formula ROI extraction so a phone screenshot/photo can be cropped to the
 *   writing region, but DOES NOT binarize/canonicalize the detector input anymore.
 *   Detector V2 was trained with grayscale camera/screen backgrounds, polarity,
 *   moire, blur, lighting, JPEG and hard negatives; feeding raw grayscale is the
 *   closest Android match to that training domain.
 * - The exact-32 isolated SymbolClassifier remains a separate model.
 */
data class DetectorBox(
    val leftPx: Float,
    val topPx: Float,
    val rightPx: Float,
    val bottomPx: Float,
    val confidence: Float
) {
    val width: Float get() = (rightPx - leftPx).coerceAtLeast(0f)
    val height: Float get() = (bottomPx - topPx).coerceAtLeast(0f)
    val centerX: Float get() = (leftPx + rightPx) * 0.5f
    val centerY: Float get() = (topPx + bottomPx) * 0.5f
}

data class SymbolDetectionResult(
    val boxes: List<DetectorBox>,
    val inferenceTimeMs: Double,
    val rawPeakCount: Int = 0,
    val decodeThreshold: Float = 0f
)

class SymbolDetector(
    private val context: Context,
    private val modelFileName: String = MODEL_FILE_NAME,
    private val configFileName: String = CONFIG_FILE_NAME
) : Closeable {

    companion object {
        private const val MODEL_FILE_NAME = "symbolsense_detector_model.tflite"
        private const val CONFIG_FILE_NAME = "detector_config.json"

        private const val INPUT_H = 256
        private const val INPUT_W = 384
        private const val GRID_H = 64
        private const val GRID_W = 96
        private const val STRIDE = 4

        private const val ANALYSIS_MAX_W = 720
        private const val ANALYSIS_MAX_H = 520
        private const val MODEL_MARGIN = 18

        private const val PEAK_SCAN_FLOOR = 0.14f // legacy; calibrated decode no longer uses this
        private const val MIN_INK_PIXELS_IN_BOX = 5
        private const val MIN_INK_DENSITY = 0.012f
        private const val MAX_INK_DENSITY = 0.78f
        private const val MAX_REASONABLE_BOX_WIDTH_RATIO = 0.80f
        private const val MAX_REASONABLE_BOX_HEIGHT_RATIO = 0.80f
    }

    private data class DetectorConfig(
        val confidenceThreshold: Float,
        val nmsIouThreshold: Float,
        val maxDetections: Int,
        val maxRawPeaks: Int,
        val minConfidenceFloat: Float,
        val minConfidenceUInt8: Float
    )

    private data class FormulaRoi(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int
    ) {
        val width: Int get() = right - left + 1
        val height: Int get() = bottom - top + 1
    }

    private data class PreparedInput(
        val buffer: ByteBuffer,
        val inkMask: ByteArray,
        val scale: Float,
        val padX: Float,
        val padY: Float,
        val resizedWidth: Int,
        val resizedHeight: Int,
        val roi: FormulaRoi,
        val originalWidth: Int,
        val originalHeight: Int
    )

    private data class Peak(
        val gy: Int,
        val gx: Int,
        val score: Float
    )

    private data class RawBox(
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float,
        val score: Float
    )

    private data class DecodeResult(
        val boxes: List<RawBox>,
        val rawPeakCount: Int,
        val threshold: Float
    )

    private data class Component(
        val area: Int,
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int,
        val touchesBorder: Boolean
    ) {
        val width: Int get() = right - left + 1
        val height: Int get() = bottom - top + 1
    }

    private val config = loadConfig()

    private val interpreter = Interpreter(
        loadModel(),
        Interpreter.Options().apply { setNumThreads(4) }
    )

    private val inputTensor = interpreter.getInputTensor(0)
    private val inputType = inputTensor.dataType()
    private val inputQuant = inputTensor.quantizationParams()

    private val heatmapOutputIndex: Int
    private val sizeOutputIndex: Int
    private val heatmapOutputType: DataType

    init {
        require(inputTensor.shape().contentEquals(intArrayOf(1, INPUT_H, INPUT_W, 1))) {
            "Detector input mismatch: ${inputTensor.shape().contentToString()}"
        }
        require(inputType == DataType.FLOAT32 || inputType == DataType.UINT8) {
            "Detector input must be FLOAT32 or UINT8, actual=$inputType"
        }

        var heat = -1
        var size = -1

        for (i in 0 until interpreter.outputTensorCount) {
            val tensor = interpreter.getOutputTensor(i)
            val shape = tensor.shape()
            require(shape.size == 4 && shape[0] == 1 && shape[1] == GRID_H && shape[2] == GRID_W) {
                "Unknown detector output shape: ${shape.contentToString()}"
            }
            when (shape[3]) {
                1 -> heat = i
                2 -> size = i
            }
        }

        require(heat >= 0 && size >= 0) { "Detector heatmap/size head not found." }
        heatmapOutputIndex = heat
        sizeOutputIndex = size
        heatmapOutputType = interpreter.getOutputTensor(heatmapOutputIndex).dataType()
    }

    @Synchronized
    fun detect(bitmap: Bitmap): SymbolDetectionResult {
        require(bitmap.width > 0 && bitmap.height > 0)

        val prepared = prepareInput(bitmap)
        val outputs = mutableMapOf<Int, Any>()

        for (index in 0 until interpreter.outputTensorCount) {
            val tensor = interpreter.getOutputTensor(index)
            outputs[index] = ByteBuffer
                .allocateDirect(tensor.numBytes())
                .order(ByteOrder.nativeOrder())
        }

        val startNs = SystemClock.elapsedRealtimeNanos()
        interpreter.runForMultipleInputsOutputs(arrayOf(prepared.buffer), outputs)
        val endNs = SystemClock.elapsedRealtimeNanos()

        val heat = readOutputFloat(
            outputs.getValue(heatmapOutputIndex) as ByteBuffer,
            heatmapOutputIndex
        )
        val size = readOutputFloat(
            outputs.getValue(sizeOutputIndex) as ByteBuffer,
            sizeOutputIndex
        )

        val decoded = decode(heat, size, prepared)
        val mapped = decoded.boxes
            .mapNotNull { mapBackToOriginal(it, prepared) }
            .filter { it.width >= 2f && it.height >= 2f }

        return SymbolDetectionResult(
            boxes = mapped,
            inferenceTimeMs = (endNs - startNs) / 1_000_000.0,
            rawPeakCount = decoded.rawPeakCount,
            decodeThreshold = decoded.threshold
        )
    }

    /**
     * 1) Find formula ROI on a medium-size image.
     * 2) Crop original bitmap to that ROI.
     * 3) Canonicalize ROI to black ink / white background.
     * 4) Letterbox canonical ROI to 384x256.
     */
    private fun prepareInput(source: Bitmap): PreparedInput {
        val software = if (source.config == Bitmap.Config.ARGB_8888) {
            source
        } else {
            source.copy(Bitmap.Config.ARGB_8888, false)
        }

        val roi = estimateFormulaRoi(software)
        val roiBitmap = Bitmap.createBitmap(
            software,
            roi.left,
            roi.top,
            roi.width,
            roi.height
        )

        val targetW = INPUT_W - 2 * MODEL_MARGIN
        val targetH = INPUT_H - 2 * MODEL_MARGIN
        val scale = min(
            targetW.toFloat() / roi.width.toFloat(),
            targetH.toFloat() / roi.height.toFloat()
        )

        val resizedW = max(1, (roi.width * scale).roundToInt())
        val resizedH = max(1, (roi.height * scale).roundToInt())
        val padX = (INPUT_W - resizedW) * 0.5f
        val padY = (INPUT_H - resizedH) * 0.5f

        val resized = Bitmap.createScaledBitmap(roiBitmap, resizedW, resizedH, true)
        val resizedPixels = IntArray(resizedW * resizedH)
        resized.getPixels(resizedPixels, 0, resizedW, 0, 0, resizedW, resizedH)

        // Match CROHME V2 training more closely: raw grayscale camera/screen input.
        // Padding uses the ROI border median instead of pure white so letterbox bands
        // do not become artificial high-contrast structures.
        val bgR = borderMedianChannel(resizedPixels, resizedW, resizedH, 0)
        val bgG = borderMedianChannel(resizedPixels, resizedW, resizedH, 1)
        val bgB = borderMedianChannel(resizedPixels, resizedW, resizedH, 2)
        val bgGray = (299 * bgR + 587 * bgG + 114 * bgB) / 1000

        val canvasGray = IntArray(INPUT_W * INPUT_H) { bgGray }
        val inkMask = ByteArray(INPUT_W * INPUT_H) // retained only for data-class compatibility

        val x0 = padX.roundToInt()
        val y0 = padY.roundToInt()

        for (y in 0 until resizedH) {
            for (x in 0 until resizedW) {
                val src = y * resizedW + x
                val c = resizedPixels[src]
                val gray = (
                    299 * Color.red(c) +
                    587 * Color.green(c) +
                    114 * Color.blue(c)
                ) / 1000
                val dst = (y0 + y) * INPUT_W + (x0 + x)
                canvasGray[dst] = gray
            }
        }

        val bytesPerElement = if (inputType == DataType.FLOAT32) 4 else 1
        val buffer = ByteBuffer
            .allocateDirect(INPUT_H * INPUT_W * bytesPerElement)
            .order(ByteOrder.nativeOrder())

        for (value in canvasGray) {
            val real = value / 255f
            when (inputType) {
                DataType.FLOAT32 -> buffer.putFloat(real)
                DataType.UINT8 -> {
                    require(inputQuant.scale > 0f)
                    val q = (real / inputQuant.scale + inputQuant.zeroPoint)
                        .roundToInt()
                        .coerceIn(0, 255)
                    buffer.put(q.toByte())
                }
                else -> error("Unsupported detector input type: $inputType")
            }
        }
        buffer.rewind()

        return PreparedInput(
            buffer = buffer,
            inkMask = inkMask,
            scale = scale,
            padX = padX,
            padY = padY,
            resizedWidth = resizedW,
            resizedHeight = resizedH,
            roi = roi,
            originalWidth = software.width,
            originalHeight = software.height
        )
    }

    /**
     * Formula ROI estimator. It intentionally uses global border color distance
     * first because screen moire has high LOCAL contrast but usually much lower
     * distance from the background color than the actual ink.
     */
    private fun estimateFormulaRoi(source: Bitmap): FormulaRoi {
        val scale = min(
            1f,
            min(
                ANALYSIS_MAX_W.toFloat() / source.width.toFloat(),
                ANALYSIS_MAX_H.toFloat() / source.height.toFloat()
            )
        )

        val aw = max(1, (source.width * scale).roundToInt())
        val ah = max(1, (source.height * scale).roundToInt())
        val small = if (aw == source.width && ah == source.height) {
            source
        } else {
            Bitmap.createScaledBitmap(source, aw, ah, true)
        }

        val pixels = IntArray(aw * ah)
        small.getPixels(pixels, 0, aw, 0, 0, aw, ah)

        val bgR = borderMedianChannel(pixels, aw, ah, 0)
        val bgG = borderMedianChannel(pixels, aw, ah, 1)
        val bgB = borderMedianChannel(pixels, aw, ah, 2)
        val bgGray = ((299 * bgR + 587 * bgG + 114 * bgB) / 1000)

        val score = IntArray(pixels.size)
        val histogram = IntArray(256)

        for (i in pixels.indices) {
            val c = pixels[i]
            val r = Color.red(c)
            val g = Color.green(c)
            val b = Color.blue(c)
            val gray = (299 * r + 587 * g + 114 * b) / 1000

            val dr = abs(r - bgR)
            val dg = abs(g - bgG)
            val db = abs(b - bgB)
            val colorDistance = min(255, (dr + dg + db) * 2 / 3)
            val grayDistance = min(255, abs(gray - bgGray) * 2)
            val s = max(colorDistance, grayDistance)

            score[i] = s
            histogram[s]++
        }

        val otsu = otsuThreshold(histogram, score.size)
        val threshold = max(24, otsu)
        val mask = ByteArray(score.size)

        for (i in score.indices) {
            if (score[i] >= threshold) mask[i] = 1
        }

        // Zero a small analysis border. Formula crops from CameraX/Gallery are
        // expected to have some context; screen/card borders should not define ROI.
        val border = max(2, (min(aw, ah) * 0.012f).roundToInt())
        for (y in 0 until ah) {
            for (x in 0 until aw) {
                if (x < border || x >= aw - border || y < border || y >= ah - border) {
                    mask[y * aw + x] = 0
                }
            }
        }

        val components = connectedComponents(mask, aw, ah)
        if (components.isEmpty()) return fullRoi(source)

        val largest = components.maxOf { it.area }
        val minArea = max(5, (largest * 0.004f).roundToInt())

        val kept = components.filter { c ->
            c.area >= minArea &&
                !c.touchesBorder &&
                !(c.height > ah * 0.72f && c.width < aw * 0.035f) &&
                !(c.width > aw * 0.72f && c.height < ah * 0.035f)
        }

        if (kept.isEmpty()) return fullRoi(source)

        // Keep components near the dominant horizontal writing band. This is V3
        // for long/linear formulas + superscripts, not full multi-line math yet.
        val weightedCenterY = kept.sumOf { it.area.toLong() * ((it.top + it.bottom) / 2L) }
            .toDouble() / max(1L, kept.sumOf { it.area.toLong() }).toDouble()

        val bandTolerance = max(ah * 0.30f, 24f)
        val band = kept.filter { c ->
            abs(((c.top + c.bottom) * 0.5f) - weightedCenterY.toFloat()) <= bandTolerance
        }

        val finalComponents = if (band.size >= 2) band else kept

        var left = finalComponents.minOf { it.left }
        var top = finalComponents.minOf { it.top }
        var right = finalComponents.maxOf { it.right }
        var bottom = finalComponents.maxOf { it.bottom }

        val bw = right - left + 1
        val bh = bottom - top + 1
        val padX = max(5, (bw * 0.08f).roundToInt())
        val padY = max(5, (bh * 0.18f).roundToInt())

        left = max(0, left - padX)
        right = min(aw - 1, right + padX)
        top = max(0, top - padY)
        bottom = min(ah - 1, bottom + padY)

        // Avoid pathological ROI collapse.
        if ((right - left + 1) < aw * 0.18f || (bottom - top + 1) < ah * 0.08f) {
            return fullRoi(source)
        }

        val inv = 1f / scale
        val originalLeft = (left * inv).roundToInt().coerceIn(0, source.width - 1)
        val originalTop = (top * inv).roundToInt().coerceIn(0, source.height - 1)
        val originalRight = (right * inv).roundToInt().coerceIn(originalLeft, source.width - 1)
        val originalBottom = (bottom * inv).roundToInt().coerceIn(originalTop, source.height - 1)

        return FormulaRoi(originalLeft, originalTop, originalRight, originalBottom)
    }

    /**
     * Canonical ROI: near-binary dark ink on a white background.
     * This deliberately suppresses screen texture before the detector.
     */
    private fun canonicalizeRoi(bitmap: Bitmap): Pair<IntArray, ByteArray> {
        val w = bitmap.width
        val h = bitmap.height
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        val bgR = borderMedianChannel(pixels, w, h, 0)
        val bgG = borderMedianChannel(pixels, w, h, 1)
        val bgB = borderMedianChannel(pixels, w, h, 2)
        val bgGray = (299 * bgR + 587 * bgG + 114 * bgB) / 1000

        val gray = IntArray(pixels.size)
        for (i in pixels.indices) {
            val c = pixels[i]
            gray[i] = (299 * Color.red(c) + 587 * Color.green(c) + 114 * Color.blue(c)) / 1000
        }

        val localBackground = boxBlur(gray, w, h, radius = max(3, min(w, h) / 24))
        val score = IntArray(pixels.size)
        val hist = IntArray(256)

        for (i in pixels.indices) {
            val c = pixels[i]
            val r = Color.red(c)
            val g = Color.green(c)
            val b = Color.blue(c)

            val colorDistance = min(
                255,
                (abs(r - bgR) + abs(g - bgG) + abs(b - bgB)) * 2 / 3
            )
            val globalGray = min(255, abs(gray[i] - bgGray) * 2)
            val localGray = min(255, abs(gray[i] - localBackground[i]) * 2)

            // Global color/background difference dominates. Local contrast only
            // assists when illumination is uneven; it is intentionally downweighted.
            val s = max(max(colorDistance, globalGray), (localGray * 0.62f).roundToInt())
            score[i] = s
            hist[s]++
        }

        val otsu = otsuThreshold(hist, score.size)
        val p92 = percentileFromHistogram(hist, score.size, 0.92f)
        val threshold = max(26, max(otsu, (p92 * 0.58f).roundToInt()))

        val rawMask = ByteArray(score.size)
        for (i in score.indices) {
            if (score[i] >= threshold) rawMask[i] = 1
        }

        val cleaned = cleanForegroundMask(rawMask, w, h)
        val canonical = IntArray(score.size) { 255 }

        for (i in cleaned.indices) {
            if (cleaned[i].toInt() != 0) {
                // Slight grayscale edge retention for anti-aliasing, but never
                // leave the original colored/screen background in detector input.
                val darkness = (245 - min(205, score[i])).coerceIn(0, 80)
                canonical[i] = darkness
            }
        }

        return canonical to cleaned
    }

    private fun cleanForegroundMask(mask: ByteArray, w: Int, h: Int): ByteArray {
        val comps = connectedComponents(mask, w, h)
        if (comps.isEmpty()) return mask

        val largest = comps.maxOf { it.area }
        val minArea = max(3, (largest * 0.0025f).roundToInt())
        val keep = BooleanArray(comps.size)

        comps.forEachIndexed { index, c ->
            val verticalLine = c.height > h * 0.75f && c.width < w * 0.025f
            val horizontalLine = c.width > w * 0.85f && c.height < h * 0.02f
            keep[index] = c.area >= minArea && !verticalLine && !horizontalLine
        }

        // Re-label once to reconstruct only kept components.
        val out = ByteArray(mask.size)
        val visited = BooleanArray(mask.size)
        val queue = IntArray(mask.size)
        var componentIndex = 0

        for (start in mask.indices) {
            if (mask[start].toInt() == 0 || visited[start]) continue

            var head = 0
            var tail = 0
            queue[tail++] = start
            visited[start] = true
            val members = IntArrayList()

            while (head < tail) {
                val idx = queue[head++]
                members.add(idx)
                val x = idx % w
                val y = idx / w

                for (dy in -1..1) {
                    for (dx in -1..1) {
                        if (dx == 0 && dy == 0) continue
                        val nx = x + dx
                        val ny = y + dy
                        if (nx !in 0 until w || ny !in 0 until h) continue
                        val ni = ny * w + nx
                        if (!visited[ni] && mask[ni].toInt() != 0) {
                            visited[ni] = true
                            queue[tail++] = ni
                        }
                    }
                }
            }

            if (componentIndex < keep.size && keep[componentIndex]) {
                for (i in 0 until members.size) out[members[i]] = 1
            }
            componentIndex++
        }

        return out
    }

    private fun decode(
        heat: FloatArray,
        size: FloatArray,
        prepared: PreparedInput
    ): DecodeResult {
        val threshold = effectiveBaseThreshold()
        val peaks = ArrayList<Peak>()

        // Mirrors notebook decode(): threshold first, then 3x3 local maximum.
        for (gy in 0 until GRID_H) {
            for (gx in 0 until GRID_W) {
                val score = heat[heatIndex(gy, gx)]
                if (score < threshold) continue
                if (!isLocalPeak(heat, gy, gx, score)) continue

                val cx = (gx + 0.5f) * STRIDE
                val cy = (gy + 0.5f) * STRIDE
                if (!centerInsideValidImage(cx, cy, prepared)) continue

                peaks += Peak(gy, gx, score)
            }
        }

        if (peaks.isEmpty()) {
            return DecodeResult(emptyList(), 0, threshold)
        }

        val candidates = ArrayList<RawBox>()

        for (peak in peaks.sortedByDescending { it.score }) {
            val base = sizeIndex(peak.gy, peak.gx, 0)
            val boxW = size[base] * INPUT_W
            val boxH = size[base + 1] * INPUT_H

            if (!boxW.isFinite() || !boxH.isFinite()) continue
            if (boxW < 2f || boxH < 2f) continue
            if (boxW > INPUT_W * MAX_REASONABLE_BOX_WIDTH_RATIO) continue
            if (boxH > INPUT_H * MAX_REASONABLE_BOX_HEIGHT_RATIO) continue

            val cx = (peak.gx + 0.5f) * STRIDE
            val cy = (peak.gy + 0.5f) * STRIDE

            val left = (cx - boxW * 0.5f).coerceIn(0f, INPUT_W - 1f)
            val top = (cy - boxH * 0.5f).coerceIn(0f, INPUT_H - 1f)
            val right = (cx + boxW * 0.5f).coerceIn(0f, INPUT_W - 1f)
            val bottom = (cy + boxH * 0.5f).coerceIn(0f, INPUT_H - 1f)

            if (right <= left || bottom <= top) continue
            candidates += RawBox(left, top, right, bottom, peak.score)
        }

        return DecodeResult(
            boxes = nonMaximumSuppression(candidates),
            rawPeakCount = peaks.size,
            threshold = threshold
        )
    }

    private fun foregroundSupported(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        mask: ByteArray
    ): Boolean {
        val x1 = left.roundToInt().coerceIn(0, INPUT_W - 1)
        val y1 = top.roundToInt().coerceIn(0, INPUT_H - 1)
        val x2 = right.roundToInt().coerceIn(x1, INPUT_W - 1)
        val y2 = bottom.roundToInt().coerceIn(y1, INPUT_H - 1)

        var ink = 0
        var total = 0

        for (y in y1..y2) {
            for (x in x1..x2) {
                total++
                if (mask[y * INPUT_W + x].toInt() != 0) ink++
            }
        }

        if (ink < MIN_INK_PIXELS_IN_BOX || total <= 0) return false
        val density = ink.toFloat() / total.toFloat()
        return density in MIN_INK_DENSITY..MAX_INK_DENSITY
    }

    private fun effectiveBaseThreshold(): Float {
        val runtimeFloor = if (heatmapOutputType == DataType.UINT8) {
            config.minConfidenceUInt8
        } else {
            config.minConfidenceFloat
        }
        return max(config.confidenceThreshold, runtimeFloor)
    }

    private fun centerInsideValidImage(cx: Float, cy: Float, p: PreparedInput): Boolean {
        return cx >= p.padX && cx <= p.padX + p.resizedWidth &&
            cy >= p.padY && cy <= p.padY + p.resizedHeight
    }

    private fun mapBackToOriginal(box: RawBox, p: PreparedInput): DetectorBox? {
        val localLeft = ((box.left - p.padX) / p.scale).coerceIn(0f, p.roi.width - 1f)
        val localTop = ((box.top - p.padY) / p.scale).coerceIn(0f, p.roi.height - 1f)
        val localRight = ((box.right - p.padX) / p.scale).coerceIn(0f, p.roi.width - 1f)
        val localBottom = ((box.bottom - p.padY) / p.scale).coerceIn(0f, p.roi.height - 1f)

        val left = (p.roi.left + localLeft).coerceIn(0f, p.originalWidth - 1f)
        val top = (p.roi.top + localTop).coerceIn(0f, p.originalHeight - 1f)
        val right = (p.roi.left + localRight).coerceIn(0f, p.originalWidth - 1f)
        val bottom = (p.roi.top + localBottom).coerceIn(0f, p.originalHeight - 1f)

        if (right - left < 2f || bottom - top < 2f) return null
        return DetectorBox(left, top, right, bottom, box.score)
    }

    private fun nonMaximumSuppression(candidates: List<RawBox>): List<RawBox> {
        if (candidates.isEmpty()) return emptyList()
        val sorted = candidates.sortedByDescending { it.score }.toMutableList()
        val kept = ArrayList<RawBox>()

        while (sorted.isNotEmpty() && kept.size < config.maxDetections) {
            val current = sorted.removeAt(0)
            kept += current
            val it = sorted.iterator()
            while (it.hasNext()) {
                if (iou(current, it.next()) >= config.nmsIouThreshold) it.remove()
            }
        }
        return kept
    }

    private fun iou(a: RawBox, b: RawBox): Float {
        val l = max(a.left, b.left)
        val t = max(a.top, b.top)
        val r = min(a.right, b.right)
        val bt = min(a.bottom, b.bottom)
        val iw = max(0f, r - l)
        val ih = max(0f, bt - t)
        val inter = iw * ih
        val aa = max(0f, a.right - a.left) * max(0f, a.bottom - a.top)
        val ba = max(0f, b.right - b.left) * max(0f, b.bottom - b.top)
        return inter / max(aa + ba - inter, 1e-6f)
    }

    private fun isLocalPeak(heat: FloatArray, gy: Int, gx: Int, score: Float): Boolean {
        for (y in max(0, gy - 1)..min(GRID_H - 1, gy + 1)) {
            for (x in max(0, gx - 1)..min(GRID_W - 1, gx + 1)) {
                if (y == gy && x == gx) continue
                if (heat[heatIndex(y, x)] > score) return false
            }
        }
        return true
    }

    private fun heatIndex(gy: Int, gx: Int): Int = (gy * GRID_W + gx)
    private fun sizeIndex(gy: Int, gx: Int, channel: Int): Int = (gy * GRID_W + gx) * 2 + channel

    private fun readOutputFloat(buffer: ByteBuffer, outputIndex: Int): FloatArray {
        val tensor = interpreter.getOutputTensor(outputIndex)
        val dup = buffer.duplicate().order(ByteOrder.nativeOrder())
        dup.rewind()

        return when (tensor.dataType()) {
            DataType.FLOAT32 -> FloatArray(tensor.numElements()) { dup.float }
            DataType.UINT8 -> {
                val q = tensor.quantizationParams()
                require(q.scale > 0f)
                FloatArray(tensor.numElements()) {
                    val raw = dup.get().toInt() and 0xFF
                    (raw - q.zeroPoint) * q.scale
                }
            }
            else -> error("Unsupported detector output type: ${tensor.dataType()}")
        }
    }

    private fun loadConfig(): DetectorConfig {
        return try {
            val text = context.assets.open(configFileName).bufferedReader().use { it.readText() }
            val root = JSONObject(text)
            val decode = root.optJSONObject("decode")

            // CROHME V2 exports a flat config:
            // confidence_threshold=0.35, nms_iou=0.35, max_detections=128.
            // Keep backward compatibility with the earlier nested V3 config.
            val confidence = when {
                root.has("confidence_threshold") ->
                    root.optDouble("confidence_threshold", 0.35).toFloat()
                decode != null ->
                    decode.optDouble("confidence_threshold", 0.35).toFloat()
                else -> 0.35f
            }

            val nms = when {
                root.has("nms_iou") ->
                    root.optDouble("nms_iou", 0.35).toFloat()
                decode != null ->
                    decode.optDouble("nms_iou_threshold", 0.35).toFloat()
                else -> 0.35f
            }

            val maxDetections = when {
                root.has("max_detections") -> root.optInt("max_detections", 128)
                decode != null -> decode.optInt("max_detections", 128)
                else -> 128
            }.coerceIn(1, 128)

            val maxRawPeaks = when {
                decode != null -> decode.optInt("max_raw_peaks", 256)
                else -> 256
            }.coerceIn(16, 512)

            val minFloat = when {
                decode != null -> decode.optDouble("min_confidence_float", confidence.toDouble()).toFloat()
                else -> confidence
            }

            val minUInt8 = when {
                decode != null -> decode.optDouble("min_confidence_uint8", 0.50).toFloat()
                else -> 0.50f
            }

            DetectorConfig(
                confidenceThreshold = confidence,
                nmsIouThreshold = nms,
                maxDetections = maxDetections,
                maxRawPeaks = maxRawPeaks,
                minConfidenceFloat = minFloat,
                minConfidenceUInt8 = minUInt8
            )
        } catch (_: Exception) {
            DetectorConfig(
                confidenceThreshold = 0.35f,
                nmsIouThreshold = 0.35f,
                maxDetections = 128,
                maxRawPeaks = 256,
                minConfidenceFloat = 0.35f,
                minConfidenceUInt8 = 0.50f
            )
        }
    }

    private fun loadModel(): ByteBuffer {
        val bytes = context.assets.open(modelFileName).use { it.readBytes() }
        return ByteBuffer.allocateDirect(bytes.size)
            .order(ByteOrder.nativeOrder())
            .apply {
                put(bytes)
                rewind()
            }
    }

    private fun borderMedianChannel(pixels: IntArray, w: Int, h: Int, channel: Int): Int {
        val values = IntArray(2 * w + 2 * h)
        var k = 0

        fun value(c: Int): Int = when (channel) {
            0 -> Color.red(c)
            1 -> Color.green(c)
            else -> Color.blue(c)
        }

        for (x in 0 until w) {
            values[k++] = value(pixels[x])
            values[k++] = value(pixels[(h - 1) * w + x])
        }
        for (y in 0 until h) {
            values[k++] = value(pixels[y * w])
            values[k++] = value(pixels[y * w + (w - 1)])
        }

        val used = values.copyOf(k)
        used.sort()
        return used[used.size / 2]
    }

    private fun otsuThreshold(hist: IntArray, total: Int): Int {
        if (total <= 0) return 32
        var sum = 0.0
        for (i in hist.indices) sum += i.toDouble() * hist[i].toDouble()

        var sumB = 0.0
        var wB = 0
        var maxVar = -1.0
        var threshold = 32

        for (t in hist.indices) {
            wB += hist[t]
            if (wB == 0) continue
            val wF = total - wB
            if (wF <= 0) break

            sumB += t.toDouble() * hist[t].toDouble()
            val mB = sumB / wB.toDouble()
            val mF = (sum - sumB) / wF.toDouble()
            val diff = mB - mF
            val between = wB.toDouble() * wF.toDouble() * diff * diff

            if (between > maxVar) {
                maxVar = between
                threshold = t
            }
        }
        return threshold
    }

    private fun percentileFromHistogram(hist: IntArray, total: Int, p: Float): Int {
        if (total <= 0) return 0
        val target = max(1, (total * p.coerceIn(0f, 1f)).roundToInt())
        var cumulative = 0
        for (i in hist.indices) {
            cumulative += hist[i]
            if (cumulative >= target) return i
        }
        return 255
    }

    private fun boxBlur(input: IntArray, w: Int, h: Int, radius: Int): IntArray {
        if (radius <= 0) return input.copyOf()
        val horizontal = IntArray(input.size)
        val output = IntArray(input.size)

        for (y in 0 until h) {
            var sum = 0L
            for (x in -radius..radius) {
                val xx = x.coerceIn(0, w - 1)
                sum += input[y * w + xx]
            }
            for (x in 0 until w) {
                horizontal[y * w + x] = (sum / (2 * radius + 1)).toInt()
                val removeX = (x - radius).coerceIn(0, w - 1)
                val addX = (x + radius + 1).coerceIn(0, w - 1)
                sum += input[y * w + addX] - input[y * w + removeX]
            }
        }

        for (x in 0 until w) {
            var sum = 0L
            for (y in -radius..radius) {
                val yy = y.coerceIn(0, h - 1)
                sum += horizontal[yy * w + x]
            }
            for (y in 0 until h) {
                output[y * w + x] = (sum / (2 * radius + 1)).toInt()
                val removeY = (y - radius).coerceIn(0, h - 1)
                val addY = (y + radius + 1).coerceIn(0, h - 1)
                sum += horizontal[addY * w + x] - horizontal[removeY * w + x]
            }
        }
        return output
    }

    private fun connectedComponents(mask: ByteArray, w: Int, h: Int): List<Component> {
        val visited = BooleanArray(mask.size)
        val queue = IntArray(mask.size)
        val out = ArrayList<Component>()

        for (start in mask.indices) {
            if (mask[start].toInt() == 0 || visited[start]) continue

            var head = 0
            var tail = 0
            queue[tail++] = start
            visited[start] = true

            var area = 0
            var left = w
            var top = h
            var right = -1
            var bottom = -1
            var touches = false

            while (head < tail) {
                val idx = queue[head++]
                val x = idx % w
                val y = idx / w
                area++
                left = min(left, x)
                right = max(right, x)
                top = min(top, y)
                bottom = max(bottom, y)
                if (x == 0 || y == 0 || x == w - 1 || y == h - 1) touches = true

                for (dy in -1..1) {
                    for (dx in -1..1) {
                        if (dx == 0 && dy == 0) continue
                        val nx = x + dx
                        val ny = y + dy
                        if (nx !in 0 until w || ny !in 0 until h) continue
                        val ni = ny * w + nx
                        if (!visited[ni] && mask[ni].toInt() != 0) {
                            visited[ni] = true
                            queue[tail++] = ni
                        }
                    }
                }
            }

            out += Component(area, left, top, right, bottom, touches)
        }

        return out
    }

    private fun fullRoi(source: Bitmap): FormulaRoi =
        FormulaRoi(0, 0, source.width - 1, source.height - 1)

    override fun close() {
        interpreter.close()
    }

    private class IntArrayList(initialCapacity: Int = 64) {
        private var data = IntArray(initialCapacity)
        var size: Int = 0
            private set

        fun add(value: Int) {
            if (size >= data.size) data = data.copyOf(max(8, data.size * 2))
            data[size++] = value
        }

        operator fun get(index: Int): Int = data[index]
    }
}
