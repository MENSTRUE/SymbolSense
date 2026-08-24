package com.symbolsense.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.BottomNavTab
import com.symbolsense.ui.components.SymbolSenseBottomBar

@Composable
fun SettingsScreen(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    onOpenCamera: () -> Unit,
    onAbout: () -> Unit
) {
    var darkMode by remember { mutableStateOf(false) }
    var autoDomainDetect by remember { mutableStateOf(true) }
    var confidenceThreshold by remember { mutableStateOf(0.70f) }
    var autoPerspective by remember { mutableStateOf(true) }
    var ttsEnabled by remember { mutableStateOf(false) }
    var speechRate by remember { mutableStateOf(0.5f) }

    Scaffold(
        topBar = { AppTopBar(title = "Setelan") },
        bottomBar = {
            SymbolSenseBottomBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                onCameraClick = onOpenCamera
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            item {
                Spacer(Modifier.size(8.dp))
                SectionHeader("TAMPILAN")
                SettingsGroup {
                    SwitchRow(icon = Icons.Filled.DarkMode, title = "Mode Gelap", subtitle = "Gunakan tema gelap", checked = darkMode, onCheckedChange = { darkMode = it })
                    HorizontalDivider()
                    NavRow(icon = Icons.Filled.Language, title = "Bahasa", subtitle = "Pilih bahasa antarmuka", trailingValue = "Indonesia", onClick = {})
                }

                Spacer(Modifier.size(20.dp))
                SectionHeader("PEMROSESAN")
                SettingsGroup {
                    SwitchRow(icon = Icons.Filled.AutoAwesome, title = "Deteksi Domain Otomatis", subtitle = "AI memilih domain secara otomatis", checked = autoDomainDetect, onCheckedChange = { autoDomainDetect = it })
                    HorizontalDivider()
                    SliderRow(icon = Icons.Filled.Tune, title = "Ambang Confidence Review", subtitle = "Tandai simbol dengan confidence di bawah threshold", value = confidenceThreshold, onValueChange = { confidenceThreshold = it })
                    HorizontalDivider()
                    SwitchRow(icon = Icons.Filled.RemoveRedEye, title = "Koreksi Perspektif Otomatis", subtitle = "Luruskan gambar secara otomatis", checked = autoPerspective, onCheckedChange = { autoPerspective = it })
                }

                Spacer(Modifier.size(20.dp))
                SectionHeader("AKSESIBILITAS")
                SettingsGroup {
                    SwitchRow(icon = Icons.Filled.VolumeUp, title = "Mode Pembacaan Simbol (TTS)", subtitle = "Bacakan nama dan deskripsi simbol", checked = ttsEnabled, onCheckedChange = { ttsEnabled = it })
                    HorizontalDivider()
                    SliderRow(icon = Icons.Filled.FastForward, title = "Kecepatan Bicara", subtitle = "Atur kecepatan text-to-speech", value = speechRate, onValueChange = { speechRate = it }, showPercentLabel = false)
                }

                Spacer(Modifier.size(20.dp))
                SectionHeader("DATA")
                SettingsGroup {
                    NavRow(icon = Icons.Filled.Delete, title = "Hapus Semua Riwayat", subtitle = "Hapus seluruh riwayat scan", titleColor = MaterialTheme.colorScheme.error, iconTint = MaterialTheme.colorScheme.error, onClick = {})
                    HorizontalDivider()
                    NavRow(icon = Icons.Filled.Info, title = "Tentang SymbolSense", subtitle = "Versi 1.0.0 · Model v3.2", onClick = onAbout)
                }

                Spacer(Modifier.size(20.dp))
                Text("SymbolSense v1.0.0 · © 2026", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun SettingsGroup(content: @Composable () -> Unit) {
    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
        Column { content() }
    }
}

@Composable
private fun SwitchRow(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(8.dp).size(20.dp))
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun NavRow(icon: ImageVector, title: String, subtitle: String, trailingValue: String? = null, titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface, iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant, onClick: () -> Unit) {
    Surface(onClick = onClick, color = MaterialTheme.colorScheme.surface) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = iconTint.copy(alpha = 0.1f)) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.padding(8.dp).size(20.dp))
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyLarge, color = titleColor)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (trailingValue != null) {
                Text(trailingValue, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.size(4.dp))
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SliderRow(icon: ImageVector, title: String, subtitle: String, value: Float, onValueChange: (Float) -> Unit, showPercentLabel: Boolean = true) {
    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(8.dp).size(20.dp))
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (showPercentLabel) {
                Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
        Slider(value = value, onValueChange = onValueChange)
    }
}