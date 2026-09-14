package com.symbolsense.ai

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Separate structural recognizer for symbols that benefit from geometry checks.
 *
 * V5.5 scope:
 * - '=' structural recognition
 * - '÷' structural recognition
 * - meaningful-crop gate
 *
 * IMPORTANT:
 * These recognizers are intentionally conservative.
 *
 * They run AFTER detector crop extraction and classifier inference.
 * They must not globally rewrite arbitrary classifier predictions.
 */
object StructuralTokenRecognizer {

    private const val EQUAL_ID = -100

    // "divide" is part of the exact-32 classifier contract.
    private const val DIVIDE_ID = 13

    /**
     * Recognize '=' from two long, horizontally aligned bands.
     */
    fun recognizeEqual(
        bitmap: Bitmap
    ): SymbolPrediction? {

        val aspect =
            bitmap.width.toFloat() /
                    max(
                        1,
                        bitmap.height
                    ).toFloat()

        if (aspect < 1.10f) {
            return null
        }

        val w = 96
        val h = 96

        val gray =
            resizeGray(
                bitmap,
                w,
                h
            )

        val background =
            borderMedian(
                gray,
                w,
                h
            )

        val contrast =
            IntArray(
                gray.size
            ) {
                abs(
                    gray[it] -
                            background
                )
            }

        val p95 =
            percentile(
                contrast,
                0.95f
            )

        val p99 =
            percentile(
                contrast,
                0.99f
            )

        if (p99 < 24) {
            return null
        }

        val threshold =
            max(
                20,
                (
                        (p95 + p99) *
                                0.34f
                        )
                    .roundToInt()
            )

        val rowCount =
            IntArray(h)

        val rowMinX =
            IntArray(h) {
                w
            }

        val rowMaxX =
            IntArray(h) {
                -1
            }

        for (
        y in
        0 until h
        ) {
            for (
            x in
            0 until w
            ) {
                if (
                    contrast[
                        y * w + x
                    ] >=
                    threshold
                ) {
                    rowCount[y]++

                    rowMinX[y] =
                        min(
                            rowMinX[y],
                            x
                        )

                    rowMaxX[y] =
                        max(
                            rowMaxX[y],
                            x
                        )
                }
            }
        }

        val smooth =
            IntArray(h)

        for (
        y in
        0 until h
        ) {
            var sum = 0
            var n = 0

            for (
            yy in
            max(
                0,
                y - 1
            )..
                    min(
                        h - 1,
                        y + 1
                    )
            ) {
                sum +=
                    rowCount[yy]

                n++
            }

            smooth[y] =
                sum /
                        max(
                            1,
                            n
                        )
        }

        val activeThreshold =
            (
                    w *
                            0.40f
                    )
                .roundToInt()

        val bands =
            mutableListOf<
                    IntRange
                    >()

        var start = -1

        for (
        y in
        0 until h
        ) {
            val active =
                smooth[y] >=
                        activeThreshold

            if (
                active &&
                start < 0
            ) {
                start =
                    y
            }

            if (
                (
                        !active ||
                                y ==
                                h - 1
                        ) &&
                start >= 0
            ) {
                val end =
                    if (
                        active &&
                        y ==
                        h - 1
                    ) {
                        y
                    } else {
                        y - 1
                    }

                if (
                    end -
                    start +
                    1 in
                    1..18
                ) {
                    bands +=
                        start..end
                }

                start =
                    -1
            }
        }

        if (
            bands.size <
            2
        ) {
            return null
        }

        data class Band(
            val y1: Int,
            val y2: Int,
            val x1: Int,
            val x2: Int,
            val strength: Int
        ) {
            val cy: Float
                get() =
                    (
                            y1 +
                                    y2
                            ) *
                            0.5f

            val width: Int
                get() =
                    x2 -
                            x1 +
                            1

            val thickness: Int
                get() =
                    y2 -
                            y1 +
                            1
        }

        val candidates =
            bands
                .mapNotNull {
                        range ->

                    var x1 =
                        w

                    var x2 =
                        -1

                    var strength =
                        0

                    for (
                    y in
                    range
                    ) {
                        strength +=
                            smooth[y]

                        if (
                            rowMaxX[y] >=
                            0
                        ) {
                            x1 =
                                min(
                                    x1,
                                    rowMinX[y]
                                )

                            x2 =
                                max(
                                    x2,
                                    rowMaxX[y]
                                )
                        }
                    }

                    if (
                        x2 <
                        x1
                    ) {
                        null
                    } else {
                        Band(
                            range.first,
                            range.last,
                            x1,
                            x2,
                            strength
                        )
                    }
                }
                .sortedByDescending {
                    it.strength
                }

        var best =
            0f

        for (
        i in
        0 until
                min(
                    4,
                    candidates.size
                )
        ) {
            for (
            j in
            i + 1 until
                    min(
                        4,
                        candidates.size
                    )
            ) {
                val a =
                    candidates[i]

                val b =
                    candidates[j]

                val upper =
                    if (
                        a.cy <
                        b.cy
                    ) {
                        a
                    } else {
                        b
                    }

                val lower =
                    if (
                        a.cy <
                        b.cy
                    ) {
                        b
                    } else {
                        a
                    }

                val gap =
                    lower.cy -
                            upper.cy

                if (
                    gap !in
                    (
                            h *
                                    0.12f
                            )..
                    (
                            h *
                                    0.48f
                            )
                ) {
                    continue
                }

                if (
                    upper.thickness >
                    h *
                    0.18f ||
                    lower.thickness >
                    h *
                    0.18f
                ) {
                    continue
                }

                val minW =
                    min(
                        upper.width,
                        lower.width
                    )
                        .toFloat()

                val maxW =
                    max(
                        upper.width,
                        lower.width
                    )
                        .toFloat()

                if (
                    minW <
                    w *
                    0.38f
                ) {
                    continue
                }

                val similarity =
                    minW /
                            max(
                                maxW,
                                1f
                            )

                if (
                    similarity <
                    0.72f
                ) {
                    continue
                }

                val overlap =
                    max(
                        0,
                        min(
                            upper.x2,
                            lower.x2
                        ) -
                                max(
                                    upper.x1,
                                    lower.x1
                                ) +
                                1
                    )
                        .toFloat()

                val overlapRatio =
                    overlap /
                            max(
                                minW,
                                1f
                            )

                if (
                    overlapRatio <
                    0.76f
                ) {
                    continue
                }

                best =
                    max(
                        best,
                        0.5f *
                                similarity +
                                0.5f *
                                overlapRatio
                    )
            }
        }

        if (
            best <
            0.80f
        ) {
            return null
        }

        return SymbolPrediction(
            id = EQUAL_ID,
            name = "equal",
            display = "=",
            latex = "=",
            confidence =
                (
                        0.86f +
                                0.12f *
                                best
                        )
                    .coerceAtMost(
                        0.98f
                    )
        )
    }

