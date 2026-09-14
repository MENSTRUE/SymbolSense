package com.symbolsense.ai

import android.graphics.Bitmap
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * V5.6 scan-quality guard.
 *
 * Tujuan:
 * - jangan berharap user selalu memotret dengan framing ideal;
 * - nilai framing dari hasil detector;
 * - jika formula terlalu kecil, izinkan FormulaRecognizer melakukan
 *   auto-crop + detector retry sekali;
 * - jika hasil memang tidak layak, kirim alasan yang bisa dipahami UI.
 *
 * Penting:
 * - evaluator ini TIDAK mengubah label simbol;
 * - evaluator ini TIDAK mengganti classifier / detector model;
 * - semua ukuran box di sini memakai pixel dari gambar yang sedang dievaluasi.
 */
enum class ScanQualityIssue {
    GOOD,
    TOO_FAR,
    TOO_CLOSE,
    CUT_OFF,
    LOW_CONFIDENCE,
    NO_SYMBOLS
}

data class ScanQualityBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val confidence: Float
) {
    val width: Float
        get() = (right - left).coerceAtLeast(0f)

    val height: Float
        get() = (bottom - top).coerceAtLeast(0f)
}

data class ScanQualityReport(
    val issue: ScanQualityIssue,
    val userTitle: String,
    val userMessage: String,
    val canAutoRecover: Boolean,
    val boxCount: Int,
    val meanDetectorConfidence: Float,
    val medianSymbolHeightRatio: Float,
    val formulaWidthRatio: Float,
    val formulaHeightRatio: Float,
    val formulaLeftRatio: Float,
    val formulaTopRatio: Float,
    val formulaRightRatio: Float,
    val formulaBottomRatio: Float
)

data class ScanRecoveryRegion(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) {
    val width: Int
        get() = right - left

    val height: Int
        get() = bottom - top
}

/**
 * Exception ini sengaja membawa report terstruktur supaya ProcessingScreen
 * bisa memberi instruksi yang spesifik, bukan cuma "AI gagal".
 */
class ScanQualityException(
    val report: ScanQualityReport
) : IllegalStateException(
    report.userMessage
)

object ScanQualityEvaluator {

    /*
     * Detector V5.4 mengeluarkan candidate mulai 0.35.
     * Gunakan threshold yang sama untuk membaca geometri scan.
     */
    private const val BASE_BOX_CONFIDENCE = 0.35f

    /*
     * Threshold dibuat konservatif supaya scan yang sebenarnya masih bisa
     * dikenali tidak ditolak hanya karena framing sedikit berbeda.
     */
    private const val TOO_FAR_MEDIAN_HEIGHT = 0.075f
    private const val TOO_CLOSE_MEDIAN_HEIGHT = 0.50f
    private const val LOW_MEAN_CONFIDENCE = 0.43f

    private const val EDGE_MARGIN_RATIO = 0.012f

