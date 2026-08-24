package com.symbolsense.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.SampleData
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.BottomNavTab
import com.symbolsense.ui.components.DomainChip
import com.symbolsense.ui.components.HistoryCardCompact
import com.symbolsense.ui.components.SymbolSenseBottomBar
import com.symbolsense.ui.components.TrailingAction
import com.symbolsense.ui.components.domainIcon
import com.symbolsense.ui.theme.CyanAccent
import com.symbolsense.ui.theme.IndigoPrimary

@Composable
fun HomeScreen(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    onOpenCamera: () -> Unit,
    onOpenHistoryDetail: (String) -> Unit
) {
    var selectedDomain by remember { mutableStateOf(SymbolDomain.AUTO) }
    val domainOptions = listOf(
        SymbolDomain.AUTO,
        SymbolDomain.MATH,
        SymbolDomain.CHEMISTRY,
        SymbolDomain.ELECTRONICS,
        SymbolDomain.GENERAL
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = "SymbolSense",
                subtitle = "Selamat datang kembali",
                trailingIcon = TrailingAction.SETTINGS,
                onTrailingClick = { onTabSelected(BottomNavTab.SETTINGS) }
            )
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Hero card
            Surface(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                onClick = onOpenCamera
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(IndigoPrimary, CyanAccent)))
                        .padding(20.dp)
                ) {
                    Column {
                        Text("Mulai sekarang", color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
                        Text("Scan Sekarang", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    Icon(
                        Icons.Filled.PhotoCamera, contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.align(Alignment.CenterEnd).size(44.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        Text(
                            "Ketuk untuk membuka kamera",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Pilih Domain — horizontal scroll, tidak kepotong
            Text("Pilih Domain", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                domainOptions.forEach { domain ->
                    DomainChip(
                        label = domain.label,
                        icon = domainIcon(domain),
                        selected = selectedDomain == domain,
                        onClick = { selectedDomain = domain }
                    )
                }
                Spacer(Modifier.width(4.dp)) // padding akhir scroll
            }

            Spacer(Modifier.height(24.dp))

            // Riwayat Terbaru
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Riwayat Terbaru", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Transparent,
                    onClick = { onTabSelected(BottomNavTab.HISTORY) }
                ) {
                    Text(
                        "Lihat Semua",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(SampleData.historyList.take(4)) { item ->
                    HistoryCardCompact(item = item, onClick = { onOpenHistoryDetail(item.id) })
                }
            }

            Spacer(Modifier.height(24.dp))

            // Pustaka Simbol banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                onClick = { onTabSelected(BottomNavTab.LIBRARY) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.primary) {
                        Icon(
                            Icons.Filled.MenuBook, contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Pustaka Simbol", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        Text("Jelajahi kamus simbol yang dikenali", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}