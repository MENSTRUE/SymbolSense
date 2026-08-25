package com.symbolsense.ui.screens.historydetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.ScanResult
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.ConfidenceText
import com.symbolsense.ui.components.GlyphTile
import com.symbolsense.ui.components.SDivider
import com.symbolsense.ui.components.SectionLabel
import com.symbolsense.ui.components.domainCodeLabel
import com.symbolsense.ui.theme.CodeBlack
import com.symbolsense.ui.theme.SymbolMono
import com.symbolsense.ui.theme.TextSecondaryLight

@Composable
fun HistoryDetailScreen(
    result: ScanResult,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onExport: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AppTopBar(title = "Detail riwayat", subtitle = result.timestampLabel, onBack = onBack)
        LazyColumn(Modifier.weight(1f)) {
            item { SectionLabel("Konten yang dikenali") }
            item {
                Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
                    Text(result.rawPreviewText, fontFamily = SymbolMono, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.size(6.dp)); Text(result.domain.label, style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                }
                SDivider()
            }
            item { SectionLabel("Simbol terdeteksi") }
            itemsIndexed(result.detectedSymbols) { index, symbol ->
                if (index > 0) SDivider(indent = 56)
                Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 16.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                    GlyphTile(symbol.displayGlyph, size = 32); Spacer(Modifier.size(12.dp))
                    Text(symbol.label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    ConfidenceText(symbol.confidence)
                }
            }
            item {
                SectionLabel("Hasil terstruktur")
                Column(Modifier.fillMaxWidth().background(CodeBlack).padding(16.dp)) {
                    Text(domainCodeLabel(result.domain), style = MaterialTheme.typography.labelSmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.40f))
                    Spacer(Modifier.size(8.dp)); Text(result.latexOrCode, fontFamily = SymbolMono, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.82f), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        SDivider()
        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, "Hapus") }
            androidx.compose.material3.Button(onClick = onExport, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium, elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(0.dp)) { Text("Ekspor") }
        }
    }
}