    /**
     * Recognize '÷' structurally.
     *
     * Expected geometry:
     *
     *          •
     *       -------
     *          •
     *
     * The implementation requires:
     * - one clearly horizontal center bar,
     * - one compact component above it,
     * - one compact component below it,
     * - top/bottom components aligned around the bar center.
     *
     * A plain '-' therefore cannot pass because it has no two aligned dots.
     */
    fun recognizeDivide(
        bitmap: Bitmap
    ): SymbolPrediction? {

        if (
            bitmap.width < 3 ||
            bitmap.height < 3
        ) {
            return null
        }

        val originalAspect =
            bitmap.width.toFloat() /
                    max(
                        1,
                        bitmap.height
                    ).toFloat()

        /*
         * Very extreme crops are unlikely to be a normal division sign.
         * Keep this range loose because detector crop padding varies.
         */
        if (
            originalAspect <
            0.45f ||
            originalAspect >
            3.40f
        ) {
            return null
        }

        val w = 96
        val h = 96

        val gray =
            resizeGray(
                bitmap,
                w,
                h
            )

        val background =
            borderMedian(
                gray,
                w,
                h
            )

        val contrast =
            IntArray(
                gray.size
            ) {
                abs(
                    gray[it] -
                            background
                )
            }

        val p90 =
            percentile(
                contrast,
                0.90f
            )

        val p95 =
            percentile(
                contrast,
                0.95f
            )

        val p99 =
            percentile(
                contrast,
                0.99f
            )

        if (
            p99 <
            22
        ) {
            return null
        }

        val threshold =
            max(
                18,
                (
                        0.20f *
                                p90 +
                                0.30f *
                                p95 +
                                0.28f *
                                p99
                        )
                    .roundToInt()
            )

        val foreground =
            BooleanArray(
                w *
                        h
            )

        var foregroundCount =
            0

        for (
        i in
        contrast.indices
        ) {
            val isForeground =
                contrast[i] >=
                        threshold

            foreground[i] =
                isForeground

            if (
                isForeground
            ) {
                foregroundCount++
            }
        }

        val foregroundRatio =
            foregroundCount.toFloat() /
                    foreground.size.toFloat()

        if (
            foregroundRatio !in
            0.010f..0.48f
        ) {
            return null
        }

        val components =
            connectedComponents(
                foreground = foreground,
                w = w,
                h = h
            )
                .filter {
                    it.area >=
                            6
                }
                .sortedByDescending {
                    it.area
                }

        if (
            components.size <
            3
        ) {
            return null
        }

        /*
         * The center bar should be much wider than tall.
         */
        val barCandidates =
            components
                .filter {
                    it.width >=
                            (
                                    w *
                                            0.28f
                                    )
                                .roundToInt()
                }
                .filter {
                    it.height <=
                            (
                                    h *
                                            0.28f
                                    )
                                .roundToInt()
                }
                .filter {
                    it.width.toFloat() /
                            max(
                                1,
                                it.height
                            ).toFloat() >=
                            2.0f
                }
                .filter {
                    it.cy in
                            (
                                    h *
                                            0.25f
                                    )..
                            (
                                    h *
                                            0.75f
                                    )
                }
                .sortedByDescending {
                    it.width *
                            it.area
                }

        if (
            barCandidates.isEmpty()
        ) {
            return null
        }

        var bestScore =
            0f

        for (
        bar in
        barCandidates.take(
            4
        )
        ) {
            val barCenterX =
                bar.cx

            val compactCandidates =
                components
                    .filter {
                        it !==
                                bar
                    }
                    .filter {
                        it.width <=
                                (
                                        w *
                                                0.34f
                                        )
                                    .roundToInt()
                    }
                    .filter {
                        it.height <=
                                (
                                        h *
                                                0.34f
                                        )
                                    .roundToInt()
                    }
                    .filter {
                        it.area >=
                                6
                    }
                    .filter {
                        abs(
                            it.cx -
                                    barCenterX
                        ) <=
                                w *
                                0.22f
                    }

            val upperCandidates =
                compactCandidates
                    .filter {
                        it.cy <
                                bar.cy -
                                h *
                                0.10f
                    }
                    .filter {
                        it.bottom <
                                bar.top +
                                h *
                                0.05f
                    }
                    .sortedBy {
                        abs(
                            it.cx -
                                    barCenterX
                        ) +
                                abs(
                                    it.cy -
                                            (
                                                    bar.cy -
                                                            h *
                                                            0.25f
                                                    )
                                )
                    }

            val lowerCandidates =
                compactCandidates
                    .filter {
                        it.cy >
                                bar.cy +
                                h *
                                0.10f
                    }
                    .filter {
                        it.top >
                                bar.bottom -
                                h *
                                0.05f
                    }
                    .sortedBy {
                        abs(
                            it.cx -
                                    barCenterX
                        ) +
                                abs(
                                    it.cy -
                                            (
                                                    bar.cy +
                                                            h *
                                                            0.25f
                                                    )
                                )
                    }

            if (
                upperCandidates.isEmpty() ||
                lowerCandidates.isEmpty()
            ) {
                continue
            }

            for (
            upper in
            upperCandidates.take(
                3
            )
            ) {
                for (
                lower in
                lowerCandidates.take(
                    3
                )
                ) {
                    val upperDx =
                        abs(
                            upper.cx -
                                    barCenterX
                        ) /
                                w.toFloat()

                    val lowerDx =
                        abs(
                            lower.cx -
                                    barCenterX
                        ) /
                                w.toFloat()

                    val dotAlignment =
                        (
                                1f -
                                        (
                                                upperDx +
                                                        lowerDx
                                                ) /
                                        0.44f
                                )
                            .coerceIn(
                                0f,
                                1f
                            )

                    val dotPairAlignment =
                        (
                                1f -
                                        abs(
                                            upper.cx -
                                                    lower.cx
                                        ) /
                                        (
                                                w *
                                                        0.25f
                                                )
                                )
                            .coerceIn(
                                0f,
                                1f
                            )

                    val upperGap =
                        bar.cy -
                                upper.cy

                    val lowerGap =
                        lower.cy -
                                bar.cy

                    if (
                        upperGap !in
                        (
                                h *
                                        0.14f
                                )..
                        (
                                h *
                                        0.44f
                                )
                    ) {
                        continue
                    }

                    if (
                        lowerGap !in
                        (
                                h *
                                        0.14f
                                )..
                        (
                                h *
                                        0.44f
                                )
                    ) {
                        continue
                    }

                    val gapSimilarity =
                        (
                                min(
                                    upperGap,
                                    lowerGap
                                ) /
                                        max(
                                            upperGap,
                                            lowerGap
                                        )
                                )
                            .coerceIn(
                                0f,
                                1f
                            )

                    val dotAreaSimilarity =
                        (
                                min(
                                    upper.area,
                                    lower.area
                                )
                                    .toFloat() /
                                        max(
                                            upper.area,
                                            lower.area
                                        )
                                            .toFloat()
                                )
                            .coerceIn(
                                0f,
                                1f
                            )

                    val upperCompactness =
                        componentCompactness(
                            upper
                        )

                    val lowerCompactness =
                        componentCompactness(
                            lower
                        )

                    if (
                        upperCompactness <
                        0.28f ||
                        lowerCompactness <
                        0.28f
                    ) {
                        continue
                    }

                    val barHorizontality =
                        (
                                bar.width.toFloat() /
                                        max(
                                            1,
                                            bar.height
                                        ).toFloat() /
                                        6f
                                )
                            .coerceIn(
                                0f,
                                1f
                            )

                    val score =
                        0.25f *
                                dotAlignment +
                                0.20f *
                                dotPairAlignment +
                                0.20f *
                                gapSimilarity +
                                0.12f *
                                dotAreaSimilarity +
                                0.13f *
                                (
                                        upperCompactness +
                                                lowerCompactness
                                        ) *
                                0.5f +
                                0.10f *
                                barHorizontality

                    bestScore =
                        max(
                            bestScore,
                            score
                        )
                }
            }
        }

        if (
            bestScore <
            0.68f
        ) {
            return null
        }

        return SymbolPrediction(
            id = DIVIDE_ID,
            name = "divide",
            display = "÷",
            latex = "\\div",
            confidence =
                (
                        0.88f +
                                0.10f *
                                bestScore
                        )
                    .coerceIn(
                        0.88f,
                        0.98f
                    )
        )
    }

