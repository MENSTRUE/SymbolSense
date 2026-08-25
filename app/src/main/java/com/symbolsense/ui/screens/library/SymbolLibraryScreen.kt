package com.symbolsense.ui.screens.library

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.sp
import com.symbolsense.data.model.SampleData
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.components.BottomNavTab
import com.symbolsense.ui.components.SymbolSenseBottomBar
import com.symbolsense.ui.theme.BorderLight
import com.symbolsense.ui.theme.IndigoPrimary
import com.symbolsense.ui.theme.SymbolMono
import com.symbolsense.ui.theme.TextSecondaryLight

@Composable
fun SymbolLibraryScreen(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    onOpenSymbol: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var domain by remember { mutableStateOf(SymbolDomain.MATH) }
    val tabs = listOf(SymbolDomain.MATH, SymbolDomain.CHEMISTRY, SymbolDomain.ELECTRONICS)
    val filtered = SampleData.symbolLibrary.filter { entry ->
        entry.domain == domain &&
            (query.isBlank() || entry.name.contains(query, true) || entry.glyph.contains(query) || entry.category.contains(query, true))
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { SymbolSenseBottomBar(selectedTab, onTabSelected) }
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            Surface(color = MaterialTheme.colorScheme.surface) {
                Column(Modifier.fillMaxWidth().statusBarsPadding().padding(top = 12.dp)) {
                    Text("Pustaka Simbol", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Cari simbol") },
                        leadingIcon = { Icon(Icons.Filled.Search, null) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = BorderLight, focusedBorderColor = IndigoPrimary)
                    )
                    Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 4.dp)) {
                        tabs.forEach { d ->
                            Text(
                                d.label,
                                modifier = Modifier.clickable { domain = d }.padding(horizontal = 8.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (domain == d) IndigoPrimary else TextSecondaryLight,
                                fontWeight = if (domain == d) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
            ) {
                if (filtered.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(Modifier.fillMaxWidth().padding(vertical = 48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Belum ada data contoh", style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.size(6.dp))
                            Text("Belum ada simbol untuk domain ${domain.label.lowercase()}.", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                        }
                    }
                }
                items(filtered) { entry ->
                    Surface(
                        onClick = { onOpenSymbol(entry.id) },
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.medium,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Column(Modifier.padding(horizontal = 8.dp, vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(entry.glyph, fontFamily = SymbolMono, fontWeight = FontWeight.Bold, fontSize = 34.sp)
                            Spacer(Modifier.size(8.dp))
                            Text(entry.name, style = MaterialTheme.typography.labelMedium, maxLines = 1)
                            Text(entry.category, style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
