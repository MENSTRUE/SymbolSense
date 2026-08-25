package com.symbolsense.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.SDivider
import com.symbolsense.ui.components.SectionLabel
import com.symbolsense.ui.theme.SymbolMono
import com.symbolsense.ui.theme.TextSecondaryLight
import com.symbolsense.ui.theme.TextTertiaryLight

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenHistory: () -> Unit,
    onClearHistory: () -> Unit,
    onAbout: () -> Unit
) {
    var autoDomain by remember { mutableStateOf(true) }
    var confidence by remember { mutableFloatStateOf(0.70f) }
    var autoCrop by remember { mutableStateOf(true) }
    var scanAnimation by remember { mutableStateOf(true) }
    var highContrast by remember { mutableStateOf(false) }
    var confirmClear by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AppTopBar(title = "Pengaturan", onBack = onBack)

        LazyColumn(Modifier.weight(1f)) {
            item {
                SectionLabel("Pengenalan")
                ToggleSetting(
                    title = "Deteksi domain otomatis",
                    subtitle = "Pilih domain berdasarkan gambar",
                    checked = autoDomain,
                    onChecked = { autoDomain = it }
                )
                SDivider(indent = 16)
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Kepercayaan minimum",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Tandai hasil di bawah ambang untuk ditinjau",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryLight
                            )
                        }
                        Text(
                            "${(confidence * 100).toInt()}%",
                            fontFamily = SymbolMono,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Slider(
                        value = confidence,
                        onValueChange = { confidence = it },
                        valueRange = 0.4f..0.95f
                    )
                }
                SDivider()
            }

            item {
                SectionLabel("Kamera")
                ToggleSetting(
                    "Pangkas otomatis",
                    "Deteksi area simbol setelah mengambil gambar",
                    autoCrop
                ) { autoCrop = it }
                SDivider(indent = 16)
                ToggleSetting(
                    "Animasi pemindaian",
                    "Tampilkan garis pemindaian cyan",
                    scanAnimation
                ) { scanAnimation = it }
                SDivider()
            }

            item {
                SectionLabel("Tampilan & aksesibilitas")
                ToggleSetting(
                    "Kontras tinggi",
                    "Perkuat kontras elemen penting",
                    highContrast
                ) { highContrast = it }
                SDivider()
            }

            item {
                SectionLabel("Data")
                NavigationSetting(
                    "Riwayat",
                    "Lihat hasil pemindaian yang tersimpan",
                    onOpenHistory
                )
                SDivider(indent = 16)
                NavigationSetting(
                    "Hapus riwayat lokal",
                    "Hapus seluruh hasil pemindaian dari perangkat"
                ) {
                    confirmClear = true
                }
                SDivider()
            }

            item {
                SectionLabel("Tentang")
                NavigationSetting("SymbolSense", "Tentang aplikasi", onAbout)
                SDivider(indent = 16)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Versi",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "0.1.0",
                        fontFamily = SymbolMono,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryLight
                    )
                }
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Hapus seluruh riwayat?") },
            text = {
                Text("Semua hasil pemindaian yang tersimpan di perangkat akan dihapus.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmClear = false
                        onClearHistory()
                    }
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun ToggleSetting(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryLight
            )
        }
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}

@Composable
private fun NavigationSetting(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryLight
            )
        }
        androidx.compose.material3.Icon(
            Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextTertiaryLight,
            modifier = Modifier.size(18.dp)
        )
    }
}
