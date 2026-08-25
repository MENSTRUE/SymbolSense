package com.symbolsense.ui.screens.processing

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.ui.theme.CyanAccent
import com.symbolsense.ui.theme.GreenSuccess
import com.symbolsense.ui.theme.TextSecondaryLight
import com.symbolsense.ui.theme.TextTertiaryLight
import kotlinx.coroutines.delay

@Composable
fun ProcessingScreen(onDone: () -> Unit) {
    val steps = listOf("Gambar disiapkan", "Simbol terdeteksi", "Menafsirkan struktur", "Menyiapkan hasil")
    var current by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        for (i in steps.indices) {
            current = i
            delay(650)
        }
        delay(350)
        onDone()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        ScanMiniAnimation()
        Spacer(Modifier.height(28.dp))
        Text("Menganalisis gambar", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(6.dp))
        Text("Menyiapkan hasil pengenalan simbol.", style = MaterialTheme.typography.bodyMedium, color = TextSecondaryLight)
        Spacer(Modifier.height(28.dp))
        steps.forEachIndexed { index, step ->
            val done = index < current
            val active = index == current
            Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                when {
                    done -> Icon(Icons.Filled.Check, null, tint = GreenSuccess, modifier = Modifier.size(18.dp))
                    active -> Icon(Icons.Filled.Circle, null, tint = CyanAccent, modifier = Modifier.size(12.dp))
                    else -> Icon(Icons.Filled.Circle, null, tint = TextTertiaryLight.copy(alpha = 0.45f), modifier = Modifier.size(10.dp))
                }
                Spacer(Modifier.size(12.dp))
                Text(step, style = MaterialTheme.typography.bodyMedium, color = if (active || done) MaterialTheme.colorScheme.onSurface else TextTertiaryLight, fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal)
            }
        }
    }
}

@Composable
private fun ScanMiniAnimation() {
    val transition = rememberInfiniteTransition(label = "processing")
    val p by transition.animateFloat(0.12f, 0.88f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "line")
    Box(Modifier.fillMaxWidth().height(90.dp).background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)) {
        Canvas(Modifier.fillMaxSize()) {
            drawLine(CyanAccent.copy(alpha = 0.85f), Offset(size.width * 0.08f, size.height * p), Offset(size.width * 0.92f, size.height * p), 1.5.dp.toPx())
        }
    }
}
