package com.symbolsense.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Paint
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
import kotlin.math.min
import kotlin.math.roundToInt

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

data class SymbolRecognitionResult(
    val best: SymbolPrediction,
    val topK: List<SymbolPrediction>,
    val inferenceTimeMs: Double,
    val reliable: Boolean
)

class SymbolClassifier(
    private val context: Context,
    private val modelFileName: String = "symbolsense_v2_int8.tflite",
    private val mappingFileName: String = "class_mapping.json"
) : Closeable {

    companion object {
        private const val INPUT_SIZE = 64

        // Sama dengan preprocessing Kaggle.
        private const val MARGIN = 5

        // Sementara untuk baseline.
        private const val MIN_CONFIDENCE = 0.60f
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
        require(inputShape.contentEquals(intArrayOf(1, 64, 64, 1))) {
            "Model input tidak sesuai. Expected [1,64,64,1], " +
                    "actual=${inputShape.contentToString()}"
        }

        require(inputTensor.dataType() == DataType.UINT8) {
            "Model yang dipasang bukan UINT8 model. " +
                    "Actual=${inputTensor.dataType()}"
        }

        require(outputTensor.dataType() == DataType.UINT8) {
            "Output model bukan UINT8. " +
                    "Actual=${outputTensor.dataType()}"
        }

        val outputClasses = outputShape.last()

        require(outputClasses == labels.size) {
            "Jumlah output model ($outputClasses) " +
                    "tidak sama dengan class_mapping.json (${labels.size})."
        }

        require(inputQuantization.scale > 0f) {
            "Input quantization scale tidak valid."
        }

        require(outputQuantization.scale > 0f) {
            "Output quantization scale tidak valid."
        }
    }

    /**
     * Untuk hasil crop yang sudah berupa Bitmap.
     */
    @Synchronized
    fun classify(
        bitmap: Bitmap,
        topK: Int = 3
    ): SymbolRecognitionResult {

        val preparedPixels = preprocess(bitmap)

        val inputBuffer = createInputBuffer(preparedPixels)

        val output = Array(1) {
            ByteArray(labels.size)
        }

        val startNs = SystemClock.elapsedRealtimeNanos()

        interpreter.run(
            inputBuffer,
            output
        )

        val endNs = SystemClock.elapsedRealtimeNanos()

        val probabilities = dequantizeOutput(
            output[0]
        )

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
            .sortedByDescending {
                it.confidence
            }

        val best = predictions.first()

        return SymbolRecognitionResult(
            best = best,
            topK = predictions.take(
                topK.coerceIn(
                    1,
                    predictions.size
                )
            ),
            inferenceTimeMs =
                (endNs - startNs) / 1_000_000.0,
            reliable =
                best.confidence >= MIN_CONFIDENCE
        )
    }

    /**
     * Bisa langsung menerima Uri hasil Camera/Gallery/Crop.
     */
    fun classify(
        uri: Uri,
        topK: Int = 3
    ): SymbolRecognitionResult {

        val bitmap = loadBitmap(uri)

        return classify(
            bitmap = bitmap,
            topK = topK
        )
    }

    /**
     * Preprocessing ini mengikuti training Kaggle:
     *
     * grayscale
     * ↓
     * white background / black ink
     * ↓
     * crop foreground
     * ↓
     * preserve aspect ratio
     * ↓
     * margin
     * ↓
     * 64x64
     *
     * OUTPUT:
     * ByteArray grayscale 0..255
     */
    private fun preprocess(
        sourceBitmap: Bitmap
    ): ByteArray {

        val bitmap = if (
            sourceBitmap.config == Bitmap.Config.ARGB_8888
        ) {
            sourceBitmap
        } else {
            sourceBitmap.copy(
                Bitmap.Config.ARGB_8888,
                false
            )
        }

        val width = bitmap.width
        val height = bitmap.height

        require(width > 0 && height > 0) {
            "Bitmap kosong."
        }

        val sourcePixels = IntArray(
            width * height
        )

        bitmap.getPixels(
            sourcePixels,
            0,
            width,
            0,
            0,
            width,
            height
        )

        val gray = IntArray(
            width * height
        )

        for (i in sourcePixels.indices) {

            val color = sourcePixels[i]

            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)

            // Standard luminance conversion.
            gray[i] = (
                    0.299f * r +
                            0.587f * g +
                            0.114f * b
                    )
                .roundToInt()
                .coerceIn(
                    0,
                    255
                )
        }

        // ------------------------------------------------
        // DETECT BACKGROUND POLARITY
        // Sama dengan Python:
        // border.mean() < 127 -> invert
        // ------------------------------------------------

        var borderSum = 0L
        var borderCount = 0

        // top + bottom
        for (x in 0 until width) {

            borderSum += gray[x]

            borderSum += gray[
                (height - 1) * width + x
            ]

            borderCount += 2
        }

        // left + right
        for (y in 0 until height) {

            borderSum += gray[
                y * width
            ]

            borderSum += gray[
                y * width + (width - 1)
            ]

            borderCount += 2
        }

        val borderMean =
            borderSum.toFloat() /
                    borderCount.toFloat()

        if (borderMean < 127f) {

            for (i in gray.indices) {
                gray[i] = 255 - gray[i]
            }
        }

        // ------------------------------------------------
        // FIND FOREGROUND BOUNDING BOX
        // Python:
        // foreground = image < 245
        // ------------------------------------------------

        var minX = width
        var maxX = -1

        var minY = height
        var maxY = -1

        for (y in 0 until height) {

            for (x in 0 until width) {

                val value = gray[
                    y * width + x
                ]

                if (value < 245) {

                    if (x < minX) minX = x
                    if (x > maxX) maxX = x

                    if (y < minY) minY = y
                    if (y > maxY) maxY = y
                }
            }
        }

        // Tidak ada foreground.
        if (
            maxX < minX ||
            maxY < minY
        ) {
            return ByteArray(
                INPUT_SIZE * INPUT_SIZE
            ) {
                255.toByte()
            }
        }

        val cropWidth =
            maxX - minX + 1

        val cropHeight =
            maxY - minY + 1

        val cropPixels = IntArray(
            cropWidth * cropHeight
        )

        for (y in 0 until cropHeight) {

            for (x in 0 until cropWidth) {

                val value = gray[
                    (minY + y) * width +
                            (minX + x)
                ]

                cropPixels[
                    y * cropWidth + x
                ] = Color.rgb(
                    value,
                    value,
                    value
                )
            }
        }

        val cropBitmap = Bitmap.createBitmap(
            cropPixels,
            cropWidth,
            cropHeight,
            Bitmap.Config.ARGB_8888
        )

        // ------------------------------------------------
        // RESIZE WITH ASPECT RATIO
        // ------------------------------------------------

        val usable =
            INPUT_SIZE -
                    (2 * MARGIN)

        val scale = min(
            usable.toFloat() /
                    cropWidth.toFloat(),
            usable.toFloat() /
                    cropHeight.toFloat()
        )

        val newWidth = (
                cropWidth * scale
                )
            .roundToInt()
            .coerceAtLeast(1)

        val newHeight = (
                cropHeight * scale
                )
            .roundToInt()
            .coerceAtLeast(1)

        val resizedBitmap =
            Bitmap.createScaledBitmap(
                cropBitmap,
                newWidth,
                newHeight,
                true
            )

        // ------------------------------------------------
        // WHITE 64x64 CANVAS
        // ------------------------------------------------

        val resultBitmap =
            Bitmap.createBitmap(
                INPUT_SIZE,
                INPUT_SIZE,
                Bitmap.Config.ARGB_8888
            )

        val canvas = Canvas(
            resultBitmap
        )

        canvas.drawColor(
            Color.WHITE
        )

        val offsetX =
            (INPUT_SIZE - newWidth) /
                    2f

        val offsetY =
            (INPUT_SIZE - newHeight) /
                    2f

        val paint = Paint(
            Paint.ANTI_ALIAS_FLAG or
                    Paint.FILTER_BITMAP_FLAG
        )

        canvas.drawBitmap(
            resizedBitmap,
            offsetX,
            offsetY,
            paint
        )

        // ------------------------------------------------
        // RETURN GRAYSCALE BYTE ARRAY
        // ------------------------------------------------

        val resultPixels =
            IntArray(
                INPUT_SIZE * INPUT_SIZE
            )

        resultBitmap.getPixels(
            resultPixels,
            0,
            INPUT_SIZE,
            0,
            0,
            INPUT_SIZE,
            INPUT_SIZE
        )

        return ByteArray(
            INPUT_SIZE * INPUT_SIZE
        ) { index ->

            val color =
                resultPixels[index]

            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)

            val value = (
                    0.299f * r +
                            0.587f * g +
                            0.114f * b
                    )
                .roundToInt()
                .coerceIn(
                    0,
                    255
                )

            value.toByte()
        }
    }

    /**
     * Real value saat training:
     *
     * pixel / 255.0
     *
     * Lalu dikonversi ke UINT8 sesuai
     * quantization parameter model.
     */
    private fun createInputBuffer(
        pixels: ByteArray
    ): ByteBuffer {

        val buffer =
            ByteBuffer.allocateDirect(
                INPUT_SIZE *
                        INPUT_SIZE
            )
                .order(
                    ByteOrder.nativeOrder()
                )

        val scale =
            inputQuantization.scale

        val zeroPoint =
            inputQuantization.zeroPoint

        for (pixelByte in pixels) {

            val pixel =
                pixelByte.toInt() and 0xFF

            val realValue =
                pixel / 255.0f

            val quantized =
                (
                        realValue /
                                scale +
                                zeroPoint
                        )
                    .roundToInt()
                    .coerceIn(
                        0,
                        255
                    )

            buffer.put(
                quantized.toByte()
            )
        }

        buffer.rewind()

        return buffer
    }

    private fun dequantizeOutput(
        output: ByteArray
    ): FloatArray {

        val scale =
            outputQuantization.scale

        val zeroPoint =
            outputQuantization.zeroPoint

        return FloatArray(
            output.size
        ) { index ->

            val quantized =
                output[index]
                    .toInt() and 0xFF

            (
                    quantized -
                            zeroPoint
                    ) * scale
        }
    }

    private fun loadLabels():
            List<SymbolLabel> {

        val jsonString =
            context.assets
                .open(
                    mappingFileName
                )
                .bufferedReader()
                .use {
                    it.readText()
                }

        val root =
            JSONObject(
                jsonString
            )

        val classes =
            root.getJSONArray(
                "classes"
            )

        val result =
            mutableListOf<SymbolLabel>()

        for (
        index in
        0 until classes.length()
        ) {

            val item =
                classes.getJSONObject(
                    index
                )

            result +=
                SymbolLabel(
                    id =
                        item.getInt(
                            "id"
                        ),

                    name =
                        item.getString(
                            "name"
                        ),

                    display =
                        item.getString(
                            "display"
                        ),

                    latex =
                        item.getString(
                            "latex"
                        )
                )
        }

        return result.sortedBy {
            it.id
        }
    }

    private fun loadModel():
            ByteBuffer {

        val modelBytes =
            context.assets
                .open(
                    modelFileName
                )
                .use {
                    it.readBytes()
                }

        return ByteBuffer
            .allocateDirect(
                modelBytes.size
            )
            .order(
                ByteOrder.nativeOrder()
            )
            .apply {

                put(
                    modelBytes
                )

                rewind()
            }
    }

    @Suppress("DEPRECATION")
    private fun loadBitmap(
        uri: Uri
    ): Bitmap {

        return if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.P
        ) {

            val source =
                ImageDecoder
                    .createSource(
                        context.contentResolver,
                        uri
                    )

            ImageDecoder.decodeBitmap(
                source
            ) {
                    decoder,
                    _,
                    _ ->

                // getPixels() tidak bisa dipakai
                // pada hardware Bitmap.
                decoder.allocator =
                    ImageDecoder.ALLOCATOR_SOFTWARE
            }

        } else {

            MediaStore.Images.Media
                .getBitmap(
                    context.contentResolver,
                    uri
                )
        }
    }

    override fun close() {
        interpreter.close()
    }
}