    /**
     * Generic crop sanity check.
     */
    fun isMeaningfulCrop(
        bitmap: Bitmap
    ): Boolean {

        if (
            bitmap.width <
            2 ||
            bitmap.height <
            2
        ) {
            return false
        }

        val gray =
            resizeGray(
                bitmap,
                64,
                64
            )

        val bg =
            borderMedian(
                gray,
                64,
                64
            )

        val contrast =
            IntArray(
                gray.size
            ) {
                abs(
                    gray[it] -
                            bg
                )
            }

        val p95 =
            percentile(
                contrast,
                0.95f
            )

        val threshold =
            max(
                18,
                (
                        p95 *
                                0.55f
                        )
                    .roundToInt()
            )

        val fg =
            contrast.count {
                it >=
                        threshold
            }

        val ratio =
            fg.toFloat() /
                    contrast.size.toFloat()

        return (
                p95 >=
                        18 &&
                        ratio in
                        0.012f..0.66f
                )
    }

    private data class Component(
        val area: Int,
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int
    ) {
        val width: Int
            get() =
                right -
                        left +
                        1

        val height: Int
            get() =
                bottom -
                        top +
                        1

        val cx: Float
            get() =
                (
                        left +
                                right
                        ) *
                        0.5f

        val cy: Float
            get() =
                (
                        top +
                                bottom
                        ) *
                        0.5f
    }

