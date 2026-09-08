package com.symbolsense.ai

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Spatial parser V2.
 *
 * Adds to V1:
 * - one-line baseline estimation
 * - superscript attachment, e.g. a² -> a^{2}
 * - structural '=' spacing
 *
 * It deliberately does NOT pretend to solve full 2D mathematics yet.
 * Fractions, integral limits, matrices, nested radicals, etc. remain future work.
 *
 */
data class ParsedFormulaV2(
    val orderedSymbols: List<RecognizedSymbol>,
    val display: String,
    val latex: String
)

object SpatialParserV2 {

    private val operatorNames = setOf(
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

    private val invalidSuperscriptBases = setOf(
        "plus",
        "minus",
        "times",
        "divide",
        "equal",
        "less_than",
        "greater_than",
        "less_equal",
        "greater_equal",
        "integral",
        "sum"
    )

    private val superscriptDisplay = mapOf(
        "0" to "⁰",
        "1" to "¹",
        "2" to "²",
        "3" to "³",
        "4" to "⁴",
        "5" to "⁵",
        "6" to "⁶",
        "7" to "⁷",
        "8" to "⁸",
        "9" to "⁹",
        "plus" to "⁺",
        "minus" to "⁻"
    )

    private data class Positioned(
        val symbol: RecognizedSymbol,
        val centerX: Float,
        val centerY: Float,
        val width: Float,
        val height: Float
    )

    fun parse(symbols: List<RecognizedSymbol>): ParsedFormulaV2 {
        if (symbols.isEmpty()) {
            return ParsedFormulaV2(
                orderedSymbols = emptyList(),
                display = "",
                latex = ""
            )
        }

        if (symbols.size == 1) {
            val s = symbols.first()
            return ParsedFormulaV2(
                orderedSymbols = symbols,
                display = s.prediction.display,
                latex = s.prediction.latex
            )
        }

        val positioned = symbols.map { s ->
            val b = s.boundingBox
            Positioned(
                symbol = s,
                centerX = (b.left + b.right) * 0.5f,
                centerY = (b.top + b.bottom) * 0.5f,
                width = (b.right - b.left).coerceAtLeast(0.001f),
                height = (b.bottom - b.top).coerceAtLeast(0.001f)
            )
        }

        val heights = positioned.map { it.height }.sorted()
        val medianHeight = median(heights).coerceAtLeast(0.02f)

        val centersY = positioned.map { it.centerY }.sorted()
        val baselineY = median(centersY)

        val superscriptThreshold = max(0.025f, medianHeight * 0.23f)

        val superscriptCandidates = positioned.filter { p ->
            p.centerY < baselineY - superscriptThreshold &&
                p.symbol.prediction.name !in operatorNames
        }.toMutableSet()

        val bases = positioned
            .filter { it !in superscriptCandidates }
            .sortedBy { it.centerX }
            .toMutableList()

        // If the simple median classified everything as superscript, fall back.
        if (bases.isEmpty()) {
            val ordered = positioned.sortedBy { it.centerX }.map { it.symbol }
            return ParsedFormulaV2(
                orderedSymbols = ordered,
                display = buildLinearDisplay(ordered),
                latex = buildLinearLatex(ordered)
            )
        }

        val attached = mutableMapOf<RecognizedSymbol, MutableList<RecognizedSymbol>>()
        val unattached = mutableListOf<RecognizedSymbol>()

        for (sup in superscriptCandidates.sortedBy { it.centerX }) {
            val candidateBase = bases
                .filter { base ->
                    base.symbol.prediction.name !in invalidSuperscriptBases &&
                        sup.centerY < base.centerY - superscriptThreshold * 0.55f &&
                        sup.centerX >= base.centerX - medianHeight * 0.15f &&
                        sup.symbol.boundingBox.left <=
                            base.symbol.boundingBox.right + medianHeight * 1.15f
                }
                .minByOrNull { base ->
                    val horizontalGap = max(
                        0f,
                        sup.symbol.boundingBox.left - base.symbol.boundingBox.right
                    )
                    val centerGap = abs(sup.centerX - base.centerX)
                    horizontalGap * 2f + centerGap
                }

            if (candidateBase == null) {
                unattached += sup.symbol
            } else {
                attached
                    .getOrPut(candidateBase.symbol) { mutableListOf() }
                    .add(sup.symbol)
            }
        }

        // Unattached high symbols are treated as ordinary symbols rather than lost.
        val topLevel = (bases.map { it.symbol } + unattached)
            .sortedBy { it.boundingBox.left }

        val display = StringBuilder()
        val latex = StringBuilder()

        topLevel.forEachIndexed { index, base ->
            val p = base.prediction
            val isOperator = p.name in operatorNames
            val supers = attached[base]
                .orEmpty()
                .sortedBy { it.boundingBox.left }

            if (isOperator) {
                appendSpaced(display, p.display)
                appendSpaced(latex, p.latex)
            } else {
                display.append(p.display)
                latex.append(p.latex)

                if (supers.isNotEmpty()) {
                    display.append(renderSuperscriptDisplay(supers))
                    latex.append("^{")
                    latex.append(
                        supers.joinToString(separator = "") {
                            it.prediction.latex
                        }
                    )
                    latex.append("}")
                }

                // Avoid command concatenation such as \pix.
                if (
                    p.latex.startsWith("\\") &&
                    index < topLevel.lastIndex &&
                    topLevel[index + 1].prediction.name !in operatorNames
                ) {
                    latex.append(' ')
                }
            }
        }

        val ordered = mutableListOf<RecognizedSymbol>()
        topLevel.forEach { base ->
            ordered += base
            ordered += attached[base]
                .orEmpty()
                .sortedBy { it.boundingBox.left }
        }

        return ParsedFormulaV2(
            orderedSymbols = ordered,
            display = display.toString().trim().replace(Regex("\\s+"), " "),
            latex = latex.toString().trim().replace(Regex("\\s+"), " ")
        )
    }

    private fun renderSuperscriptDisplay(
        symbols: List<RecognizedSymbol>
    ): String {
        val mapped = symbols.map { superscriptDisplay[it.prediction.name] }

        return if (mapped.all { it != null }) {
            mapped.joinToString(separator = "") { it!! }
        } else {
            "^(" + symbols.joinToString(separator = "") {
                it.prediction.display
            } + ")"
        }
    }

    private fun appendSpaced(out: StringBuilder, token: String) {
        if (out.isNotEmpty() && out.last() != ' ') out.append(' ')
        out.append(token)
        out.append(' ')
    }

    private fun buildLinearDisplay(symbols: List<RecognizedSymbol>): String {
        val out = StringBuilder()
        for (s in symbols) {
            if (s.prediction.name in operatorNames) {
                appendSpaced(out, s.prediction.display)
            } else {
                out.append(s.prediction.display)
            }
        }
        return out.toString().trim().replace(Regex("\\s+"), " ")
    }

    private fun buildLinearLatex(symbols: List<RecognizedSymbol>): String {
        val out = StringBuilder()
        symbols.forEachIndexed { index, s ->
            val p = s.prediction
            if (p.name in operatorNames) {
                appendSpaced(out, p.latex)
            } else {
                out.append(p.latex)
                if (
                    p.latex.startsWith("\\") &&
                    index < symbols.lastIndex &&
                    symbols[index + 1].prediction.name !in operatorNames
                ) {
                    out.append(' ')
                }
            }
        }
        return out.toString().trim().replace(Regex("\\s+"), " ")
    }

    private fun median(values: List<Float>): Float {
        if (values.isEmpty()) return 0f
        val sorted = values.sorted()
        val mid = sorted.size / 2
        return if (sorted.size % 2 == 1) {
            sorted[mid]
        } else {
            (sorted[mid - 1] + sorted[mid]) * 0.5f
        }
    }
}
