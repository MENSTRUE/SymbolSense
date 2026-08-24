package com.symbolsense.ui.screens.detection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.DetectedSymbol
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.BoundingBoxOverlay
import com.symbolsense.ui.components.SymbolMiniCard
import com.symbolsense.ui.components.TrailingAction
import com.symbolsense.ui.components.confidenceColor
import com.symbolsense.ui.theme.AmberWarning

/**
 * Screen 8/15 — OCR Result: Detection Overlay.
 *
 * [symbols] berisi hasil deteksi (bounding box relatif + label + confidence)
 * dari symbol classifier. Tap mini-card simbol bisa dipakai untuk membuka
 * dropdown koreksi label manual (TODO: hubungkan ke ViewModel).
 */
@Composable
fun DetectionResultScreen(
    symbols: List<DetectedSymbol>,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onViewStructuredResult: () -> Unit
) {
    val reviewCount = symbols.count { it.confidence < 0.65f }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Hasil Deteksi",
                onBack = onBack,
                trailingIcon = TrailingAction.SHARE, // placeholder, dipakai sebagai shortcut edit
                onTrailingClick = onEdit
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            // Gambar hasil scan + overlay bounding box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = "x²\ndx = x³/3\ne^x = 1+x+x²/2!+...\n√(a²+b²) ≤ a + b",
                    color = MaterialTheme.colorScheme.surface,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(16.dp)
                )
                BoundingBoxOverlay(symbols = symbols, modifier = Modifier.fillMaxSize())
            }

            Spacer(Modifier.size(16.dp))

            // Ringkasan deteksi
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.size(10.dp))
                    Text(
                        "${symbols.size} simbol terdeteksi · $reviewCount perlu review",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    if (reviewCount > 0) {
                        Surface(shape = RoundedCornerShape(8.dp), color = AmberWarning.copy(alpha = 0.15f)) {
                            Text(
                                "$reviewCount review",
                                color = AmberWarning,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.size(16.dp))

            Text("Simbol Terdeteksi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.size(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(symbols) { symbol ->
                    SymbolMiniCard(
                        glyph = symbol.displayGlyph,
                        label = symbol.label,
                        confidence = "${(symbol.confidence * 100).toInt()}%",
                        confidenceColor = confidenceColor(symbol.confidence),
                        onClick = { /* TODO: buka dropdown koreksi label manual */ }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onViewStructuredResult,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Lihat Hasil Terstruktur", modifier = Modifier.padding(vertical = 6.dp))
            }

            Spacer(Modifier.size(16.dp))
        }
    }
}
