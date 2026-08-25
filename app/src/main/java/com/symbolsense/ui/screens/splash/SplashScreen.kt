package com.symbolsense.ui.screens.splash

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.symbolsense.ui.theme.BorderLight
import com.symbolsense.ui.theme.CyanAccent
import com.symbolsense.ui.theme.IndigoPrimary
import com.symbolsense.ui.theme.SymbolMono
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit, splashDurationMs: Long = 1600L) {
    LaunchedEffect(Unit) { delay(splashDurationMs); onTimeout() }
    val transition = rememberInfiniteTransition(label = "splash")
    val p by transition.animateFloat(0.08f, 0.88f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "scan")
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).statusBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(1f))
        Box(Modifier.size(72.dp).border(1.dp, BorderLight, MaterialTheme.shapes.large), contentAlignment = Alignment.Center) {
            Text("∫", fontFamily = SymbolMono, fontWeight = FontWeight.Bold, fontSize = 40.sp, color = IndigoPrimary)
            Canvas(Modifier.fillMaxSize()) { drawLine(CyanAccent, Offset(0f, size.height * p), Offset(size.width, size.height * p), 1.5.dp.toPx()) }
        }
        Spacer(Modifier.size(20.dp))
        Text("SymbolSense", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.weight(1f))
    }
}
