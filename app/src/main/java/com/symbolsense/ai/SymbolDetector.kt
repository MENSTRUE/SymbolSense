package com.symbolsense.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
 * SymbolSense multi-symbol detector runtime V2.
 *
 * Fixes vs V1 Android runtime:
 * 1. Supports FLOAT32 (FP16-weight TFLite) as the preferred detector runtime.
 * 2. Still supports UINT8 detector models for debugging/backward compatibility.
 * 3. Formula-wide camera preprocessing canonicalizes BOTH dark-on-light and
 *    light/colored-on-dark strokes into dark ink on a white background.
 * 4. Adaptive peak threshold prevents the decoder from blindly hitting the
 *    old maxDetections=64 ceiling on real phone/screen images.
 * 5. Runtime guards reject peaks/boxes in letterbox padding and obvious
 *    geometry outliers before NMS.
 *
 * The detector remains class-agnostic. Classification stays in SymbolClassifier.
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
        // Generic name: the supplied patch uses the FP16-weight detector.
        private const val MODEL_FILE_NAME = "symbolsense_detector_model.tflite"
        private const val CONFIG_FILE_NAME = "detector_config.json"

        private const val INPUT_H = 256
        private const val INPUT_W = 384
        private const val GRID_H = 64
        private const val GRID_W = 96
        private const val STRIDE = 4

        private const val PEAK_SCAN_FLOOR = 0.12f
        private const val MAX_REASONABLE_BOX_WIDTH_RATIO = 0.78f
        private const val MAX_REASONABLE_BOX_HEIGHT_RATIO = 0.86f
        private const val EDGE_GUARD_PX = 3
    }

    private data class DetectorConfig(
        val confidenceThreshold: Float,
        val nmsIouThreshold: Float,
        val maxDetections: Int,
        val maxRawPeaks: Int,
        val minConfidenceFloat: Float,
        val minConfidenceUInt8: Float
    )

    private data class PreparedInput(
        val buffer: ByteBuffer,
        val scale: Float,
        val padX: Float,
        val padY: Float,
        val resizedWidth: Int,
        val resizedHeight: Int,
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

    private val config = loadConfig()

    private val interpreter = Interpreter(
        loadModel(),
        Interpreter.Options().apply {
            setNumThreads(4)
        }
    )

    private val inputTensor = interpreter.getInputTensor(0)
    private val inputType = inputTensor.dataType()
    private val inputQuant = inputTensor.quantizationParams()

    private val heatmapOutputIndex: Int
    private val sizeOutputIndex: Int
    private val heatmapOutputType: DataType

    init {
        require(inputTensor.shape().contentEquals(intArrayOf(1, INPUT_H, INPUT_W, 1))) {
            "Detector input mismatch. Expected [1,$INPUT_H,$INPUT_W,1], " +
                "actual=${inputTensor.shape().contentToString()}"
        }

        require(inputType == DataType.FLOAT32 || inputType == DataType.UINT8) {
            "Detector input must be FLOAT32 or UINT8, actual=$inputType"
        }

        if (inputType == DataType.UINT8) {
            require(inputQuant.scale > 0f) {
                "Detector UINT8 input quantization invalid."
            }
        }

        var heatIndex = -1
        var sizeIndex = -1

        for (index in 0 until interpreter.outputTensorCount) {
            val tensor = interpreter.getOutputTensor(index)
            val shape = tensor.shape()

            require(
                tensor.dataType() == DataType.FLOAT32 ||
                    tensor.dataType() == DataType.UINT8
            ) {
                "Detector output $index must be FLOAT32/UINT8, actual=${tensor.dataType()}"
            }

            require(
                shape.size == 4 &&
                    shape[0] == 1 &&
                    shape[1] == GRID_H &&
                    shape[2] == GRID_W
            ) {
                "Unknown detector output shape: ${shape.contentToString()}"
            }

            when (shape[3]) {
                1 -> heatIndex = index
                2 -> sizeIndex = index
            }
        }

        require(heatIndex >= 0) {
            "Heatmap output [1,$GRID_H,$GRID_W,1] not found."
        }
        require(sizeIndex >= 0) {
            "Size output [1,$GRID_H,$GRID_W,2] not found."
        }

        heatmapOutputIndex = heatIndex
        sizeOutputIndex = sizeIndex
        heatmapOutputType = interpreter.getOutputTensor(heatmapOutputIndex).dataType()
    }

    @Synchronized
    fun detect(bitmap: Bitmap): SymbolDetectionResult {
        require(bitmap.width > 0 && bitmap.height > 0) {
            "Bitmap detector is empty."
        }

        val prepared = prepareInput(bitmap)

        val outputs = mutableMapOf<Int, Any>()
        for (index in 0 until interpreter.outputTensorCount) {
            val tensor = interpreter.getOutputTensor(index)
            outputs[index] = ByteBuffer
                .allocateDirect(tensor.numBytes())
                .order(ByteOrder.nativeOrder())
        }

        val startNs = SystemClock.elapsedRealtimeNanos()
        interpreter.runForMultipleInputsOutputs(
            arrayOf(prepared.buffer),
            outputs
        )
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
     * Formula-wide preprocessing that preserves geometry.
     *
     * Instead of assuming black strokes on a white page, it estimates a local
     * background and converts absolute local contrast into dark ink on white.
     * This handles examples such as yellow/white symbols on a green screen.
     */
    private fun prepareInput(source: Bitmap): PreparedInput {
        val software = if (source.config == Bitmap.Config.ARGB_8888) {
            source
        } else {
            source.copy(Bitmap.Config.ARGB_8888, false)
        }

        val originalW = software.width
        val originalH = software.height

        val scale = min(
            INPUT_W.toFloat() / originalW.toFloat(),
            INPUT_H.toFloat() / originalH.toFloat()
        )

        val resizedW = max(1, (originalW * scale).roundToInt())
        val resizedH = max(1, (originalH * scale).roundToInt())
        val padX = (INPUT_W - resizedW) * 0.5f
        val padY = (INPUT_H - resizedH) * 0.5f

        val letterbox = Bitmap.createBitmap(
            INPUT_W,
            INPUT_H,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(letterbox)
        canvas.drawColor(Color.WHITE)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isFilterBitmap = true
        }

        canvas.drawBitmap(
            software,
            null,
            RectF(
                padX,
                padY,
                padX + resizedW,
                padY + resizedH
            ),
            paint
        )

        val pixels = IntArray(INPUT_W * INPUT_H)
        letterbox.getPixels(
            pixels,
            0,
            INPUT_W,
            0,
            0,
            INPUT_W,
            INPUT_H
        )

        val validLeft = padX.roundToInt().coerceIn(0, INPUT_W - 1)
        val validTop = padY.roundToInt().coerceIn(0, INPUT_H - 1)
        val validRight = (validLeft + resizedW - 1).coerceIn(0, INPUT_W - 1)
        val validBottom = (validTop + resizedH - 1).coerceIn(0, INPUT_H - 1)

        val gray = IntArray(INPUT_W * INPUT_H)
        for (i in pixels.indices) {
            gray[i] = luminance(pixels[i])
        }

        // Prevent the white letterbox pad from creating a false contrast edge.
        val borderMedian = validBorderMedian(
            gray,
            validLeft,
            validTop,
            validRight,
            validBottom
        )

        for (y in 0 until INPUT_H) {
            for (x in 0 until INPUT_W) {
                if (
                    x < validLeft || x > validRight ||
                    y < validTop || y > validBottom
                ) {
                    gray[y * INPUT_W + x] = borderMedian
                }
            }
        }

        val smooth = boxBlur(gray, INPUT_W, INPUT_H, radius = 1)
        val background = boxBlur(smooth, INPUT_W, INPUT_H, radius = 15)
        val contrast = IntArray(INPUT_W * INPUT_H)

        val histogram = IntArray(256)
        var validCount = 0

        for (y in validTop..validBottom) {
            for (x in validLeft..validRight) {
                val idx = y * INPUT_W + x
                val c = abs(smooth[idx] - background[idx]).coerceIn(0, 255)
                contrast[idx] = c

                if (
                    x >= validLeft + EDGE_GUARD_PX &&
                    x <= validRight - EDGE_GUARD_PX &&
                    y >= validTop + EDGE_GUARD_PX &&
                    y <= validBottom - EDGE_GUARD_PX
                ) {
                    histogram[c]++
                    validCount++
                }
            }
        }

        val p75 = percentileFromHistogram(histogram, validCount, 0.75f)
        val p98 = percentileFromHistogram(histogram, validCount, 0.98f)

        val low = max(8, p75)
        val high = max(low + 20, p98)
        val denom = max(1, high - low).toFloat()

        val canonical = IntArray(INPUT_W * INPUT_H) { 255 }

        for (y in validTop..validBottom) {
            for (x in validLeft..validRight) {
                val idx = y * INPUT_W + x

                if (
                    x < validLeft + EDGE_GUARD_PX ||
                    x > validRight - EDGE_GUARD_PX ||
                    y < validTop + EDGE_GUARD_PX ||
                    y > validBottom - EDGE_GUARD_PX
                ) {
                    canonical[idx] = 255
                    continue
                }

                val strength = ((contrast[idx] - low) / denom)
                    .coerceIn(0f, 1f)

                // A slight non-linear boost suppresses weak screen texture while
                // keeping high-contrast handwriting dark.
                val boosted = strength * strength * (3f - 2f * strength)
                canonical[idx] = (255f * (1f - boosted))
                    .roundToInt()
                    .coerceIn(0, 255)
            }
        }

        val bytesPerElement = if (inputType == DataType.FLOAT32) 4 else 1
        val buffer = ByteBuffer
            .allocateDirect(INPUT_H * INPUT_W * bytesPerElement)
            .order(ByteOrder.nativeOrder())

        for (value in canonical) {
            val real = value / 255f

            when (inputType) {
                DataType.FLOAT32 -> buffer.putFloat(real)

                DataType.UINT8 -> {
                    val q = (
                        real / inputQuant.scale + inputQuant.zeroPoint
                        ).roundToInt().coerceIn(0, 255)
                    buffer.put(q.toByte())
                }

                else -> error("Unsupported detector input type: $inputType")
            }
        }

        buffer.rewind()

        return PreparedInput(
            buffer = buffer,
            scale = scale,
            padX = padX,
            padY = padY,
            resizedWidth = resizedW,
            resizedHeight = resizedH,
            originalWidth = originalW,
            originalHeight = originalH
        )
    }

    private fun decode(
        heat: FloatArray,
        size: FloatArray,
        prepared: PreparedInput
    ): DecodeResult {
        val peaks = ArrayList<Peak>()

        for (gy in 0 until GRID_H) {
            for (gx in 0 until GRID_W) {
                val score = heat[heatIndex(gy, gx)]
                if (score < PEAK_SCAN_FLOOR) continue
                if (!isLocalPeak(heat, gy, gx, score)) continue

                val cx = (gx + 0.5f) * STRIDE
                val cy = (gy + 0.5f) * STRIDE

                if (!centerInsideValidImage(cx, cy, prepared)) continue

                peaks += Peak(gy, gx, score)
            }
        }

        if (peaks.isEmpty()) {
            return DecodeResult(
                boxes = emptyList(),
                rawPeakCount = 0,
                threshold = effectiveBaseThreshold()
            )
        }

        val sortedPeaks = peaks.sortedByDescending { it.score }
        val topScore = sortedPeaks.first().score

        // Keras FP16 stays close to training; UINT8 needs a higher safety floor.
        var threshold = max(
            effectiveBaseThreshold(),
            topScore * 0.36f
        )

        var selected = sortedPeaks.filter { it.score >= threshold }

        // Hard guard against the exact failure mode seen on device: the old
        // decoder returned 64 background peaks. Raise threshold adaptively so
        // only the strongest plausible peaks survive.
        if (selected.size > config.maxRawPeaks) {
            val rankScore = selected[config.maxRawPeaks - 1].score
            threshold = max(threshold, rankScore)
            selected = selected.filter { it.score + 1e-7f >= threshold }
                .take(config.maxRawPeaks)
        }

        val candidates = ArrayList<RawBox>()

        for (peak in selected) {
            val sizeBase = sizeIndex(peak.gy, peak.gx, 0)
            val boxW = size[sizeBase] * INPUT_W
            val boxH = size[sizeBase + 1] * INPUT_H

            if (boxW < 3f || boxH < 3f) continue
            if (boxW > INPUT_W * MAX_REASONABLE_BOX_WIDTH_RATIO) continue
            if (boxH > INPUT_H * MAX_REASONABLE_BOX_HEIGHT_RATIO) continue

            val centerX = (peak.gx + 0.5f) * STRIDE
            val centerY = (peak.gy + 0.5f) * STRIDE

            val validLeft = prepared.padX
            val validTop = prepared.padY
            val validRight = prepared.padX + prepared.resizedWidth
            val validBottom = prepared.padY + prepared.resizedHeight

            val left = (centerX - boxW * 0.5f).coerceIn(validLeft, validRight)
            val top = (centerY - boxH * 0.5f).coerceIn(validTop, validBottom)
            val right = (centerX + boxW * 0.5f).coerceIn(validLeft, validRight)
            val bottom = (centerY + boxH * 0.5f).coerceIn(validTop, validBottom)

            if (right - left < 3f || bottom - top < 3f) continue

            candidates += RawBox(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                score = peak.score
            )
        }

        return DecodeResult(
            boxes = nonMaximumSuppression(candidates),
            rawPeakCount = peaks.size,
            threshold = threshold
        )
    }

    private fun effectiveBaseThreshold(): Float {
        val runtimeFloor = if (heatmapOutputType == DataType.UINT8) {
            config.minConfidenceUInt8
        } else {
            config.minConfidenceFloat
        }

        return max(config.confidenceThreshold, runtimeFloor)
    }

    private fun centerInsideValidImage(
        centerX: Float,
        centerY: Float,
        prepared: PreparedInput
    ): Boolean {
        return centerX >= prepared.padX &&
            centerX <= prepared.padX + prepared.resizedWidth &&
            centerY >= prepared.padY &&
            centerY <= prepared.padY + prepared.resizedHeight
    }

    private fun isLocalPeak(
        heat: FloatArray,
        gy: Int,
        gx: Int,
        score: Float
    ): Boolean {
        val y1 = max(0, gy - 1)
        val y2 = min(GRID_H - 1, gy + 1)
        val x1 = max(0, gx - 1)
        val x2 = min(GRID_W - 1, gx + 1)

        for (y in y1..y2) {
            for (x in x1..x2) {
                if (y == gy && x == gx) continue
                if (heat[heatIndex(y, x)] > score) return false
            }
        }
        return true
    }

    private fun nonMaximumSuppression(candidates: List<RawBox>): List<RawBox> {
        if (candidates.isEmpty()) return emptyList()

        val sorted = candidates.sortedByDescending { it.score }.toMutableList()
        val kept = ArrayList<RawBox>()

        while (sorted.isNotEmpty() && kept.size < config.maxDetections) {
            val current = sorted.removeAt(0)
            kept += current

            val iterator = sorted.iterator()
            while (iterator.hasNext()) {
                val other = iterator.next()
                if (iou(current, other) >= config.nmsIouThreshold) {
                    iterator.remove()
                }
            }
        }

        return kept
    }

    private fun mapBackToOriginal(
        box: RawBox,
        prepared: PreparedInput
    ): DetectorBox? {
        val left = ((box.left - prepared.padX) / prepared.scale)
            .coerceIn(0f, prepared.originalWidth - 1f)
        val top = ((box.top - prepared.padY) / prepared.scale)
            .coerceIn(0f, prepared.originalHeight - 1f)
        val right = ((box.right - prepared.padX) / prepared.scale)
            .coerceIn(0f, prepared.originalWidth - 1f)
        val bottom = ((box.bottom - prepared.padY) / prepared.scale)
            .coerceIn(0f, prepared.originalHeight - 1f)

        if (right - left < 2f || bottom - top < 2f) return null

        return DetectorBox(
            leftPx = left,
            topPx = top,
            rightPx = right,
            bottomPx = bottom,
            confidence = box.score
        )
    }

    private fun readOutputFloat(
        buffer: ByteBuffer,
        outputIndex: Int
    ): FloatArray {
        val tensor = interpreter.getOutputTensor(outputIndex)
        val duplicate = buffer.duplicate().order(ByteOrder.nativeOrder())
        duplicate.rewind()

        return when (tensor.dataType()) {
            DataType.FLOAT32 -> {
                FloatArray(tensor.numElements()) {
                    duplicate.float
                }
            }

            DataType.UINT8 -> {
                val quant = tensor.quantizationParams()
                require(quant.scale > 0f) {
                    "Output quantization invalid for tensor $outputIndex"
                }

                FloatArray(tensor.numElements()) {
                    val q = duplicate.get().toInt() and 0xFF
                    (q - quant.zeroPoint) * quant.scale
                }
            }

            else -> error(
                "Unsupported detector output type: ${tensor.dataType()}"
            )
        }
    }

    private fun heatIndex(y: Int, x: Int): Int = y * GRID_W + x

    private fun sizeIndex(y: Int, x: Int, channel: Int): Int {
        return (y * GRID_W + x) * 2 + channel
    }

    private fun iou(a: RawBox, b: RawBox): Float {
        val left = max(a.left, b.left)
        val top = max(a.top, b.top)
        val right = min(a.right, b.right)
        val bottom = min(a.bottom, b.bottom)

        val interW = max(0f, right - left)
        val interH = max(0f, bottom - top)
        val intersection = interW * interH

        val areaA = max(0f, a.right - a.left) * max(0f, a.bottom - a.top)
        val areaB = max(0f, b.right - b.left) * max(0f, b.bottom - b.top)
        val union = areaA + areaB - intersection

        return if (union <= 0f) 0f else intersection / union
    }

    private fun loadConfig(): DetectorConfig {
        val json = context.assets.open(configFileName)
            .bufferedReader()
            .use { it.readText() }

        val root = JSONObject(json)
        val shape = root.getJSONArray("input_shape")

        require(shape.length() == 4)
        require(shape.getInt(0) == 1)
        require(shape.getInt(1) == INPUT_H)
        require(shape.getInt(2) == INPUT_W)
        require(shape.getInt(3) == 1)
        require(root.getInt("stride") == STRIDE)

        val decode = root.getJSONObject("decode")

        return DetectorConfig(
            confidenceThreshold = decode
                .optDouble("confidence_threshold", 0.38)
                .toFloat(),
            nmsIouThreshold = decode
                .optDouble("nms_iou_threshold", 0.30)
                .toFloat(),
            maxDetections = decode
                .optInt("max_detections", 32)
                .coerceIn(1, 48),
            maxRawPeaks = decode
                .optInt("runtime_max_raw_peaks", 28)
                .coerceIn(4, 48),
            minConfidenceFloat = decode
                .optDouble("runtime_min_confidence_float", 0.36)
                .toFloat(),
            minConfidenceUInt8 = decode
                .optDouble("runtime_min_confidence_uint8", 0.50)
                .toFloat()
        )
    }

    private fun loadModel(): ByteBuffer {
        val modelBytes = context.assets.open(modelFileName).use { it.readBytes() }
        return ByteBuffer
            .allocateDirect(modelBytes.size)
            .order(ByteOrder.nativeOrder())
            .apply {
                put(modelBytes)
                rewind()
            }
    }

    private fun luminance(color: Int): Int {
        return (
            0.299 * Color.red(color) +
                0.587 * Color.green(color) +
                0.114 * Color.blue(color)
            ).roundToInt().coerceIn(0, 255)
    }

    private fun validBorderMedian(
        gray: IntArray,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ): Int {
        val values = ArrayList<Int>()
        val samples = 96

        for (i in 0 until samples) {
            val fx = i.toFloat() / (samples - 1).toFloat()
            val x = (left + (right - left) * fx).roundToInt().coerceIn(left, right)
            val y = (top + (bottom - top) * fx).roundToInt().coerceIn(top, bottom)

            values += gray[top * INPUT_W + x]
            values += gray[bottom * INPUT_W + x]
            values += gray[y * INPUT_W + left]
            values += gray[y * INPUT_W + right]
        }

        if (values.isEmpty()) return 255
        values.sort()
        return values[values.size / 2]
    }

    /** Fast edge-clamped box blur using an integral image. */
    private fun boxBlur(
        src: IntArray,
        width: Int,
        height: Int,
        radius: Int
    ): IntArray {
        if (radius <= 0) return src.copyOf()

        val integralWidth = width + 1
        val integral = LongArray((width + 1) * (height + 1))

        for (y in 0 until height) {
            var rowSum = 0L
            for (x in 0 until width) {
                rowSum += src[y * width + x].toLong()
                integral[(y + 1) * integralWidth + (x + 1)] =
                    integral[y * integralWidth + (x + 1)] + rowSum
            }
        }

        val out = IntArray(width * height)

        for (y in 0 until height) {
            val y1 = max(0, y - radius)
            val y2 = min(height - 1, y + radius)

            for (x in 0 until width) {
                val x1 = max(0, x - radius)
                val x2 = min(width - 1, x + radius)

                val a = integral[y1 * integralWidth + x1]
                val b = integral[y1 * integralWidth + (x2 + 1)]
                val c = integral[(y2 + 1) * integralWidth + x1]
                val d = integral[(y2 + 1) * integralWidth + (x2 + 1)]

                val sum = d - b - c + a
                val count = (x2 - x1 + 1) * (y2 - y1 + 1)
                out[y * width + x] = (sum / count).toInt().coerceIn(0, 255)
            }
        }

        return out
    }

    private fun percentileFromHistogram(
        histogram: IntArray,
        count: Int,
        percentile: Float
    ): Int {
        if (count <= 0) return 0

        val target = (count * percentile.coerceIn(0f, 1f))
            .roundToInt()
            .coerceAtLeast(1)

        var cumulative = 0
        for (value in histogram.indices) {
            cumulative += histogram[value]
            if (cumulative >= target) return value
        }

        return 255
    }

    override fun close() {
        interpreter.close()
    }
}
