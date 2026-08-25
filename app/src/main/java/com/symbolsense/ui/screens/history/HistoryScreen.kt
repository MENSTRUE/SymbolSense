package com.symbolsense.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.ScanResult
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.components.BottomNavTab
import com.symbolsense.ui.components.GlyphTile
import com.symbolsense.ui.components.SDivider
import com.symbolsense.ui.components.SectionLabel
import com.symbolsense.ui.components.SymbolSenseBottomBar
import com.symbolsense.ui.components.domainGlyph
import com.symbolsense.ui.theme.BorderLight
import com.symbolsense.ui.theme.IndigoPrimary
import com.symbolsense.ui.theme.TextSecondaryLight
import com.symbolsense.ui.theme.TextTertiaryLight

@Composable
fun HistoryScreen(
    selectedTab: BottomNavTab,
    historyItems: List<ScanResult>,
    onTabSelected: (BottomNavTab) -> Unit,
    onOpenDetail: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var domain by remember { mutableStateOf(SymbolDomain.AUTO) }

    val domains = listOf(
        SymbolDomain.AUTO,
        SymbolDomain.MATH,
        SymbolDomain.CHEMISTRY,
        SymbolDomain.ELECTRONICS
    )

    val filtered = historyItems.filter { item ->
        (domain == SymbolDomain.AUTO || item.domain == domain) &&
            (
                query.isBlank() ||
                    item.rawPreviewText.contains(query, ignoreCase = true) ||
                    item.structuredOutput.contains(query, ignoreCase = true) ||
                    item.domain.label.contains(query, ignoreCase = true)
                )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { SymbolSenseBottomBar(selectedTab, onTabSelected) }
    ) { inner ->
        LazyColumn(Modifier.fillMaxSize().padding(inner)) {
            item {
                androidx.compose.material3.Surface(color = MaterialTheme.colorScheme.surface) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(top = 12.dp)
                    ) {
                        Text(
                            "Riwayat",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            placeholder = { Text("Cari riwayat") },
                            leadingIcon = { Icon(Icons.Filled.Search, null) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = BorderLight,
                                focusedBorderColor = IndigoPrimary
                            )
                        )
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            domains.forEach { itemDomain ->
                                Text(
                                    text = if (itemDomain == SymbolDomain.AUTO) {
                                        "Semua"
                                    } else {
                                        itemDomain.label
                                    },
                                    modifier = Modifier
                                        .clickable { domain = itemDomain }
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (domain == itemDomain) {
                                        IndigoPrimary
                                    } else {
                                        TextSecondaryLight
                                    },
                                    fontWeight = if (domain == itemDomain) {
                                        FontWeight.SemiBold
                                    } else {
                                        FontWeight.Normal
                                    }
                                )
                            }
                        }
                    }
                }
                SDivider()
            }

            if (filtered.isEmpty()) {
                item {
                    EmptyHistoryState(
                        hasAnyHistory = historyItems.isNotEmpty(),
                        hasQuery = query.isNotBlank() || domain != SymbolDomain.AUTO
                    )
                }
            } else {
                val groups = filtered.groupBy { item ->
                    when {
                        item.timestampLabel.startsWith("Hari ini") -> "Hari ini"
                        item.timestampLabel.startsWith("Kemarin") -> "Kemarin"
                        else -> item.timestampLabel.substringBefore(",")
                    }
                }

                groups.forEach { (group, groupItems) ->
                    item { SectionLabel(group) }
                    items(groupItems, key = { it.id }) { item ->
                        HistoryRow(item, onOpenDetail)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryState(
    hasAnyHistory: Boolean,
    hasQuery: Boolean
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (hasAnyHistory && hasQuery) {
                "Riwayat tidak ditemukan"
            } else {
                "Belum ada riwayat"
            },
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.size(6.dp))
        Text(
            text = if (hasAnyHistory && hasQuery) {
                "Coba ubah kata kunci atau filter domain."
            } else {
                "Simpan hasil pemindaian agar muncul di halaman ini."
            },
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryLight
        )
    }
}

@Composable
private fun HistoryRow(
    item: ScanResult,
    onOpenDetail: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onOpenDetail(item.id) }
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlyphTile(
                item.detectedSymbols.firstOrNull()?.displayGlyph ?: domainGlyph(item.domain)
            )
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    item.rawPreviewText,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    item.domain.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryLight
                )
            }
            Text(
                item.timestampLabel.substringAfter(", ", item.timestampLabel),
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiaryLight
            )
        }
        SDivider(indent = 68)
    }
}
