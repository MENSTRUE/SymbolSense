package com.symbolsense.ui.screens.library

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.SampleData
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.data.model.SymbolEntry
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.BottomNavTab
import com.symbolsense.ui.components.DomainChip
import com.symbolsense.ui.components.SymbolMiniCard
import com.symbolsense.ui.components.SymbolSenseBottomBar
import com.symbolsense.ui.components.TrailingAction

private val libraryTabs = listOf(SymbolDomain.MATH, SymbolDomain.CHEMISTRY, SymbolDomain.ELECTRONICS)

@Composable
fun SymbolLibraryScreen(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    onOpenCamera: () -> Unit,
    onOpenSymbol: (String) -> Unit
) {
    var selectedDomainTab by remember { mutableStateOf(0) }
    var selectedCategory by remember { mutableStateOf("Semua") }

    val domain = libraryTabs[selectedDomainTab]
    val symbolsForDomain = remember(domain) { SampleData.symbolLibrary.filter { it.domain == domain } }
    val categories = remember(domain) { listOf("Semua") + symbolsForDomain.map { it.category }.distinct() }
    val filtered = remember(symbolsForDomain, selectedCategory) {
        if (selectedCategory == "Semua") symbolsForDomain
        else symbolsForDomain.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Pustaka Simbol", trailingIcon = TrailingAction.SEARCH) },
        bottomBar = {
            SymbolSenseBottomBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                onCameraClick = onOpenCamera
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            TabRow(selectedTabIndex = selectedDomainTab) {
                libraryTabs.forEachIndexed { index, d ->
                    Tab(
                        selected = selectedDomainTab == index,
                        onClick = { selectedDomainTab = index; selectedCategory = "Semua" },
                        text = { Text(d.label) }
                    )
                }
            }

            Spacer(Modifier.size(12.dp))

            // Category chips — scrollable
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    DomainChip(label = category, icon = "", selected = selectedCategory == category, onClick = { selectedCategory = category })
                }
                Spacer(Modifier.width(4.dp))
            }

            Spacer(Modifier.size(12.dp))

            if (filtered.isEmpty()) {
                Text("Belum ada simbol untuk domain ini.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 32.dp))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered) { entry: SymbolEntry ->
                        SymbolMiniCard(glyph = entry.glyph, label = entry.name, modifier = Modifier.fillMaxWidth(), onClick = { onOpenSymbol(entry.id) })
                    }
                }
            }
        }
    }
}