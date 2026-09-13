package com.symbolsense.ai

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Spatial parser V2.1.
 *
 * Adds to V2:
 * - one-line baseline estimation
 * - superscript attachment, e.g. a² -> a^{2}
 * - structural '=' spacing
 * - context-aware x <-> times disambiguation
 * - conservative top-K operator recovery for ambiguous glyphs
 *
 * Important:
 * - This does NOT globally rename x to times.
 * - A contextual replacement is only allowed when the token is physically
 *   between two operand-like tokens on the same baseline.
 * - For x -> times, "times" must already exist in classifier top-K.
 * - For other operator recovery, the operator candidate must already exist
 *   in classifier top-K and pass conservative confidence/ratio gates.
 *
 * It deliberately does NOT pretend to solve full 2D mathematics yet.
 * Fractions, integral limits, matrices, nested radicals, etc. remain future work.
 */
data class ParsedFormulaV2(
    val orderedSymbols: List<RecognizedSymbol>,
    val display: String,
    val latex: String
)

object SpatialParserV2 {

    private const val X_TIMES_MIN_TOPK_CONFIDENCE = 0.04f

    private const val GENERIC_OPERATOR_MAX_BEST_CONFIDENCE = 0.80f
    private const val GENERIC_OPERATOR_MIN_TOPK_CONFIDENCE = 0.12f
    private const val GENERIC_OPERATOR_MIN_RATIO_TO_BEST = 0.18f

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

    private val contextualOperatorNames = setOf(
        "plus",
        "minus",
        "times",
        "divide"
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

    private val digitNames = (0..9).map { it.toString() }.toSet()

    private val variableNames = setOf(
        "x",
        "y",
        "z",
        "a",
        "b",
        "d",
        "e",
        "pi",
        "infinity"
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

        val contextualSymbols = rerankLinearContext(symbols)

        val positioned = contextualSymbols.map { s ->
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

    private fun rerankLinearContext(
        symbols: List<RecognizedSymbol>
    ): List<RecognizedSymbol> {
        if (symbols.size < 3) {
            return symbols.sortedBy { it.boundingBox.left }
        }

        val ordered = symbols
            .sortedBy { it.boundingBox.left }
            .toMutableList()

        val heights = ordered
            .map {
                (it.boundingBox.bottom - it.boundingBox.top)
                    .coerceAtLeast(0.001f)
            }
            .sorted()

        val medianHeight = median(heights).coerceAtLeast(0.02f)
        val original = ordered.toList()

        for (index in 1 until original.lastIndex) {
            val left = original[index - 1]
            val current = original[index]
            val right = original[index + 1]

            if (!isLeftOperandLike(left.prediction.name)) continue
            if (!isRightOperandLike(right.prediction.name)) continue

            if (
                !sameMainLine(
                    left = left,
                    current = current,
                    right = right,
                    medianHeight = medianHeight
                )
            ) {
                continue
            }

            if (current.prediction.name == "x") {
                val timesCandidate = current.topK.firstOrNull {
                    it.name == "times" &&
                            it.confidence >= X_TIMES_MIN_TOPK_CONFIDENCE
                }

                if (timesCandidate != null) {
                    ordered[index] = current.copy(
                        prediction = timesCandidate
                    )
                    continue
                }
            }

            if (
                current.prediction.name !in operatorNames &&
                current.prediction.confidence <=
                GENERIC_OPERATOR_MAX_BEST_CONFIDENCE
            ) {
                val bestOperatorCandidate = current.topK
                    .asSequence()
                    .filter { it.name in contextualOperatorNames }
                    .filter {
                        it.confidence >=
                                GENERIC_OPERATOR_MIN_TOPK_CONFIDENCE
                    }
                    .filter {
                        it.confidence >=
                                current.prediction.confidence *
                                GENERIC_OPERATOR_MIN_RATIO_TO_BEST
                    }
                    .maxByOrNull { it.confidence }

                if (bestOperatorCandidate != null) {
                    ordered[index] = current.copy(
                        prediction = bestOperatorCandidate
                    )
                }
            }
        }

        return ordered
    }

    private fun isLeftOperandLike(name: String): Boolean {
        return name in digitNames ||
                name in variableNames ||
                name == "rbracket"
    }

    private fun isRightOperandLike(name: String): Boolean {
        return name in digitNames ||
                name in variableNames ||
                name == "lbracket" ||
                name == "sqrt" ||
                name == "integral" ||
                name == "sum"
    }

    private fun sameMainLine(
        left: RecognizedSymbol,
        current: RecognizedSymbol,
        right: RecognizedSymbol,
        medianHeight: Float
    ): Boolean {
        fun centerY(s: RecognizedSymbol): Float {
            return (s.boundingBox.top + s.boundingBox.bottom) * 0.5f
        }

        val leftY = centerY(left)
        val currentY = centerY(current)
        val rightY = centerY(right)
        val neighborY = (leftY + rightY) * 0.5f

        val allowed = max(
            0.06f,
            medianHeight * 0.48f
        )

        return abs(currentY - neighborY) <= allowed
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