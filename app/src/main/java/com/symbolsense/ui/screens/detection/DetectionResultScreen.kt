package com.symbolsense.ui.screens.detection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.DetectedSymbol
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.BoundingBoxOverlay
import com.symbolsense.ui.components.ConfidenceText
import com.symbolsense.ui.components.GlyphTile
import com.symbolsense.ui.components.PrimaryActionButton
import com.symbolsense.ui.components.SDivider
import com.symbolsense.ui.theme.AmberWarning
import com.symbolsense.ui.theme.CameraBlack
import com.symbolsense.ui.theme.SymbolMono
import com.symbolsense.ui.theme.TextSecondaryLight
import com.symbolsense.ui.theme.TextTertiaryLight

@Composable
fun DetectionResultScreen(
    symbols: List<DetectedSymbol>,
    onBack: () -> Unit,
    onViewStructuredResult: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AppTopBar(title = "Hasil pemindaian", onBack = onBack)
        LazyColumn(Modifier.weight(1f)) {
            item {
                Box(
                    Modifier.fillMaxWidth().aspectRatio(1.45f).background(CameraBlack),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("∫ x² dx = x³/3 + C", fontFamily = SymbolMono, color = Color.White.copy(alpha = 0.55f))
                        Spacer(Modifier.height(14.dp))
                        Text("e^x   √(a²+b²) ≤ a+b", fontFamily = SymbolMono, color = Color.White.copy(alpha = 0.42f))
                    }
                    BoundingBoxOverlay(symbols)
                }
            }
            item {
                Text("${symbols.size} simbol ditemukan", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
            }
            itemsIndexed(symbols) { index, symbol ->
                if (index > 0) SDivider(indent = 68)
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${index + 1}".padStart(2, '0'), style = MaterialTheme.typography.labelSmall, color = TextTertiaryLight, modifier = Modifier.padding(end = 10.dp))
                    GlyphTile(symbol.displayGlyph)
                    Spacer(Modifier.size(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(symbol.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        Text("Matematika", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                        if (symbol.confidence < 0.70f) Text("Perlu ditinjau", style = MaterialTheme.typography.bodySmall, color = AmberWarning)
                    }
                    ConfidenceText(symbol.confidence)
                }
            }
        }
        PrimaryActionButton("Lihat hasil terstruktur", onClick = onViewStructuredResult, modifier = Modifier.padding(16.dp))
    }
}