    /**
     * 8-connected component extraction on a small 96x96 binary mask.
     */
    private fun connectedComponents(
        foreground: BooleanArray,
        w: Int,
        h: Int
    ): List<Component> {

        val visited =
            BooleanArray(
                foreground.size
            )

        val queueX =
            IntArray(
                foreground.size
            )

        val queueY =
            IntArray(
                foreground.size
            )

        val components =
            mutableListOf<
                    Component
                    >()

        for (
        startY in
        0 until h
        ) {
            for (
            startX in
            0 until w
            ) {
                val startIndex =
                    startY *
                            w +
                            startX

                if (
                    !foreground[
                        startIndex
                    ] ||
                    visited[
                        startIndex
                    ]
                ) {
                    continue
                }

                var head =
                    0

                var tail =
                    0

                queueX[tail] =
                    startX

                queueY[tail] =
                    startY

                tail++

                visited[
                    startIndex
                ] =
                    true

                var area =
                    0

                var left =
                    startX

                var right =
                    startX

                var top =
                    startY

                var bottom =
                    startY

                while (
                    head <
                    tail
                ) {
                    val x =
                        queueX[
                            head
                        ]

                    val y =
                        queueY[
                            head
                        ]

                    head++

                    area++

                    left =
                        min(
                            left,
                            x
                        )

                    right =
                        max(
                            right,
                            x
                        )

                    top =
                        min(
                            top,
                            y
                        )

                    bottom =
                        max(
                            bottom,
                            y
                        )

                    for (
                    dy in
                    -1..1
                    ) {
                        for (
                        dx in
                        -1..1
                        ) {
                            if (
                                dx ==
                                0 &&
                                dy ==
                                0
                            ) {
                                continue
                            }

                            val nx =
                                x +
                                        dx

                            val ny =
                                y +
                                        dy

                            if (
                                nx !in
                                0 until w ||
                                ny !in
                                0 until h
                            ) {
                                continue
                            }

                            val ni =
                                ny *
                                        w +
                                        nx

                            if (
                                foreground[
                                    ni
                                ] &&
                                !visited[
                                    ni
                                ]
                            ) {
                                visited[
                                    ni
                                ] =
                                    true

                                queueX[
                                    tail
                                ] =
                                    nx

                                queueY[
                                    tail
                                ] =
                                    ny

                                tail++
                            }
                        }
                    }
                }

                components +=
                    Component(
                        area = area,
                        left = left,
                        top = top,
                        right = right,
                        bottom = bottom
                    )
            }
        }

        return components
    }

