package com.symbolsense.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.ScanResult
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.TrailingAction
import com.symbolsense.ui.theme.DomainColors
import com.symbolsense.ui.theme.GreenSuccess

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResultEditorScreen(
    result: ScanResult,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onEditManual: () -> Unit,
    onOpenHistory: () -> Unit,
    onExport: () -> Unit,
    onScanLagi: () -> Unit   // ← BARU: tombol scan lagi
) {
    var selectedTab by remember { mutableStateOf(0) }
    val codeTabLabel = when (result.domain) {
        SymbolDomain.MATH -> "Kode LaTeX"
        SymbolDomain.CHEMISTRY -> "Kode SMILES"
        SymbolDomain.ELECTRONICS -> "Netlist"
        else -> "Kode"
    }
    val (badgeBg, badgeFg) = when (result.domain) {
        SymbolDomain.MATH -> DomainColors.MathBg to DomainColors.MathText
        SymbolDomain.CHEMISTRY -> DomainColors.ChemBg to DomainColors.ChemText
        SymbolDomain.ELECTRONICS -> DomainColors.ElectroBg to DomainColors.ElectroText
        else -> DomainColors.GeneralBg to DomainColors.GeneralText
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Hasil",
                titleBadge = result.domain.label,
                titleBadgeBg = badgeBg,
                titleBadgeColor = badgeFg,
                onBack = onBack,
                trailingIcon = TrailingAction.SHARE,
                onTrailingClick = onShare
            )
        },
        bottomBar = {
            // Bottom action bar khusus result — bukan bottom nav tab
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Row 1: Edit | Simpan ke Riwayat | Export
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onEditManual,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.size(4.dp))
                        Text("Edit")
                    }
                    OutlinedButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.size(4.dp))
                        Text("Riwayat")
                    }
                    Button(
                        onClick = onExport,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(Modifier.size(4.dp))
                        Text("Export")
                    }
                }

                Spacer(Modifier.size(8.dp))

                // Row 2: SCAN LAGI — full width, sekali tap langsung ke kamera
                Button(
                    onClick = onScanLagi,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(Modifier.size(8.dp))
                    Text("Scan Lagi", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Pratinjau") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(codeTabLabel) })
            }

            Spacer(Modifier.size(16.dp))

            if (selectedTab == 0) PreviewTab(result) else CodeTab(result, codeTabLabel)

            Spacer(Modifier.size(20.dp))

            Text("Simbol Terdeteksi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.size(8.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                result.detectedSymbols.forEach { symbol ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            "${symbol.displayGlyph} ${symbol.label}",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.size(16.dp))
        }
    }
}

@Composable
private fun PreviewTab(result: ScanResult) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(result.structuredOutput, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, fontFamily = FontFamily.Serif)
            Spacer(Modifier.size(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(6.dp))
                Text("${result.detectedSymbols.size} simbol berhasil dirender", color = GreenSuccess, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun CodeTab(result: ScanResult, codeLabel: String) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(result.latexOrCode, color = MaterialTheme.colorScheme.surface, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(end = 32.dp))
            IconButton(onClick = {}, modifier = Modifier.align(Alignment.TopEnd).size(28.dp)) {
                Icon(Icons.Filled.ContentCopy, contentDescription = "Copy $codeLabel", tint = MaterialTheme.colorScheme.surface)
            }
        }
    }
}