package com.symbolsense.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import org.json.JSONObject
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * SymbolSense isolated-symbol classifier.
 *
 * Runtime contract:
 * - 1 crop = 1 symbol
 * - input model = [1, 64, 64, 1]
 * - UINT8 input / UINT8 output
 * - exact 32 Android classes
 * - times = ID 12
 * - pi    = ID 27
 *
 * IMPORTANT:
 * Preprocessing below mirrors the semantics of Kaggle V4 `prep_camera_np()`:
 * grayscale -> polarity -> illumination flattening -> light blur -> Otsu ->
 * glyph-relative component cleanup -> tight bbox -> padding -> 64x64 letterbox.
 *
 * It intentionally uses only Android/Kotlin APIs, so OpenCV is NOT required.
 */

data class SymbolLabel(
    val id: Int,
    val name: String,
    val display: String,
    val latex: String
)

data class SymbolPrediction(
    val id: Int,
    val name: String,
    val display: String,
    val latex: String,
    val confidence: Float
)

data class RecognitionBoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    init {
        require(left in 0f..1f)
        require(top in 0f..1f)
        require(right in 0f..1f)
        require(bottom in 0f..1f)
        require(right >= left)
        require(bottom >= top)
    }
}

data class RecognizedSymbol(
    val prediction: SymbolPrediction,
    val topK: List<SymbolPrediction>,
    val detectorConfidence: Float,
    val boundingBox: RecognitionBoundingBox,
    val classifierInferenceTimeMs: Double,
    val reliable: Boolean
)

enum class RecognitionMode {
    ISOLATED,
    MULTI_SYMBOL,
    DETECTOR_FALLBACK
}

data class SymbolRecognitionResult(
    val best: SymbolPrediction,
    val topK: List<SymbolPrediction>,
    val inferenceTimeMs: Double,
    val reliable: Boolean,
    val symbols: List<RecognizedSymbol> = emptyList(),
    val structuredDisplay: String = best.display,
    val structuredLatex: String = best.latex,
    val detectorInferenceTimeMs: Double = 0.0,
    val mode: RecognitionMode = RecognitionMode.ISOLATED
)