    fun evaluate(
        imageWidth: Int,
        imageHeight: Int,
        boxes: List<ScanQualityBox>
    ): ScanQualityReport {

        if (
            imageWidth <= 0 ||
            imageHeight <= 0
        ) {
            return noSymbolsReport()
        }

        val usable = boxes
            .filter {
                it.confidence >= BASE_BOX_CONFIDENCE &&
                    it.width > 1f &&
                    it.height > 1f
            }

        if (usable.isEmpty()) {
            return noSymbolsReport()
        }

        val widthF = imageWidth.toFloat()
        val heightF = imageHeight.toFloat()

        val left = usable.minOf { it.left }
            .coerceIn(0f, widthF)

        val top = usable.minOf { it.top }
            .coerceIn(0f, heightF)

        val right = usable.maxOf { it.right }
            .coerceIn(0f, widthF)

        val bottom = usable.maxOf { it.bottom }
            .coerceIn(0f, heightF)

        val formulaWidthRatio =
            ((right - left) / widthF)
                .coerceIn(0f, 1f)

        val formulaHeightRatio =
            ((bottom - top) / heightF)
                .coerceIn(0f, 1f)

        val leftRatio =
            (left / widthF)
                .coerceIn(0f, 1f)

        val topRatio =
            (top / heightF)
                .coerceIn(0f, 1f)

        val rightRatio =
            (right / widthF)
                .coerceIn(0f, 1f)

        val bottomRatio =
            (bottom / heightF)
                .coerceIn(0f, 1f)

        val symbolHeightRatios = usable
            .map {
                (it.height / heightF)
                    .coerceIn(0f, 1f)
            }
            .sorted()

        val medianHeight =
            median(symbolHeightRatios)

        val meanConfidence =
            usable
                .map { it.confidence }
                .average()
                .toFloat()
                .coerceIn(0f, 1f)

        val touchesEdge =
            leftRatio <= EDGE_MARGIN_RATIO ||
                topRatio <= EDGE_MARGIN_RATIO ||
                rightRatio >= 1f - EDGE_MARGIN_RATIO ||
                bottomRatio >= 1f - EDGE_MARGIN_RATIO

        /*
         * CUT_OFF hanya dianggap serius jika formula juga cukup besar.
         * Formula kecil yang kebetulan berada dekat tepi jangan langsung ditolak.
         */
        val likelyCutOff =
            touchesEdge &&
                (
                    formulaWidthRatio >= 0.62f ||
                        formulaHeightRatio >= 0.48f
                    )

        if (likelyCutOff) {
            return report(
                issue = ScanQualityIssue.CUT_OFF,
                title = "Sebagian rumus mungkin terpotong",
                message = "Pastikan seluruh rumus terlihat dan tidak menyentuh tepi gambar. Ambil ulang dengan sedikit ruang di sekeliling rumus.",
                canAutoRecover = false,
                boxCount = usable.size,
                meanConfidence = meanConfidence,
                medianHeight = medianHeight,
                formulaWidthRatio = formulaWidthRatio,
                formulaHeightRatio = formulaHeightRatio,
                leftRatio = leftRatio,
                topRatio = topRatio,
                rightRatio = rightRatio,
                bottomRatio = bottomRatio
            )
        }

        if (medianHeight >= TOO_CLOSE_MEDIAN_HEIGHT) {
            return report(
                issue = ScanQualityIssue.TOO_CLOSE,
                title = "Rumus terlalu dekat",
                message = "Mundurkan kamera sedikit agar setiap simbol dan ruang di sekeliling rumus tetap terlihat.",
                canAutoRecover = false,
                boxCount = usable.size,
                meanConfidence = meanConfidence,
                medianHeight = medianHeight,
                formulaWidthRatio = formulaWidthRatio,
                formulaHeightRatio = formulaHeightRatio,
                leftRatio = leftRatio,
                topRatio = topRatio,
                rightRatio = rightRatio,
                bottomRatio = bottomRatio
            )
        }

        val compactFormula =
            formulaWidthRatio < 0.34f &&
                formulaHeightRatio < 0.28f

        if (
            medianHeight <= TOO_FAR_MEDIAN_HEIGHT ||
            (
                medianHeight < 0.11f &&
                    compactFormula
                )
        ) {
            return report(
                issue = ScanQualityIssue.TOO_FAR,
                title = "Rumus terlihat terlalu kecil",
                message = "SymbolSense akan mencoba memperbesar area rumus secara otomatis. Jika masih gagal, dekatkan kamera sedikit.",
                canAutoRecover = true,
                boxCount = usable.size,
                meanConfidence = meanConfidence,
                medianHeight = medianHeight,
                formulaWidthRatio = formulaWidthRatio,
                formulaHeightRatio = formulaHeightRatio,
                leftRatio = leftRatio,
                topRatio = topRatio,
                rightRatio = rightRatio,
                bottomRatio = bottomRatio
            )
        }

        if (meanConfidence < LOW_MEAN_CONFIDENCE) {
            return report(
                issue = ScanQualityIssue.LOW_CONFIDENCE,
                title = "Rumus belum terbaca dengan jelas",
                message = "Coba sejajarkan kamera, pastikan gambar tidak blur, dan beri pencahayaan yang cukup.",
                canAutoRecover = true,
                boxCount = usable.size,
                meanConfidence = meanConfidence,
                medianHeight = medianHeight,
                formulaWidthRatio = formulaWidthRatio,
                formulaHeightRatio = formulaHeightRatio,
                leftRatio = leftRatio,
                topRatio = topRatio,
                rightRatio = rightRatio,
                bottomRatio = bottomRatio
            )
        }

        return report(
            issue = ScanQualityIssue.GOOD,
            title = "Framing baik",
            message = "Rumus berada pada ukuran dan posisi yang layak untuk diproses.",
            canAutoRecover = false,
            boxCount = usable.size,
            meanConfidence = meanConfidence,
            medianHeight = medianHeight,
            formulaWidthRatio = formulaWidthRatio,
            formulaHeightRatio = formulaHeightRatio,
            leftRatio = leftRatio,
            topRatio = topRatio,
            rightRatio = rightRatio,
            bottomRatio = bottomRatio
        )
    }

