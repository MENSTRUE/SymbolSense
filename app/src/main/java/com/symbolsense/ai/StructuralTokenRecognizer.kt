package com.symbolsense.ai

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Recognizes structural tokens that are intentionally NOT part of the exact
 * 32-class isolated classifier taxonomy.
 *
 * V2 scope: '=' only.
 *
 * This does not mutate class_mapping.json and does not add a 33rd classifier
 * class. It is a separate deterministic structural stage after detection.
 */
object StructuralTokenRecognizer {

    private const val EQUAL_ID = -100

    fun recognize(bitmap: Bitmap): SymbolPrediction? {
        return recognizeEqual(bitmap)
    }

    fun isMeaningfulCrop(bitmap: Bitmap): Boolean {
        val gray = resizeGray(bitmap, 96, 96)
        val background = borderMedian(gray, 96, 96)

        val contrast = IntArray(gray.size) {
            abs(gray[it] - background)
        }

        val p90 = percentile(contrast, 0.90f)
        val threshold = max(14, (p90 * 0.55f).roundToInt())
        val foreground = contrast.count { it >= threshold }
        val ratio = foreground.toFloat() / contrast.size.toFloat()

        return p90 >= 14 && ratio in 0.008f..0.62f
    }

    private fun recognizeEqual(bitmap: Bitmap): SymbolPrediction? {
        val w = 96
        val h = 96
        val gray = resizeGray(bitmap, w, h)
        val background = borderMedian(gray, w, h)
        val contrast = IntArray(gray.size) {
            abs(gray[it] - background)
        }

        val p90 = percentile(contrast, 0.90f)
        val p97 = percentile(contrast, 0.97f)
        if (p97 < 18) return null

        val threshold = max(
            16,
            ((p90 + p97) * 0.30f).roundToInt()
        )

        val rowCount = IntArray(h)
        val rowMinX = IntArray(h) { w }
        val rowMaxX = IntArray(h) { -1 }

        for (y in 0 until h) {
            for (x in 0 until w) {
                val c = contrast[y * w + x]
                if (c >= threshold) {
                    rowCount[y]++
                    rowMinX[y] = min(rowMinX[y], x)
                    rowMaxX[y] = max(rowMaxX[y], x)
                }
            }
        }

        // Smooth the projection slightly.
        val smooth = IntArray(h)
        for (y in 0 until h) {
            var sum = 0
            var n = 0
            for (yy in max(0, y - 1)..min(h - 1, y + 1)) {
                sum += rowCount[yy]
                n++
            }
            smooth[y] = sum / max(1, n)
        }

        val rowThreshold = (w * 0.34f).roundToInt()
        val bands = mutableListOf<IntRange>()
        var start = -1

        for (y in 0 until h) {
            val active = smooth[y] >= rowThreshold

            if (active && start < 0) {
                start = y
            }

            if ((!active || y == h - 1) && start >= 0) {
                val end = if (active && y == h - 1) y else y - 1
                bands += start..end
                start = -1
            }
        }

        if (bands.size < 2) return null

        data class Band(
            val y1: Int,
            val y2: Int,
            val x1: Int,
            val x2: Int,
            val strength: Int
        ) {
            val centerY: Float get() = (y1 + y2) * 0.5f
            val width: Int get() = x2 - x1 + 1
        }

        val candidates = bands.mapNotNull { range ->
            var x1 = w
            var x2 = -1
            var strength = 0

            for (y in range) {
                strength += smooth[y]
                if (rowMaxX[y] >= 0) {
                    x1 = min(x1, rowMinX[y])
                    x2 = max(x2, rowMaxX[y])
                }
            }

            if (x2 < x1) null
            else Band(range.first, range.last, x1, x2, strength)
        }.sortedByDescending { it.strength }

        if (candidates.size < 2) return null

        var bestScore = 0f

        for (i in 0 until min(candidates.size, 5)) {
            for (j in i + 1 until min(candidates.size, 5)) {
                val a = candidates[i]
                val b = candidates[j]

                val upper = if (a.centerY < b.centerY) a else b
                val lower = if (a.centerY < b.centerY) b else a

                val verticalGap = lower.centerY - upper.centerY
                if (verticalGap < h * 0.10f || verticalGap > h * 0.55f) continue

                val minWidth = min(upper.width, lower.width).toFloat()
                val maxWidth = max(upper.width, lower.width).toFloat()
                if (minWidth < w * 0.32f) continue

                val widthSimilarity = minWidth / max(maxWidth, 1f)
                if (widthSimilarity < 0.62f) continue

                val overlap = max(
                    0,
                    min(upper.x2, lower.x2) - max(upper.x1, lower.x1) + 1
                ).toFloat()

                val overlapRatio = overlap / max(minWidth, 1f)
                if (overlapRatio < 0.62f) continue

                val score = (
                    0.55f * widthSimilarity +
                        0.45f * overlapRatio
                    ).coerceIn(0f, 1f)

                bestScore = max(bestScore, score)
            }
        }

        if (bestScore < 0.68f) return null

        return SymbolPrediction(
            id = EQUAL_ID,
            name = "equal",
            display = "=",
            latex = "=",
            confidence = (0.82f + 0.16f * bestScore).coerceAtMost(0.98f)
        )
    }

    private fun resizeGray(
        source: Bitmap,
        outW: Int,
        outH: Int
    ): IntArray {
        val scaled = Bitmap.createScaledBitmap(source, outW, outH, true)
        val pixels = IntArray(outW * outH)
        scaled.getPixels(pixels, 0, outW, 0, 0, outW, outH)

        return IntArray(pixels.size) { i ->
            val c = pixels[i]
            (
                0.299 * Color.red(c) +
                    0.587 * Color.green(c) +
                    0.114 * Color.blue(c)
                ).roundToInt().coerceIn(0, 255)
        }
    }

    private fun borderMedian(
        gray: IntArray,
        w: Int,
        h: Int
    ): Int {
        val values = ArrayList<Int>()

        for (x in 0 until w) {
            values += gray[x]
            values += gray[(h - 1) * w + x]
        }

        for (y in 0 until h) {
            values += gray[y * w]
            values += gray[y * w + (w - 1)]
        }

        values.sort()
        return values[values.size / 2]
    }

    private fun percentile(values: IntArray, p: Float): Int {
        val hist = IntArray(256)
        for (v in values) hist[v.coerceIn(0, 255)]++

        val target = (values.size * p.coerceIn(0f, 1f))
            .roundToInt()
            .coerceAtLeast(1)

        var cumulative = 0
        for (i in hist.indices) {
            cumulative += hist[i]
            if (cumulative >= target) return i
        }

        return 255
    }
}