class SymbolClassifier(
    private val context: Context,
    private val modelFileName: String = MODEL_FILE_NAME,
    private val mappingFileName: String = MAPPING_FILE_NAME
) : Closeable {

    companion object {
        private const val MODEL_FILE_NAME = "symbolsense_model_int8.tflite"
        private const val MAPPING_FILE_NAME = "class_mapping.json"

        private const val INPUT_SIZE = 64
        private const val LETTERBOX_MARGIN = 6
        private const val MAX_PREPROCESS_SIDE = 1400
        private const val MIN_CONFIDENCE = 0.60f

        /**
         * DO NOT reorder this list.
         * It must stay identical to training + class_mapping.json.
         */
        private val EXPECTED_ACTIVE_32 = listOf(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "plus", "minus", "times", "divide",
            "less_than", "greater_than", "less_equal", "greater_equal",
            "lbracket", "rbracket",
            "x", "y", "z", "a", "b",
            "sqrt", "integral", "pi", "infinity", "sum", "d", "e"
        )
    }

    private data class Component(
        val id: Int,
        val area: Int,
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int,
        val cx: Double,
        val cy: Double
    )

    private data class BBox(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int
    ) {
        val width: Int get() = right - left + 1
        val height: Int get() = bottom - top + 1
    }

    private val labels: List<SymbolLabel> = loadLabels()

    private val interpreter: Interpreter = Interpreter(
        loadModel(),
        Interpreter.Options().apply {
            setNumThreads(4)
        }
    )

    private val inputTensor = interpreter.getInputTensor(0)
    private val outputTensor = interpreter.getOutputTensor(0)

    private val inputShape = inputTensor.shape()
    private val outputShape = outputTensor.shape()

    private val inputQuantization = inputTensor.quantizationParams()
    private val outputQuantization = outputTensor.quantizationParams()

    init {
        require(labels.size == 32) {
            "class_mapping.json harus berisi exact 32 classes, actual=${labels.size}."
        }

        val actualNames = labels.map { it.name }
        require(actualNames == EXPECTED_ACTIVE_32) {
            "Urutan class_mapping.json berubah.\n" +
                "Expected=$EXPECTED_ACTIVE_32\n" +
                "Actual=$actualNames"
        }

        require(labels[12].name == "times") {
            "Android contract rusak: ID 12 harus times."
        }

        require(labels[27].name == "pi") {
            "Android contract rusak: ID 27 harus pi."
        }

        require(inputShape.contentEquals(intArrayOf(1, 64, 64, 1))) {
            "Model input tidak sesuai. Expected [1,64,64,1], " +
                "actual=${inputShape.contentToString()}"
        }

        require(inputTensor.dataType() == DataType.UINT8) {
            "Model harus UINT8 input. Actual=${inputTensor.dataType()}"
        }

        require(outputTensor.dataType() == DataType.UINT8) {
            "Model harus UINT8 output. Actual=${outputTensor.dataType()}"
        }

        require(outputShape.last() == labels.size) {
            "Jumlah output model (${outputShape.last()}) tidak sama dengan " +
                "class_mapping.json (${labels.size})."
        }

        require(inputQuantization.scale > 0f) {
            "Input quantization scale tidak valid."
        }

        require(outputQuantization.scale > 0f) {
            "Output quantization scale tidak valid."
        }
    }

    /**
     * Classify a single isolated-symbol crop.
     */
    @Synchronized
    fun classify(
        bitmap: Bitmap,
        topK: Int = 3
    ): SymbolRecognitionResult {
        val preparedPixels = preprocessCameraRobust(bitmap)
        val inputBuffer = createInputBuffer(preparedPixels)

        val output = Array(1) {
            ByteArray(labels.size)
        }

        val startNs = SystemClock.elapsedRealtimeNanos()
        interpreter.run(inputBuffer, output)
        val endNs = SystemClock.elapsedRealtimeNanos()

        val probabilities = dequantizeOutput(output[0])

        val predictions = probabilities
            .mapIndexed { index, confidence ->
                val label = labels[index]
                SymbolPrediction(
                    id = label.id,
                    name = label.name,
                    display = label.display,
                    latex = label.latex,
                    confidence = confidence
                )
            }
            .sortedByDescending { it.confidence }

        val best = predictions.first()

        return SymbolRecognitionResult(
            best = best,
            topK = predictions.take(topK.coerceIn(1, predictions.size)),
            inferenceTimeMs = (endNs - startNs) / 1_000_000.0,
            reliable = best.confidence >= MIN_CONFIDENCE
        )
    }

    /**
     * Direct CameraX / Gallery / crop Uri entry point.
     */
    fun classify(
        uri: Uri,
        topK: Int = 3
    ): SymbolRecognitionResult {
        return classify(
            bitmap = loadBitmap(uri),
            topK = topK
        )
    }


    /**
     * DEBUG ONLY.
     *
     * Returns the exact 64x64 canonical grayscale image produced by the same
     * V4 preprocessing path used immediately before TFLite inference.
     *
     * This lets the result screen show:
     * detector crop -> classifier canonical input -> classifier top-K.
     */
    fun preprocessForDebug(
        bitmap: Bitmap
    ): Bitmap {
        val pixels = preprocessCameraRobust(bitmap)

        val outPixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        for (i in pixels.indices) {
            val v = pixels[i].toInt() and 0xFF
            outPixels[i] = Color.rgb(v, v, v)
        }

        return Bitmap.createBitmap(
            outPixels,
            INPUT_SIZE,
            INPUT_SIZE,
            Bitmap.Config.ARGB_8888
        )
    }

    /**
     * SymbolSense V4 camera-robust preprocessing.
     *
     * Mirrors Kaggle `prep_camera_np()` semantics:
     * 1. grayscale
     * 2. polarity detection
     * 3. illumination/background estimation
     * 4. illumination flattening
     * 5. light Gaussian blur
     * 6. Otsu foreground threshold
     * 7. glyph-relative connected-component cleanup
     * 8. tight bbox + slight padding
     * 9. preserve gray ink around foreground
     * 10. aspect-ratio letterbox to 64x64
     *
     * Output is canonical grayscale ByteArray:
     * white background = 255, dark symbol = near 0.
     */
    private fun preprocessCameraRobust(
        sourceBitmap: Bitmap
    ): ByteArray {
        require(sourceBitmap.width > 0 && sourceBitmap.height > 0) {
            "Bitmap kosong."
        }

        val workingBitmap = makeSoftwareAndBounded(sourceBitmap)
        val width = workingBitmap.width
        val height = workingBitmap.height

        // ------------------------------------------------------------
        // 1) GRAYSCALE
        // ------------------------------------------------------------
        val gray = bitmapToGray(workingBitmap)

        // ------------------------------------------------------------
        // 2) POLARITY DETECTION
        // Python V4:
        // border_med < 127 && border_med <= center_med + 18 -> invert
        // ------------------------------------------------------------
        val borderMedian = borderMedian(gray, width, height)
        val centerMedian = centerMedian(gray, width, height)

        if (borderMedian < 127 && borderMedian <= centerMedian + 18) {
            for (i in gray.indices) {
                gray[i] = (255 - u8(gray[i])).toByte()
            }
        }

        // ------------------------------------------------------------
        // 3) ILLUMINATION / BACKGROUND ESTIMATION
        // V4 Python uses GaussianBlur with:
        // sigma = max(5, min(h,w)/16).
        //
        // Pure Kotlin implementation uses a 3-box Gaussian approximation.
        // This keeps the same low-frequency illumination-normalization intent
        // without adding a large OpenCV runtime to the Android app.
        // ------------------------------------------------------------
        val backgroundSigma = max(5.0, min(width, height) / 16.0)
        val background = gaussianBlurApprox(gray, width, height, backgroundSigma)

        // ------------------------------------------------------------
        // 4) ILLUMINATION FLATTENING
        // Python: cv2.divide(gray, background, scale=255)
        // ------------------------------------------------------------
        val flattened = ByteArray(gray.size)
        for (i in gray.indices) {
            val g = u8(gray[i])
            val bg = max(1, u8(background[i]))
            val value = ((g * 255.0) / bg)
                .roundToInt()
                .coerceIn(0, 255)
            flattened[i] = value.toByte()
        }

        // ------------------------------------------------------------
        // 5) LIGHT BLUR
        // Python V4: GaussianBlur(..., (3,3), sigmaX=0.45)
        // ------------------------------------------------------------
        val smooth = gaussian3x3(flattened, width, height, sigma = 0.45)

        // ------------------------------------------------------------
        // 6) OTSU ON INK DARKNESS
        // ink_score = 255 - flattened
        // threshold = max(10, otsu)
        // ------------------------------------------------------------
        val otsu = otsuThresholdFromInk(smooth)
        val threshold = max(10, otsu)

        var mask = ByteArray(smooth.size)
        for (i in smooth.indices) {
            val ink = 255 - u8(smooth[i])
            if (ink >= threshold) {
                mask[i] = 1
            }
        }

        // ------------------------------------------------------------
        // 7) GLYPH-RELATIVE COMPONENT CLEANUP
        // ------------------------------------------------------------
        mask = keepGlyphComponents(mask, width, height)

        var bbox = findBoundingBox(mask, width, height)

        // Conservative faint-symbol fallback from V4 notebook.
        if (bbox == null) {
            val fallbackThreshold = max(12, percentileInk(smooth, 0.92))

            val fallbackMask = ByteArray(smooth.size)
            for (i in smooth.indices) {
                val ink = 255 - u8(smooth[i])
                if (ink >= fallbackThreshold) {
                    fallbackMask[i] = 1
                }
            }

            mask = keepGlyphComponents(fallbackMask, width, height)
            bbox = findBoundingBox(mask, width, height)
        }

        if (bbox == null) {
            return ByteArray(INPUT_SIZE * INPUT_SIZE) {
                255.toByte()
            }
        }

        // ------------------------------------------------------------
        // 8) TIGHT BBOX + SLIGHT PADDING
        // Python: pad = max(2, round(0.06 * glyph_side))
        // ------------------------------------------------------------
        val rawGlyphSide = max(bbox.width, bbox.height)
        val pad = max(2, (0.06 * rawGlyphSide).roundToInt())

        val padded = BBox(
            left = max(0, bbox.left - pad),
            top = max(0, bbox.top - pad),
            right = min(width - 1, bbox.right + pad),
            bottom = min(height - 1, bbox.bottom + pad)
        )

        // ------------------------------------------------------------
        // 9) PRESERVE GRAYSCALE ONLY NEAR INK
        // Python:
        // dilate_px = max(1, round(glyph_side / 180.0))
        // canonical = where(support, flattened, 255)
        // ------------------------------------------------------------
        val dilatePx = max(1, (rawGlyphSide / 180.0).roundToInt())
        val support = dilateSquare(mask, width, height, dilatePx)

        val crop = ByteArray(padded.width * padded.height) {
            255.toByte()
        }

        for (cy in 0 until padded.height) {
            val sy = padded.top + cy
            val sourceRow = sy * width
            val cropRow = cy * padded.width

            for (cx in 0 until padded.width) {
                val sx = padded.left + cx
                val srcIndex = sourceRow + sx

                crop[cropRow + cx] = if (support[srcIndex].toInt() != 0) {
                    smooth[srcIndex]
                } else {
                    255.toByte()
                }
            }
        }

        // ------------------------------------------------------------
        // 10) LETTERBOX 64x64, margin=6
        // ------------------------------------------------------------
        return letterbox64(
            crop = crop,
            cropWidth = padded.width,
            cropHeight = padded.height
        )
    }

    /**
     * Ensures getPixels() is safe and bounds very large phone images.
     * V4 training preprocessing bounds max side to 1400.
     */
    private fun makeSoftwareAndBounded(source: Bitmap): Bitmap {
        var bitmap = if (source.config == Bitmap.Config.ARGB_8888 && !source.isHardwareBitmap()) {
            source
        } else {
            source.copy(Bitmap.Config.ARGB_8888, false)
        }

        val maxSide = max(bitmap.width, bitmap.height)
        if (maxSide > MAX_PREPROCESS_SIDE) {
            val scale = MAX_PREPROCESS_SIDE.toFloat() / maxSide.toFloat()
            val newWidth = max(1, (bitmap.width * scale).roundToInt())
            val newHeight = max(1, (bitmap.height * scale).roundToInt())

            bitmap = Bitmap.createScaledBitmap(
                bitmap,
                newWidth,
                newHeight,
                true
            )
        }

        return bitmap
    }

    private fun Bitmap.isHardwareBitmap(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            config == Bitmap.Config.HARDWARE
    }

    /**
     * ARGB Bitmap -> uint8 grayscale, compositing transparent pixels over white.
     */
    private fun bitmapToGray(bitmap: Bitmap): ByteArray {
        val width = bitmap.width
        val height = bitmap.height
        val argb = IntArray(width * height)

        bitmap.getPixels(
            argb,
            0,
            width,
            0,
            0,
            width,
            height
        )

        val gray = ByteArray(argb.size)

        for (i in argb.indices) {
            val color = argb[i]
            val alpha = Color.alpha(color)
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)

            val luminance = (
                0.299 * r +
                    0.587 * g +
                    0.114 * b
                )
                .roundToInt()
                .coerceIn(0, 255)

            // Transparent background should behave like white paper.
            val value = if (alpha >= 255) {
                luminance
            } else {
                ((luminance * alpha + 255 * (255 - alpha)) / 255.0)
                    .roundToInt()
                    .coerceIn(0, 255)
            }

            gray[i] = value.toByte()
        }

        return gray
    }

    private fun borderMedian(
        image: ByteArray,
        width: Int,
        height: Int
    ): Int {
        val histogram = IntArray(256)
        var count = 0

        for (x in 0 until width) {
            histogram[u8(image[x])]++
            histogram[u8(image[(height - 1) * width + x])]++
            count += 2
        }

        for (y in 0 until height) {
            histogram[u8(image[y * width])]++
            histogram[u8(image[y * width + width - 1])]++
            count += 2
        }

        return histogramMedian(histogram, count)
    }

    private fun centerMedian(
        image: ByteArray,
        width: Int,
        height: Int
    ): Int {
        val x0 = width / 4
        val x1 = max(x0 + 1, 3 * width / 4)
        val y0 = height / 4
        val y1 = max(y0 + 1, 3 * height / 4)

        val histogram = IntArray(256)
        var count = 0

        for (y in y0 until min(y1, height)) {
            val row = y * width
            for (x in x0 until min(x1, width)) {
                histogram[u8(image[row + x])]++
                count++
            }
        }

        if (count == 0) {
            for (value in image) {
                histogram[u8(value)]++
            }
            count = image.size
        }

        return histogramMedian(histogram, count)
    }

    private fun histogramMedian(
        histogram: IntArray,
        count: Int
    ): Int {
        if (count <= 0) return 0

        val target = (count - 1) / 2
        var cumulative = 0

        for (value in histogram.indices) {
            cumulative += histogram[value]
            if (cumulative > target) {
                return value
            }
        }

        return 255
    }

    /**
     * Three box-blurs approximate a Gaussian efficiently in O(N), even for
     * the large sigma used by illumination background estimation.
     */
    private fun gaussianBlurApprox(
        source: ByteArray,
        width: Int,
        height: Int,
        sigma: Double
    ): ByteArray {
        var current = source.copyOf()
        val boxSizes = boxesForGauss(sigma, 3)

        for (boxSize in boxSizes) {
            val radius = max(1, (boxSize - 1) / 2)
            current = boxBlur(current, width, height, radius)
        }

        return current
    }

    private fun boxesForGauss(
        sigma: Double,
        n: Int
    ): IntArray {
        val wIdeal = sqrt((12.0 * sigma * sigma / n) + 1.0)

        var wl = floor(wIdeal).toInt()
        if (wl % 2 == 0) wl--
        wl = max(1, wl)

        val wu = wl + 2

        val numerator =
            12.0 * sigma * sigma -
                n * wl * wl -
                4.0 * n * wl -
                3.0 * n

        val denominator = -4.0 * wl - 4.0
        val m = (numerator / denominator)
            .roundToInt()
            .coerceIn(0, n)

        return IntArray(n) { index ->
            if (index < m) wl else wu
        }
    }

    private fun boxBlur(
        source: ByteArray,
        width: Int,
        height: Int,
        radius: Int
    ): ByteArray {
        if (radius <= 0) return source.copyOf()

        val horizontal = ByteArray(source.size)
        val window = 2 * radius + 1

        // Horizontal pass with replicated borders.
        for (y in 0 until height) {
            val row = y * width
            var sum = 0L

            for (dx in -radius..radius) {
                val sx = dx.coerceIn(0, width - 1)
                sum += u8(source[row + sx])
            }

            for (x in 0 until width) {
                horizontal[row + x] =
                    (sum.toDouble() / window)
                        .roundToInt()
                        .coerceIn(0, 255)
                        .toByte()

                val removeX = (x - radius).coerceIn(0, width - 1)
                val addX = (x + radius + 1).coerceIn(0, width - 1)

                sum -= u8(source[row + removeX])
                sum += u8(source[row + addX])
            }
        }

        val output = ByteArray(source.size)

        // Vertical pass with replicated borders.
        for (x in 0 until width) {
            var sum = 0L

            for (dy in -radius..radius) {
                val sy = dy.coerceIn(0, height - 1)
                sum += u8(horizontal[sy * width + x])
            }

            for (y in 0 until height) {
                output[y * width + x] =
                    (sum.toDouble() / window)
                        .roundToInt()
                        .coerceIn(0, 255)
                        .toByte()

                val removeY = (y - radius).coerceIn(0, height - 1)
                val addY = (y + radius + 1).coerceIn(0, height - 1)

                sum -= u8(horizontal[removeY * width + x])
                sum += u8(horizontal[addY * width + x])
            }
        }

        return output
    }

    /**
     * 3x3 separable Gaussian equivalent in intent to cv2 GaussianBlur(3x3, 0.45).
     */
    private fun gaussian3x3(
        source: ByteArray,
        width: Int,
        height: Int,
        sigma: Double
    ): ByteArray {
        val sideWeight = exp(-1.0 / (2.0 * sigma * sigma))
        val normalization = 1.0 + 2.0 * sideWeight

        val a = sideWeight / normalization
        val b = 1.0 / normalization

        val horizontal = ByteArray(source.size)

        for (y in 0 until height) {
            val row = y * width
            for (x in 0 until width) {
                val xl = max(0, x - 1)
                val xr = min(width - 1, x + 1)

                val value =
                    a * u8(source[row + xl]) +
                        b * u8(source[row + x]) +
                        a * u8(source[row + xr])

                horizontal[row + x] = value
                    .roundToInt()
                    .coerceIn(0, 255)
                    .toByte()
            }
        }

        val output = ByteArray(source.size)

        for (y in 0 until height) {
            val yu = max(0, y - 1)
            val yd = min(height - 1, y + 1)

            for (x in 0 until width) {
                val value =
                    a * u8(horizontal[yu * width + x]) +
                        b * u8(horizontal[y * width + x]) +
                        a * u8(horizontal[yd * width + x])

                output[y * width + x] = value
                    .roundToInt()
                    .coerceIn(0, 255)
                    .toByte()
            }
        }

        return output
    }

    private fun otsuThresholdFromInk(image: ByteArray): Int {
        val histogram = IntArray(256)

        for (pixel in image) {
            val ink = 255 - u8(pixel)
            histogram[ink]++
        }

        val total = image.size.toLong()
        if (total <= 0L) return 0

        var sumAll = 0.0
        for (t in 0..255) {
            sumAll += t.toDouble() * histogram[t].toDouble()
        }

        var weightBackground = 0L
        var sumBackground = 0.0
        var bestVariance = -1.0
        var bestThreshold = 0

        for (t in 0..255) {
            weightBackground += histogram[t]
            if (weightBackground == 0L) continue

            val weightForeground = total - weightBackground
            if (weightForeground == 0L) break

            sumBackground += t.toDouble() * histogram[t].toDouble()

            val meanBackground = sumBackground / weightBackground.toDouble()
            val meanForeground =
                (sumAll - sumBackground) / weightForeground.toDouble()

            val delta = meanBackground - meanForeground
            val varianceBetween =
                weightBackground.toDouble() *
                    weightForeground.toDouble() *
                    delta * delta

            if (varianceBetween > bestVariance) {
                bestVariance = varianceBetween
                bestThreshold = t
            }
        }

        return bestThreshold
    }

    /**
     * V4 connected-component cleanup.
     * The cutoff is relative to the largest glyph component, NOT image area.
     * This avoids deleting small disconnected symbol strokes on high-res photos.
     */
    private fun keepGlyphComponents(
        mask: ByteArray,
        width: Int,
        height: Int
    ): ByteArray {
        val size = width * height
        val labels = IntArray(size)
        val queue = IntArray(size)
        val components = ArrayList<Component>()

        var nextId = 1

        for (start in 0 until size) {
            if (mask[start].toInt() == 0 || labels[start] != 0) {
                continue
            }

            var head = 0
            var tail = 0

            queue[tail++] = start
            labels[start] = nextId

            var area = 0
            var sumX = 0L
            var sumY = 0L

            var left = width
            var right = -1
            var top = height
            var bottom = -1

            while (head < tail) {
                val index = queue[head++]
                val y = index / width
                val x = index - y * width

                area++
                sumX += x
                sumY += y

                if (x < left) left = x
                if (x > right) right = x
                if (y < top) top = y
                if (y > bottom) bottom = y

                val y0 = max(0, y - 1)
                val y1 = min(height - 1, y + 1)
                val x0 = max(0, x - 1)
                val x1 = min(width - 1, x + 1)

                for (ny in y0..y1) {
                    val row = ny * width
                    for (nx in x0..x1) {
                        if (nx == x && ny == y) continue

                        val ni = row + nx
                        if (mask[ni].toInt() != 0 && labels[ni] == 0) {
                            labels[ni] = nextId
                            queue[tail++] = ni
                        }
                    }
                }
            }

            components += Component(
                id = nextId,
                area = area,
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                cx = sumX.toDouble() / area.toDouble(),
                cy = sumY.toDouble() / area.toDouble()
            )

            nextId++
        }

        if (components.isEmpty()) {
            return ByteArray(size)
        }

        val sorted = components.sortedByDescending { it.area }
        val largest = sorted.first().area
        val minRelativeArea = max(2, (largest * 0.006).roundToInt())

        var candidateComponents = sorted
            .take(16)
            .filter { it.area >= minRelativeArea }

        if (candidateComponents.isEmpty()) {
            candidateComponents = listOf(sorted.first())
        }

        val unionLeft = candidateComponents.minOf { it.left }
        val unionTop = candidateComponents.minOf { it.top }
        val unionRight = candidateComponents.maxOf { it.right }
        val unionBottom = candidateComponents.maxOf { it.bottom }

        val diagonal = max(
            1.0,
            hypot(
                (unionRight - unionLeft + 1).toDouble(),
                (unionBottom - unionTop + 1).toDouble()
            )
        )

        val centerX = candidateComponents.map { it.cx }.average()
        val centerY = candidateComponents.map { it.cy }.average()

        var keep = sorted
            .take(24)
            .filter { component ->
                component.area >= minRelativeArea &&
                    hypot(
                        component.cx - centerX,
                        component.cy - centerY
                    ) <= 1.35 * diagonal
            }

        if (keep.isEmpty()) {
            keep = candidateComponents
        }

        val keepIds = BooleanArray(nextId + 1)
        for (component in keep) {
            keepIds[component.id] = true
        }

        val filtered = ByteArray(size)
        for (i in labels.indices) {
            val label = labels[i]
            if (label > 0 && label < keepIds.size && keepIds[label]) {
                filtered[i] = 1
            }
        }

        return filtered
    }

    private fun findBoundingBox(
        mask: ByteArray,
        width: Int,
        height: Int
    ): BBox? {
        var left = width
        var right = -1
        var top = height
        var bottom = -1

        for (y in 0 until height) {
            val row = y * width
            for (x in 0 until width) {
                if (mask[row + x].toInt() == 0) continue

                if (x < left) left = x
                if (x > right) right = x
                if (y < top) top = y
                if (y > bottom) bottom = y
            }
        }

        return if (right >= left && bottom >= top) {
            BBox(left, top, right, bottom)
        } else {
            null
        }
    }

    private fun percentileInk(
        image: ByteArray,
        percentile: Double
    ): Int {
        val histogram = IntArray(256)

        for (pixel in image) {
            histogram[255 - u8(pixel)]++
        }

        val p = percentile.coerceIn(0.0, 1.0)
        val target = ceil(p * image.size.toDouble())
            .toLong()
            .coerceAtLeast(1L)

        var cumulative = 0L
        for (value in 0..255) {
            cumulative += histogram[value].toLong()
            if (cumulative >= target) {
                return value
            }
        }

        return 255
    }

    /**
     * Square binary dilation using two O(N) sliding-window passes.
     */
    private fun dilateSquare(
        mask: ByteArray,
        width: Int,
        height: Int,
        radius: Int
    ): ByteArray {
        if (radius <= 0) return mask.copyOf()

        val horizontal = ByteArray(mask.size)

        for (y in 0 until height) {
            val row = y * width
            var count = 0

            for (dx in -radius..radius) {
                val sx = dx.coerceIn(0, width - 1)
                if (mask[row + sx].toInt() != 0) count++
            }

            for (x in 0 until width) {
                if (count > 0) horizontal[row + x] = 1

                val removeX = (x - radius).coerceIn(0, width - 1)
                val addX = (x + radius + 1).coerceIn(0, width - 1)

                if (mask[row + removeX].toInt() != 0) count--
                if (mask[row + addX].toInt() != 0) count++
            }
        }

        val output = ByteArray(mask.size)

        for (x in 0 until width) {
            var count = 0

            for (dy in -radius..radius) {
                val sy = dy.coerceIn(0, height - 1)
                if (horizontal[sy * width + x].toInt() != 0) count++
            }

            for (y in 0 until height) {
                if (count > 0) output[y * width + x] = 1

                val removeY = (y - radius).coerceIn(0, height - 1)
                val addY = (y + radius + 1).coerceIn(0, height - 1)

                if (horizontal[removeY * width + x].toInt() != 0) count--
                if (horizontal[addY * width + x].toInt() != 0) count++
            }
        }

        return output
    }

    private fun letterbox64(
        crop: ByteArray,
        cropWidth: Int,
        cropHeight: Int
    ): ByteArray {
        val usable = max(1, INPUT_SIZE - 2 * LETTERBOX_MARGIN)

        val scale = min(
            usable.toDouble() / max(1, cropWidth).toDouble(),
            usable.toDouble() / max(1, cropHeight).toDouble()
        )

        val newWidth = max(1, (cropWidth * scale).roundToInt())
        val newHeight = max(1, (cropHeight * scale).roundToInt())

        val resized = if (scale < 1.0) {
            resizeGrayArea(
                source = crop,
                sourceWidth = cropWidth,
                sourceHeight = cropHeight,
                targetWidth = newWidth,
                targetHeight = newHeight
            )
        } else {
            resizeGrayBilinear(
                source = crop,
                sourceWidth = cropWidth,
                sourceHeight = cropHeight,
                targetWidth = newWidth,
                targetHeight = newHeight
            )
        }

        val final = ByteArray(INPUT_SIZE * INPUT_SIZE) {
            255.toByte()
        }

        val x0 = (INPUT_SIZE - newWidth) / 2
        val y0 = (INPUT_SIZE - newHeight) / 2

        for (y in 0 until newHeight) {
            val sourceRow = y * newWidth
            val targetRow = (y0 + y) * INPUT_SIZE + x0

            System.arraycopy(
                resized,
                sourceRow,
                final,
                targetRow,
                newWidth
            )
        }

        return final
    }

    /**
     * Approximation of cv2.INTER_AREA for downscaling.
     * Since target is at most 52x52, direct region averaging is inexpensive.
     */
    private fun resizeGrayArea(
        source: ByteArray,
        sourceWidth: Int,
        sourceHeight: Int,
        targetWidth: Int,
        targetHeight: Int
    ): ByteArray {
        if (sourceWidth == targetWidth && sourceHeight == targetHeight) {
            return source.copyOf()
        }

        val output = ByteArray(targetWidth * targetHeight)

        for (ty in 0 until targetHeight) {
            val sy0 = floor(ty * sourceHeight.toDouble() / targetHeight)
                .toInt()
                .coerceIn(0, sourceHeight - 1)

            val sy1 = ceil((ty + 1) * sourceHeight.toDouble() / targetHeight)
                .toInt()
                .coerceIn(sy0 + 1, sourceHeight)

            for (tx in 0 until targetWidth) {
                val sx0 = floor(tx * sourceWidth.toDouble() / targetWidth)
                    .toInt()
                    .coerceIn(0, sourceWidth - 1)

                val sx1 = ceil((tx + 1) * sourceWidth.toDouble() / targetWidth)
                    .toInt()
                    .coerceIn(sx0 + 1, sourceWidth)

                var sum = 0L
                var count = 0

                for (sy in sy0 until sy1) {
                    val row = sy * sourceWidth
                    for (sx in sx0 until sx1) {
                        sum += u8(source[row + sx])
                        count++
                    }
                }

                val value = if (count > 0) {
                    (sum.toDouble() / count.toDouble())
                        .roundToInt()
                        .coerceIn(0, 255)
                } else {
                    255
                }

                output[ty * targetWidth + tx] = value.toByte()
            }
        }

        return output
    }

    private fun resizeGrayBilinear(
        source: ByteArray,
        sourceWidth: Int,
        sourceHeight: Int,
        targetWidth: Int,
        targetHeight: Int
    ): ByteArray {
        if (sourceWidth == targetWidth && sourceHeight == targetHeight) {
            return source.copyOf()
        }

        val output = ByteArray(targetWidth * targetHeight)

        val xScale = if (targetWidth > 1) {
            (sourceWidth - 1).toDouble() / (targetWidth - 1).toDouble()
        } else {
            0.0
        }

        val yScale = if (targetHeight > 1) {
            (sourceHeight - 1).toDouble() / (targetHeight - 1).toDouble()
        } else {
            0.0
        }

        for (ty in 0 until targetHeight) {
            val sy = ty * yScale
            val y0 = floor(sy).toInt().coerceIn(0, sourceHeight - 1)
            val y1 = min(sourceHeight - 1, y0 + 1)
            val wy = sy - y0

            for (tx in 0 until targetWidth) {
                val sx = tx * xScale
                val x0 = floor(sx).toInt().coerceIn(0, sourceWidth - 1)
                val x1 = min(sourceWidth - 1, x0 + 1)
                val wx = sx - x0

                val p00 = u8(source[y0 * sourceWidth + x0]).toDouble()
                val p01 = u8(source[y0 * sourceWidth + x1]).toDouble()
                val p10 = u8(source[y1 * sourceWidth + x0]).toDouble()
                val p11 = u8(source[y1 * sourceWidth + x1]).toDouble()

                val top = p00 * (1.0 - wx) + p01 * wx
                val bottom = p10 * (1.0 - wx) + p11 * wx
                val value = top * (1.0 - wy) + bottom * wy

                output[ty * targetWidth + tx] = value
                    .roundToInt()
                    .coerceIn(0, 255)
                    .toByte()
            }
        }

        return output
    }

    /**
     * Training model receives float pixel/255.0 and the exported model is UINT8.
     * Therefore we quantize canonical grayscale using the TFLite tensor parameters.
     */
    private fun createInputBuffer(
        pixels: ByteArray
    ): ByteBuffer {
        require(pixels.size == INPUT_SIZE * INPUT_SIZE) {
            "Prepared image harus 64x64, actual bytes=${pixels.size}."
        }

        val buffer = ByteBuffer
            .allocateDirect(INPUT_SIZE * INPUT_SIZE)
            .order(ByteOrder.nativeOrder())

        val scale = inputQuantization.scale
        val zeroPoint = inputQuantization.zeroPoint

        for (pixelByte in pixels) {
            val pixel = u8(pixelByte)
            val realValue = pixel / 255.0f

            val quantized = (
                realValue / scale + zeroPoint
                )
                .roundToInt()
                .coerceIn(0, 255)

            buffer.put(quantized.toByte())
        }

        buffer.rewind()
        return buffer
    }

    private fun dequantizeOutput(
        output: ByteArray
    ): FloatArray {
        val scale = outputQuantization.scale
        val zeroPoint = outputQuantization.zeroPoint

        return FloatArray(output.size) { index ->
            val quantized = u8(output[index])
            ((quantized - zeroPoint) * scale)
                .coerceIn(0f, 1f)
        }
    }

    private fun loadLabels(): List<SymbolLabel> {
        val jsonString = context.assets
            .open(mappingFileName)
            .bufferedReader()
            .use { it.readText() }

        val root = JSONObject(jsonString)
        val classes = root.getJSONArray("classes")
        val result = mutableListOf<SymbolLabel>()

        for (index in 0 until classes.length()) {
            val item = classes.getJSONObject(index)

            result += SymbolLabel(
                id = item.getInt("id"),
                name = item.getString("name"),
                display = item.getString("display"),
                latex = item.getString("latex")
            )
        }

        val sorted = result.sortedBy { it.id }

        require(sorted.map { it.id } == (0 until sorted.size).toList()) {
            "class_mapping.json ID harus kontigu mulai dari 0."
        }

        return sorted
    }

    private fun loadModel(): ByteBuffer {
        val modelBytes = context.assets
            .open(modelFileName)
            .use { it.readBytes() }

        return ByteBuffer
            .allocateDirect(modelBytes.size)
            .order(ByteOrder.nativeOrder())
            .apply {
                put(modelBytes)
                rewind()
            }
    }

    @Suppress("DEPRECATION")
    private fun loadBitmap(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(
                context.contentResolver,
                uri
            )

            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                // Required because hardware Bitmap cannot be read with getPixels().
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                uri
            )
        }
    }

    private fun u8(value: Byte): Int {
        return value.toInt() and 0xFF
    }

    override fun close() {
        interpreter.close()
    }
}
