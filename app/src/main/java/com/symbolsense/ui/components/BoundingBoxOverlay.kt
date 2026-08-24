package com.symbolsense.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.DetectedSymbol
import com.symbolsense.ui.theme.ConfidenceHigh
import com.symbolsense.ui.theme.ConfidenceLow
import com.symbolsense.ui.theme.ConfidenceMedium

/**
 * Overlay bounding box + label confidence di atas gambar hasil scan (Screen 8 - Detection).
 * Letakkan di dalam Box yang sama dengan gambar (mis. menggunakan Modifier.matchParentSize()).
 *
 * Threshold warna label:
 *  - >= 0.85  -> hijau/cyan (confidence tinggi)
 *  - 0.65-0.85 -> cyan (confidence sedang)
 *  - < 0.65   -> amber (perlu review manual)
 */
@Composable
fun BoundingBoxOverlay(
    symbols: List<DetectedSymbol>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthDp = maxWidth
        val heightDp = maxHeight

        symbols.forEach { symbol ->
            val box = symbol.boundingBox
            val color = confidenceColor(symbol.confidence)

            val left = widthDp * box.left
            val top = heightDp * box.top
            val w = widthDp * (box.right - box.left)
            val h = heightDp * (box.bottom - box.top)

            // Kotak deteksi
            Box(
                modifier = Modifier
                    .offset(x = left, y = top)
                    .size(width = w, height = h)
                    .border(BorderStroke(1.dp, color), RoundedCornerShape(2.dp))
            )

            // Label mengambang di atas kotak
            val labelTop = if (top > 16.dp) top - 16.dp else top
            Box(
                modifier = Modifier
                    .offset(x = left, y = labelTop)
                    .background(color, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "${symbol.displayGlyph} ${(symbol.confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
    }
}

fun confidenceColor(confidence: Float): Color = when {
    confidence >= 0.85f -> ConfidenceHigh
    confidence >= 0.65f -> ConfidenceMedium
    else -> ConfidenceLow
}
