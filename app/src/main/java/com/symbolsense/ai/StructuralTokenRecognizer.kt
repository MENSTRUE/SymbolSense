package com.symbolsense.ai

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Separate structural recognizer for tokens intentionally outside the exact
 * 32-class classifier. V3 scope: '=' only.
 *
 * IMPORTANT: this recognizer is intentionally conservative. It is only called
 * after classifier inference and should not turn arbitrary textured crops into '='.
 */
object StructuralTokenRecognizer {

    private const val EQUAL_ID = -100

    fun recognizeEqual(bitmap: Bitmap): SymbolPrediction? {
        val aspect = bitmap.width.toFloat() / max(1, bitmap.height).toFloat()
        if (aspect < 1.10f) return null

        val w = 96
        val h = 96
        val gray = resizeGray(bitmap, w, h)
        val background = borderMedian(gray, w, h)
        val contrast = IntArray(gray.size) { abs(gray[it] - background) }

        val p95 = percentile(contrast, 0.95f)
        val p99 = percentile(contrast, 0.99f)
        if (p99 < 24) return null

        val threshold = max(20, ((p95 + p99) * 0.34f).roundToInt())
        val rowCount = IntArray(h)
        val rowMinX = IntArray(h) { w }
        val rowMaxX = IntArray(h) { -1 }

        for (y in 0 until h) {
            for (x in 0 until w) {
                if (contrast[y * w + x] >= threshold) {
                    rowCount[y]++
                    rowMinX[y] = min(rowMinX[y], x)
                    rowMaxX[y] = max(rowMaxX[y], x)
                }
            }
        }

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

        val activeThreshold = (w * 0.40f).roundToInt()
        val bands = mutableListOf<IntRange>()
        var start = -1

        for (y in 0 until h) {
            val active = smooth[y] >= activeThreshold
            if (active && start < 0) start = y
            if ((!active || y == h - 1) && start >= 0) {
                val end = if (active && y == h - 1) y else y - 1
                if (end - start + 1 in 1..18) bands += start..end
                start = -1
            }
        }

        if (bands.size < 2) return null

        data class Band(val y1: Int, val y2: Int, val x1: Int, val x2: Int, val strength: Int) {
            val cy: Float get() = (y1 + y2) * 0.5f
            val width: Int get() = x2 - x1 + 1
            val thickness: Int get() = y2 - y1 + 1
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
            if (x2 < x1) null else Band(range.first, range.last, x1, x2, strength)
        }.sortedByDescending { it.strength }

        var best = 0f
        for (i in 0 until min(4, candidates.size)) {
            for (j in i + 1 until min(4, candidates.size)) {
                val a = candidates[i]
                val b = candidates[j]
                val upper = if (a.cy < b.cy) a else b
                val lower = if (a.cy < b.cy) b else a

                val gap = lower.cy - upper.cy
                if (gap !in (h * 0.12f)..(h * 0.48f)) continue
                if (upper.thickness > h * 0.18f || lower.thickness > h * 0.18f) continue

                val minW = min(upper.width, lower.width).toFloat()
                val maxW = max(upper.width, lower.width).toFloat()
                if (minW < w * 0.38f) continue

                val similarity = minW / max(maxW, 1f)
                if (similarity < 0.72f) continue

                val overlap = max(0, min(upper.x2, lower.x2) - max(upper.x1, lower.x1) + 1).toFloat()
                val overlapRatio = overlap / max(minW, 1f)
                if (overlapRatio < 0.76f) continue

                best = max(best, 0.5f * similarity + 0.5f * overlapRatio)
            }
        }

        if (best < 0.80f) return null

        return SymbolPrediction(
            id = EQUAL_ID,
            name = "equal",
            display = "=",
            latex = "=",
            confidence = (0.86f + 0.12f * best).coerceAtMost(0.98f)
        )
    }

    fun isMeaningfulCrop(bitmap: Bitmap): Boolean {
        if (bitmap.width < 2 || bitmap.height < 2) return false
        val gray = resizeGray(bitmap, 64, 64)
        val bg = borderMedian(gray, 64, 64)
        val contrast = IntArray(gray.size) { abs(gray[it] - bg) }
        val p95 = percentile(contrast, 0.95f)
        val threshold = max(18, (p95 * 0.55f).roundToInt())
        val fg = contrast.count { it >= threshold }
        val ratio = fg.toFloat() / contrast.size.toFloat()
        return p95 >= 18 && ratio in 0.012f..0.66f
    }

    private fun resizeGray(source: Bitmap, outW: Int, outH: Int): IntArray {
        val scaled = Bitmap.createScaledBitmap(source, outW, outH, true)
        val pixels = IntArray(outW * outH)
        scaled.getPixels(pixels, 0, outW, 0, 0, outW, outH)
        return IntArray(pixels.size) { i ->
            val c = pixels[i]
            (0.299 * Color.red(c) + 0.587 * Color.green(c) + 0.114 * Color.blue(c))
                .roundToInt().coerceIn(0, 255)
        }
    }

    private fun borderMedian(gray: IntArray, w: Int, h: Int): Int {
        val values = ArrayList<Int>(2 * w + 2 * h)
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
        val target = max(1, (values.size * p.coerceIn(0f, 1f)).roundToInt())
        var cumulative = 0
        for (i in hist.indices) {
            cumulative += hist[i]
            if (cumulative >= target) return i
        }
        return 255
    }
}
