package com.symbolsense.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.DetectedSymbol
import com.symbolsense.ui.theme.AmberWarning
import com.symbolsense.ui.theme.CyanAccent

@Composable
fun BoundingBoxOverlay(symbols: List<DetectedSymbol>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        symbols.forEach { symbol ->
            val box = symbol.boundingBox
            val left = box.left * size.width
            val top = box.top * size.height
            val right = box.right * size.width
            val bottom = box.bottom * size.height
            val color = confidenceColor(symbol.confidence)
            drawRect(
                color = color,
                topLeft = Offset(left, top),
                size = Size((right - left).coerceAtLeast(1f), (bottom - top).coerceAtLeast(1f)),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}

fun confidenceColor(confidence: Float): Color = if (confidence < 0.70f) AmberWarning else CyanAccent
