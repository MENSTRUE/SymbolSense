package com.symbolsense.ui.screens.home

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.ScanResult
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.components.BottomNavTab
import com.symbolsense.ui.components.GlyphTile
import com.symbolsense.ui.components.SDivider
import com.symbolsense.ui.components.SymbolSenseBottomBar
import com.symbolsense.ui.components.domainGlyph
import com.symbolsense.ui.theme.IndigoPrimary
import com.symbolsense.ui.theme.IndigoPrimaryContainer
import com.symbolsense.ui.theme.TextSecondaryLight
import com.symbolsense.ui.theme.TextTertiaryLight

@Composable
fun HomeScreen(
    selectedTab: BottomNavTab,
    recentHistory: List<ScanResult>,
    onTabSelected: (BottomNavTab) -> Unit,
    onOpenCamera: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenHistoryDetail: (String) -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var domain by remember {
        mutableStateOf(SymbolDomain.AUTO)
    }

    var showModes by remember {
        mutableStateOf(false)
    }

    val modes = listOf(
        SymbolDomain.AUTO,
        SymbolDomain.MATH,
        SymbolDomain.CHEMISTRY,
        SymbolDomain.ELECTRONICS,
        SymbolDomain.GENERAL
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            SymbolSenseBottomBar(
                selectedTab,
                onTabSelected
            )
        }
    ) { inner ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentPadding =
                androidx.compose.foundation.layout.PaddingValues(
                    bottom = 8.dp
                )
        ) {

            // ============================================================
            // HEADER
            // ============================================================

            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(
                                start = 16.dp,
                                end = 8.dp,
                                top = 8.dp,
                                bottom = 10.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column {
                            Text(
                                text = "SymbolSense",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(
                                text = "Recognize symbols instantly",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryLight
                            )
                        }

                        IconButton(
                            onClick = onOpenSettings
                        ) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = "Pengaturan",
                                tint = TextSecondaryLight
                            )
                        }
                    }
                }

                SDivider()
            }

            // ============================================================
            // MAIN ACTION
            // ============================================================

            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Text(
                            text = "Kenali simbol dalam hitungan detik",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(
                            Modifier.height(4.dp)
                        )

                        Text(
                            text = "Arahkan kamera ke simbol matematika, kimia, elektronika, atau simbol umum.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryLight
                        )

                        Spacer(
                            Modifier.height(12.dp)
                        )

                        // Small new active-mode indicator
                        Surface(
                            color = IndigoPrimaryContainer,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 9.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = IndigoPrimary,
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(
                                    Modifier.size(7.dp)
                                )

                                Text(
                                    text = "Mode aktif: ${domain.label}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = IndigoPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(
                            Modifier.height(14.dp)
                        )

                        Button(
                            onClick = onOpenCamera,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IndigoPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp
                            )
                        ) {

                            Icon(
                                Icons.Filled.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(
                                Modifier.size(8.dp)
                            )

                            Text(
                                text = "Pindai simbol",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                SDivider()
            }

            // ============================================================
            // DETECTION MODE
            // ============================================================

            item {

                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {

                    Column {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showModes = !showModes
                                }
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 14.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = "Mode deteksi",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextTertiaryLight
                                )

                                Text(
                                    text = domain.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Icon(
                                Icons.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = TextTertiaryLight
                            )
                        }

                        if (showModes) {

                            SDivider()

                            modes.forEach { item ->

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (item == domain) {
                                                IndigoPrimaryContainer
                                            } else {
                                                Color.Transparent
                                            }
                                        )
                                        .clickable {
                                            domain = item
                                            showModes = false
                                        }
                                        .padding(
                                            start = 24.dp,
                                            end = 16.dp,
                                            top = 12.dp,
                                            bottom = 12.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Text(
                                        text = item.label,
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (item == domain) {
                                            IndigoPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                        fontWeight = if (item == domain) {
                                            FontWeight.SemiBold
                                        } else {
                                            FontWeight.Normal
                                        }
                                    )

                                    if (item == domain) {

                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(
                                                    IndigoPrimary,
                                                    androidx.compose.foundation.shape.CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                SDivider()
            }

            // ============================================================
            // RECENT
            // ============================================================

            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 12.dp,
                            top = 12.dp,
                            bottom = 4.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "TERBARU",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiaryLight,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        modifier = Modifier
                            .clickable(
                                onClick = onOpenHistory
                            )
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Lihat semua",
                            style = MaterialTheme.typography.labelMedium,
                            color = IndigoPrimary
                        )

                        Icon(
                            Icons.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (recentHistory.isEmpty()) {

                item {

                    Surface(
                        color = MaterialTheme.colorScheme.surface
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 18.dp
                                )
                        ) {

                            Text(
                                text = "Belum ada riwayat",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(
                                Modifier.height(3.dp)
                            )

                            Text(
                                text = "Hasil simbol yang kamu simpan akan muncul di sini.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryLight
                            )
                        }
                    }
                }

            } else {

                itemsIndexed(
                    items = recentHistory.take(3),
                    key = { _, item ->
                        item.id
                    }
                ) { index, item ->

                    Surface(
                        color = MaterialTheme.colorScheme.surface
                    ) {

                        if (index > 0) {
                            SDivider(
                                indent = 68
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOpenHistoryDetail(
                                        item.id
                                    )
                                }
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 13.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            GlyphTile(
                                glyph = item.detectedSymbols
                                    .firstOrNull()
                                    ?.displayGlyph
                                    ?: domainGlyph(
                                        item.domain
                                    )
                            )

                            Spacer(
                                Modifier.size(12.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = item.rawPreviewText,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = item.domain.label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryLight
                                )
                            }

                            Text(
                                text = item.timestampLabel.substringAfter(
                                    ", ",
                                    item.timestampLabel
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiaryLight
                            )
                        }
                    }
                }
            }

            // ============================================================
            // LIBRARY
            // ============================================================

            item {

                SDivider()

                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = onOpenLibrary
                            )
                            .padding(
                                horizontal = 16.dp,
                                vertical = 15.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    IndigoPrimaryContainer,
                                    MaterialTheme.shapes.small
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                Icons.Filled.MenuBook,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(
                            Modifier.size(12.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Pustaka Simbol",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "Jelajahi simbol berdasarkan domain",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryLight
                            )
                        }

                        Icon(
                            Icons.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = TextTertiaryLight
                        )
                    }
                }

                SDivider()
            }
        }
    }
}