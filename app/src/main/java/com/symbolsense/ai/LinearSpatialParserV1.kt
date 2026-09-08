package com.symbolsense.ai

import kotlin.math.abs

/**
 * Spatial parser V1.
 *
 * Scope deliberately kept conservative:
 * - sorts detected symbols into visual rows
 * - sorts each row left -> right
 * - creates readable display text
 * - creates valid-enough linear LaTeX for supported 32 classes
 *
 * It is NOT yet a full 2D math parser for fractions, roots with radicands,
 * superscript/subscript trees, integral limits, or matrices.
 */
data class ParsedFormula(
    val orderedSymbols: List<RecognizedSymbol>,
    val display: String,
    val latex: String
)

object LinearSpatialParserV1 {

    private val operatorNames = setOf(
        "plus",
        "minus",
        "times",
        "divide",
        "less_than",
        "greater_than",
        "less_equal",
        "greater_equal"
    )

    fun parse(symbols: List<RecognizedSymbol>): ParsedFormula {
        if (symbols.isEmpty()) {
            return ParsedFormula(
                orderedSymbols = emptyList(),
                display = "",
                latex = ""
            )
        }

        val rows = groupIntoRows(symbols)
        val ordered = rows.flatten()

        val displayRows = rows.map(::buildDisplayRow)
        val latexRows = rows.map(::buildLatexRow)

        return ParsedFormula(
            orderedSymbols = ordered,
            display = displayRows.joinToString("\n"),
            latex = latexRows.joinToString(" \\\\ ")
        )
    }

    private fun groupIntoRows(symbols: List<RecognizedSymbol>): List<List<RecognizedSymbol>> {
        if (symbols.size <= 1) return listOf(symbols)

        val heights = symbols
            .map { it.boundingBox.bottom - it.boundingBox.top }
            .filter { it > 0f }
            .sorted()

        val medianHeight = if (heights.isEmpty()) {
            0.20f
        } else {
            heights[heights.size / 2]
        }

        val tolerance = (medianHeight * 0.60f).coerceAtLeast(0.035f)

        data class MutableRow(
            val symbols: MutableList<RecognizedSymbol>,
            var meanCenterY: Float
        )

        val byY = symbols.sortedBy {
            (it.boundingBox.top + it.boundingBox.bottom) * 0.5f
        }

        val rows = mutableListOf<MutableRow>()

        for (symbol in byY) {
            val centerY = (symbol.boundingBox.top + symbol.boundingBox.bottom) * 0.5f

            val bestRow = rows
                .filter { abs(it.meanCenterY - centerY) <= tolerance }
                .minByOrNull { abs(it.meanCenterY - centerY) }

            if (bestRow == null) {
                rows += MutableRow(
                    symbols = mutableListOf(symbol),
                    meanCenterY = centerY
                )
            } else {
                bestRow.symbols += symbol
                bestRow.meanCenterY = bestRow.symbols
                    .map { (it.boundingBox.top + it.boundingBox.bottom) * 0.5f }
                    .average()
                    .toFloat()
            }
        }

        return rows
            .sortedBy { it.meanCenterY }
            .map { row ->
                row.symbols.sortedBy { it.boundingBox.left }
            }
    }

    private fun buildDisplayRow(row: List<RecognizedSymbol>): String {
        val out = StringBuilder()

        row.forEachIndexed { index, symbol ->
            val prediction = symbol.prediction
            val isOperator = prediction.name in operatorNames

            if (isOperator) {
                if (out.isNotEmpty() && out.last() != ' ') out.append(' ')
                out.append(prediction.display)
                out.append(' ')
            } else {
                out.append(prediction.display)
            }

            if (index == row.lastIndex) {
                while (out.isNotEmpty() && out.last() == ' ') {
                    out.deleteCharAt(out.lastIndex)
                }
            }
        }

        return out.toString().trim()
    }

    private fun buildLatexRow(row: List<RecognizedSymbol>): String {
        val out = StringBuilder()

        row.forEachIndexed { index, symbol ->
            val p = symbol.prediction
            val isOperator = p.name in operatorNames
            val isCommand = p.latex.startsWith("\\")

            if (isOperator) {
                if (out.isNotEmpty() && out.last() != ' ') out.append(' ')
                out.append(p.latex)
                out.append(' ')
            } else {
                out.append(p.latex)

                // Prevent commands such as \pi followed by x from becoming \pix.
                if (isCommand && index < row.lastIndex) {
                    out.append(' ')
                }
            }
        }

        return out.toString().trim().replace(Regex("\\s+"), " ")
    }
}
