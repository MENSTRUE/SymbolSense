package com.symbolsense.ui.screens.symboldetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.symbolsense.data.model.SymbolEntry
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.SDivider
import com.symbolsense.ui.components.SectionLabel
import com.symbolsense.ui.theme.CodeBlack
import com.symbolsense.ui.theme.SymbolMono
import com.symbolsense.ui.theme.TextSecondaryLight

@Composable
fun SymbolDetailScreen(
    entry: SymbolEntry,
    onBack: () -> Unit,
    onSpeak: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AppTopBar(title = "Detail simbol", onBack = onBack)
        LazyColumn(Modifier.weight(1f)) {
            item {
                Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 20.dp, vertical = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(entry.glyph, fontFamily = SymbolMono, fontSize = 88.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.size(10.dp)); Text(entry.name, style = MaterialTheme.typography.titleLarge)
                    Text("${entry.domain.label} · ${entry.category}", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                }
                SDivider()
            }
            item {
                SectionLabel("Makna")
                Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(16.dp), verticalAlignment = Alignment.Top) {
                    Text(entry.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    IconButton(onClick = onSpeak) { Icon(Icons.Filled.VolumeUp, "Bacakan") }
                }
                SDivider()
            }
            item {
                SectionLabel("Notasi")
                Column(Modifier.fillMaxWidth().background(CodeBlack).padding(16.dp)) {
                    Text("LaTeX", style = MaterialTheme.typography.labelSmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.40f)); Text(entry.notationLatex, fontFamily = SymbolMono, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.84f))
                    Spacer(Modifier.size(12.dp)); Text("Unicode", style = MaterialTheme.typography.labelSmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.40f)); Text(entry.notationUnicode, fontFamily = SymbolMono, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.84f))
                    Spacer(Modifier.size(12.dp)); Text("ASCII", style = MaterialTheme.typography.labelSmall, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.40f)); Text(entry.notationAscii, fontFamily = SymbolMono, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.84f))
                }
            }
            item { SectionLabel("Contoh penggunaan") }
            items(entry.usageExamples) { example ->
                Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(example.expression, fontFamily = SymbolMono, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.size(3.dp)); Text(example.caption, style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                }
                SDivider(indent = 16)
            }
        }
    }
}