    private fun componentCompactness(
        component: Component
    ): Float {

        val boxArea =
            (
                    component.width *
                            component.height
                    )
                .coerceAtLeast(
                    1
                )

        val fill =
            component.area.toFloat() /
                    boxArea.toFloat()

        val aspect =
            min(
                component.width,
                component.height
            )
                .toFloat() /
                    max(
                        component.width,
                        component.height
                    )
                        .toFloat()

        return (
                0.55f *
                        fill +
                        0.45f *
                        aspect
                )
            .coerceIn(
                0f,
                1f
            )
    }

    private fun resizeGray(
        source: Bitmap,
        outW: Int,
        outH: Int
    ): IntArray {

        val scaled =
            Bitmap.createScaledBitmap(
                source,
                outW,
                outH,
                true
            )

        val pixels =
            IntArray(
                outW *
                        outH
            )

        scaled.getPixels(
            pixels,
            0,
            outW,
            0,
            0,
            outW,
            outH
        )

        return IntArray(
            pixels.size
        ) {
                i ->

            val c =
                pixels[i]

            (
                    0.299 *
                            Color.red(c) +
                            0.587 *
                            Color.green(c) +
                            0.114 *
                            Color.blue(c)
                    )
                .roundToInt()
                .coerceIn(
                    0,
                    255
                )
        }
    }

    private fun borderMedian(
        gray: IntArray,
        w: Int,
        h: Int
    ): Int {

        val values =
            ArrayList<Int>(
                2 *
                        w +
                        2 *
                        h
            )

        for (
        x in
        0 until w
        ) {
            values +=
                gray[x]

            values +=
                gray[
                    (
                            h -
                                    1
                            ) *
                            w +
                            x
                ]
        }

        for (
        y in
        0 until h
        ) {
            values +=
                gray[
                    y *
                            w
                ]

            values +=
                gray[
                    y *
                            w +
                            (
                                    w -
                                            1
                                    )
                ]
        }

        values.sort()

        return values[
            values.size /
                    2
        ]
    }

    private fun percentile(
        values: IntArray,
        p: Float
    ): Int {

        val hist =
            IntArray(
                256
            )

        for (
        v in
        values
        ) {
            hist[
                v.coerceIn(
                    0,
                    255
                )
            ]++
        }

        val target =
            max(
                1,
                (
                        values.size *
                                p.coerceIn(
                                    0f,
                                    1f
                                )
                        )
                    .roundToInt()
            )

        var cumulative =
            0

        for (
        i in
        hist.indices
        ) {
            cumulative +=
                hist[i]

            if (
                cumulative >=
                target
            ) {
                return i
            }
        }

        return 255
    }
}
