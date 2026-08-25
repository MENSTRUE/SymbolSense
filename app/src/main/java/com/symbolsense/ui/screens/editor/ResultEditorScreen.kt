package com.symbolsense.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.ScanResult
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.ConfidenceText
import com.symbolsense.ui.components.GlyphTile
import com.symbolsense.ui.components.SDivider
import com.symbolsense.ui.components.SecondaryActionButton
import com.symbolsense.ui.components.SectionLabel
import com.symbolsense.ui.components.TrailingAction
import com.symbolsense.ui.components.domainCodeLabel
import com.symbolsense.ui.theme.BorderLight
import com.symbolsense.ui.theme.CodeBlack
import com.symbolsense.ui.theme.CyanAccent
import com.symbolsense.ui.theme.IndigoPrimary
import com.symbolsense.ui.theme.SymbolMono
import com.symbolsense.ui.theme.TextSecondaryLight
import com.symbolsense.ui.theme.TextTertiaryLight

@Composable
fun ResultEditorScreen(
    result: ScanResult,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onEditManual: () -> Unit,
    onOpenHistory: () -> Unit,
    onExport: () -> Unit,
    onScanLagi: () -> Unit
) {
    var tab by remember { mutableStateOf(0) }
    var copied by remember { mutableStateOf(false) }
    val codeLabel = domainCodeLabel(result.domain)

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AppTopBar(title = "Hasil", onBack = onBack, trailingIcon = TrailingAction.SHARE, onTrailingClick = onShare, titleBadge = result.domain.label)
        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
            listOf("Pratinjau", "Kode $codeLabel").forEachIndexed { index, title ->
                Column(
                    modifier = Modifier.weight(1f).clickable { tab = index }.padding(top = 11.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = if (tab == index) FontWeight.SemiBold else FontWeight.Normal, color = if (tab == index) IndigoPrimary else TextSecondaryLight)
                    Spacer(Modifier.height(9.dp))
                    Box(Modifier.fillMaxWidth().height(2.dp).background(if (tab == index) IndigoPrimary else Color.Transparent))
                }
            }
        }
        HorizontalDivider(color = BorderLight)

        if (tab == 0) {
            LazyColumn(Modifier.weight(1f)) {
                item {
                    Box(
                        Modifier.fillMaxWidth().padding(16.dp).background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium).padding(horizontal = 20.dp, vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(result.structuredOutput, fontFamily = SymbolMono, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                item { SectionLabel("Simbol yang dikenali") }
                itemsIndexed(result.detectedSymbols) { index, symbol ->
                    if (index > 0) SDivider(indent = 56)
                    Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 16.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                        GlyphTile(symbol.displayGlyph, size = 32)
                        Spacer(Modifier.size(12.dp))
                        Text(symbol.label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                        ConfidenceText(symbol.confidence)
                    }
                }
            }
        } else {
            Column(Modifier.weight(1f).padding(16.dp)) {
                Column(Modifier.fillMaxWidth().background(CodeBlack, MaterialTheme.shapes.medium)) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(codeLabel, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.38f), modifier = Modifier.weight(1f))
                        Row(Modifier.clickable { copied = true }.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material3.Icon(if (copied) Icons.Filled.Check else Icons.Filled.ContentCopy, null, tint = if (copied) Color(0xFF4ADE80) else Color.White.copy(alpha = 0.55f), modifier = Modifier.size(14.dp))
                            Spacer(Modifier.size(5.dp)); Text(if (copied) "Tersalin" else "Salin", style = MaterialTheme.typography.labelMedium, color = if (copied) Color(0xFF4ADE80) else Color.White.copy(alpha = 0.55f))
                        }
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                    Text(result.latexOrCode, fontFamily = SymbolMono, color = Color.White.copy(alpha = 0.80f), modifier = Modifier.fillMaxWidth().padding(14.dp), style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(10.dp))
                Text("Edit kode untuk memperbaiki simbol yang dikenali sebelum mengekspor.", style = MaterialTheme.typography.bodySmall, color = TextTertiaryLight)
            }
        }

        HorizontalDivider(color = BorderLight)
        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SecondaryActionButton("Edit", onClick = onEditManual, modifier = Modifier.weight(1f))
            SecondaryActionButton("Simpan", onClick = onOpenHistory, modifier = Modifier.weight(1f))
            androidx.compose.material3.Button(onClick = onExport, modifier = Modifier.weight(1.35f).height(46.dp), shape = MaterialTheme.shapes.medium, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = IndigoPrimary), elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(0.dp)) {
                Text("Ekspor")
            }
        }
    }
}
