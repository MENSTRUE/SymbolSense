package com.symbolsense.ui.screens.preview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.symbolsense.ui.components.SystemBarsForScreen
import com.symbolsense.ui.theme.CameraBlack
import com.symbolsense.ui.theme.CyanAccent
import com.symbolsense.ui.theme.SymbolMono

@Composable
fun ImagePreviewScreen(
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    SystemBarsForScreen(darkIcons = false)
    var perspective by remember { mutableStateOf(true) }
    var contrast by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(CameraBlack)) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DarkAction(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White) }
            Text("Pangkas & perbaiki", style = MaterialTheme.typography.titleSmall, color = Color.White.copy(alpha = 0.92f))
            Surface(onClick = onConfirm, shape = CircleShape, color = CyanAccent.copy(alpha = 0.15f), border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.45f)), modifier = Modifier.size(40.dp)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Filled.Check, "Lanjut", tint = CyanAccent, modifier = Modifier.size(18.dp)) }
            }
        }

        Box(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
            Box(
                Modifier.fillMaxWidth().aspectRatio(4f / 3f).background(Color.White.copy(alpha = 0.04f), MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("∫₀^∞ e^{-x²} dx = √π/2", fontFamily = SymbolMono, color = Color.White.copy(alpha = 0.34f))
                    Spacer(Modifier.height(18.dp))
                    Text("∇²φ = ρ/ε₀", fontFamily = SymbolMono, color = Color.White.copy(alpha = 0.30f))
                }
                CropOverlay()
            }
        }

        Column(Modifier.fillMaxWidth().background(Color.Black.copy(alpha = 0.48f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
            ToggleRow("Koreksi perspektif", perspective) { perspective = it }
            ToggleRow("Tingkatkan kontras", contrast) { contrast = it }
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Surface(onClick = { perspective = true; contrast = false }, shape = MaterialTheme.shapes.medium, color = Color.Transparent, border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)), modifier = Modifier.weight(1f).height(46.dp)) {
                    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(Icons.Filled.RotateLeft, null, tint = Color.White.copy(alpha = 0.65f), modifier = Modifier.size(17.dp))
                        Spacer(Modifier.size(6.dp)); Text("Reset", color = Color.White.copy(alpha = 0.75f), style = MaterialTheme.typography.labelLarge)
                    }
                }
                Surface(onClick = onConfirm, shape = MaterialTheme.shapes.medium, color = CyanAccent, modifier = Modifier.weight(1f).height(46.dp)) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Konfirmasi", color = Color.White, style = MaterialTheme.typography.labelLarge) }
                }
            }
        }
    }
}

@Composable
private fun CropOverlay() {
    Canvas(Modifier.fillMaxSize()) {
        val left = size.width * 0.08f
        val right = size.width * 0.92f
        val top = size.height * 0.12f
        val bottom = size.height * 0.88f
        drawRect(CyanAccent.copy(alpha = 0.70f), Offset(left, top), androidx.compose.ui.geometry.Size(right-left, bottom-top), style = androidx.compose.ui.graphics.drawscope.Stroke(1.5.dp.toPx()))
        val r = 6.dp.toPx()
        listOf(Offset(left, top), Offset(right, top), Offset(left, bottom), Offset(right, bottom)).forEach { drawCircle(CyanAccent, r, it); drawCircle(Color.White, r * 0.45f, it) }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().height(46.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.78f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun DarkAction(onClick: () -> Unit, content: @Composable () -> Unit) {
    Surface(onClick = onClick, shape = CircleShape, color = Color.White.copy(alpha = 0.10f), modifier = Modifier.size(40.dp)) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
    }
}
