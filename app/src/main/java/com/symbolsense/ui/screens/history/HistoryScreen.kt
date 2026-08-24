package com.symbolsense.ui.screens.history

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.SampleData
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.BottomNavTab
import com.symbolsense.ui.components.DomainChip
import com.symbolsense.ui.components.HistoryCardFull
import com.symbolsense.ui.components.SymbolSenseBottomBar
import com.symbolsense.ui.components.TrailingAction
import com.symbolsense.ui.components.domainIcon

@Composable
fun HistoryScreen(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    onOpenCamera: () -> Unit,
    onOpenDetail: (String) -> Unit,
    onStartScan: () -> Unit
) {
    var filter by remember { mutableStateOf<SymbolDomain?>(null) }
    val items = remember(filter) {
        if (filter == null) SampleData.historyList
        else SampleData.historyList.filter { it.domain == filter }
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "Riwayat", trailingIcon = TrailingAction.SEARCH)
        },
        bottomBar = {
            SymbolSenseBottomBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                onCameraClick = onOpenCamera
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.size(8.dp))

            // Filter chips — horizontal scroll biar ga kepotong
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DomainChip(label = "Semua", icon = "⚡", selected = filter == null, onClick = { filter = null })
                listOf(SymbolDomain.MATH, SymbolDomain.CHEMISTRY, SymbolDomain.ELECTRONICS).forEach { d ->
                    DomainChip(label = d.label, icon = domainIcon(d), selected = filter == d, onClick = { filter = d })
                }
                Spacer(Modifier.width(4.dp))
            }

            Spacer(Modifier.size(12.dp))

            if (items.isEmpty()) {
                EmptyHistoryState(onStartScan)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(items) { item ->
                        HistoryCardFull(item = item, onClick = { onOpenDetail(item.id) }, onMenuClick = {})
                    }
                    item { Spacer(Modifier.size(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryState(onStartScan: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.PhotoCamera, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
            Spacer(Modifier.size(12.dp))
            Text("Belum ada riwayat scan", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.size(16.dp))
            Button(onClick = onStartScan, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Text("Mulai Scan")
            }
        }
    }
}