    /**
     * Bangun crop area formula dari gabungan bbox detector.
     *
     * Ini adalah "auto zoom" secara aman:
     * - bukan mengubah zoom CameraX;
     * - bukan mengarang pixel yang hilang;
     * - hanya memotong ROI formula yang sudah ada lalu menjalankan detector
     *   lagi pada ROI tersebut.
     */
    fun buildRecoveryRegion(
        bitmap: Bitmap,
        boxes: List<ScanQualityBox>,
        marginRatio: Float = 0.28f
    ): ScanRecoveryRegion? {

        val usable = boxes.filter {
            it.confidence >= BASE_BOX_CONFIDENCE &&
                it.width > 1f &&
                it.height > 1f
        }

        if (usable.isEmpty()) {
            return null
        }

        val rawLeft = usable.minOf { it.left }
        val rawTop = usable.minOf { it.top }
        val rawRight = usable.maxOf { it.right }
        val rawBottom = usable.maxOf { it.bottom }

        val rawWidth =
            (rawRight - rawLeft)
                .coerceAtLeast(1f)

        val rawHeight =
            (rawBottom - rawTop)
                .coerceAtLeast(1f)

        val margin =
            max(
                rawWidth,
                rawHeight
            ) *
                marginRatio
                    .coerceIn(
                        0.10f,
                        0.50f
                    )

        val left =
            floor(
                rawLeft - margin
            )
                .toInt()
                .coerceIn(
                    0,
                    bitmap.width - 1
                )

        val top =
            floor(
                rawTop - margin
            )
                .toInt()
                .coerceIn(
                    0,
                    bitmap.height - 1
                )

        val right =
            ceil(
                rawRight + margin
            )
                .toInt()
                .coerceIn(
                    left + 1,
                    bitmap.width
                )

        val bottom =
            ceil(
                rawBottom + margin
            )
                .toInt()
                .coerceIn(
                    top + 1,
                    bitmap.height
                )

        val region =
            ScanRecoveryRegion(
                left = left,
                top = top,
                right = right,
                bottom = bottom
            )

        /*
         * Kalau crop hampir sebesar gambar asli, retry tidak memberi zoom
         * yang berarti.
         */
        val areaRatio =
            (
                region.width.toFloat() *
                    region.height.toFloat()
                ) /
                (
                    bitmap.width.toFloat() *
                        bitmap.height.toFloat()
                    )

        if (
            areaRatio >= 0.88f ||
            region.width < 8 ||
            region.height < 8
        ) {
            return null
        }

        return region
    }

    fun crop(
        bitmap: Bitmap,
        region: ScanRecoveryRegion
    ): Bitmap {
        return Bitmap.createBitmap(
            bitmap,
            region.left,
            region.top,
            region.width,
            region.height
        )
    }

    fun noSymbolsReport(): ScanQualityReport {
        return ScanQualityReport(
            issue = ScanQualityIssue.NO_SYMBOLS,
            userTitle = "Rumus belum terdeteksi",
            userMessage = "Tidak ada simbol yang cukup jelas untuk dipindai. Pastikan seluruh rumus terlihat, tidak blur, dan tidak terlalu dekat atau terlalu jauh.",
            canAutoRecover = false,
            boxCount = 0,
            meanDetectorConfidence = 0f,
            medianSymbolHeightRatio = 0f,
            formulaWidthRatio = 0f,
            formulaHeightRatio = 0f,
            formulaLeftRatio = 0f,
            formulaTopRatio = 0f,
            formulaRightRatio = 0f,
            formulaBottomRatio = 0f
        )
    }

    fun lowRecognitionReport(
        base: ScanQualityReport
    ): ScanQualityReport {
        if (base.issue != ScanQualityIssue.GOOD) {
            return base
        }

        return base.copy(
            issue = ScanQualityIssue.LOW_CONFIDENCE,
            userTitle = "Hasil pemindaian belum meyakinkan",
            userMessage = "Simbol terdeteksi, tetapi hasil pengenalannya belum cukup stabil. Coba ambil ulang dengan kamera sejajar dan sedikit ruang di sekeliling rumus.",
            canAutoRecover = false
        )
    }

    private fun report(
        issue: ScanQualityIssue,
        title: String,
        message: String,
        canAutoRecover: Boolean,
        boxCount: Int,
        meanConfidence: Float,
        medianHeight: Float,
        formulaWidthRatio: Float,
        formulaHeightRatio: Float,
        leftRatio: Float,
        topRatio: Float,
        rightRatio: Float,
        bottomRatio: Float
    ): ScanQualityReport {
        return ScanQualityReport(
            issue = issue,
            userTitle = title,
            userMessage = message,
            canAutoRecover = canAutoRecover,
            boxCount = boxCount,
            meanDetectorConfidence = meanConfidence,
            medianSymbolHeightRatio = medianHeight,
            formulaWidthRatio = formulaWidthRatio,
            formulaHeightRatio = formulaHeightRatio,
            formulaLeftRatio = leftRatio,
            formulaTopRatio = topRatio,
            formulaRightRatio = rightRatio,
            formulaBottomRatio = bottomRatio
        )
    }

    private fun median(
        values: List<Float>
    ): Float {
        if (values.isEmpty()) {
            return 0f
        }

        val sorted =
            values.sorted()

        val middle =
            sorted.size / 2

        return if (
            sorted.size % 2 == 1
        ) {
            sorted[middle]
        } else {
            (
                sorted[middle - 1] +
                    sorted[middle]
                ) *
                0.5f
        }
    }
}
