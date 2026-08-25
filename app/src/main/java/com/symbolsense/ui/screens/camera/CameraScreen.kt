package com.symbolsense.ui.screens.camera

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.components.SystemBarsForScreen
import com.symbolsense.ui.theme.CameraBlack
import com.symbolsense.ui.theme.CyanAccent

@Composable
fun CameraScreen(
    onClose: () -> Unit,
    onCapture: () -> Unit,
    onOpenGallery: () -> Unit
) {
    SystemBarsForScreen(darkIcons = false)
    var flashOn by remember { mutableStateOf(false) }
    var domain by remember { mutableStateOf(SymbolDomain.AUTO) }
    var showModes by remember { mutableStateOf(false) }
    val modes = listOf(SymbolDomain.AUTO, SymbolDomain.MATH, SymbolDomain.CHEMISTRY, SymbolDomain.ELECTRONICS)

    Box(Modifier.fillMaxSize().background(CameraBlack)) {
        CameraPlaceholder()
        RuleOfThirds()

        Row(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DarkCircleButton(onClick = onClose) { Icon(Icons.Filled.Close, "Tutup", tint = Color.White) }
            DarkCircleButton(onClick = { flashOn = !flashOn }) {
                Icon(if (flashOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff, "Flash", tint = if (flashOn) Color(0xFFFACC15) else Color.White)
            }
        }

        Column(
            modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScannerFrame(Modifier.fillMaxWidth(0.72f).aspectRatio(1.2f))
            Spacer(Modifier.height(14.dp))
            Text("Posisikan simbol dalam bingkai", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.58f))
        }

        AnimatedVisibility(
            visible = showModes,
            modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 20.dp, vertical = 118.dp).navigationBarsPadding()
        ) {
            Surface(
                color = Color(0xEE121010),
                shape = MaterialTheme.shapes.large,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.10f))
            ) {
                Column(Modifier.fillMaxWidth()) {
                    modes.forEachIndexed { index, item ->
                        if (index > 0) androidx.compose.material3.HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { domain = item; showModes = false }.padding(horizontal = 16.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = if (domain == item) CyanAccent else Color.White.copy(alpha = 0.76f), fontWeight = if (domain == item) FontWeight.SemiBold else FontWeight.Normal)
                            if (domain == item) Icon(Icons.Filled.Check, null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Color.Black.copy(alpha = 0.50f)).navigationBarsPadding().padding(horizontal = 32.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onOpenGallery).padding(8.dp)) {
                Icon(Icons.Filled.Image, "Galeri", tint = Color.White.copy(alpha = 0.72f), modifier = Modifier.size(22.dp))
                Text("Galeri", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.48f))
            }

            Surface(
                onClick = onCapture,
                shape = CircleShape,
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(2.5.dp, CyanAccent),
                modifier = Modifier.size(68.dp)
            ) { Box(Modifier.fillMaxSize().padding(7.dp).border(2.dp, Color.Black.copy(alpha = 0.10f), CircleShape)) }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { showModes = !showModes }.padding(8.dp)) {
                Text("Mode", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.48f))
                Text(domain.label, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.88f), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun CameraPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("PRATINJAU KAMERA", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.12f))
    }
}

@Composable
private fun RuleOfThirds() {
    Canvas(Modifier.fillMaxSize()) {
        val c = Color.White.copy(alpha = 0.06f)
        drawLine(c, Offset(size.width / 3f, 0f), Offset(size.width / 3f, size.height), 1f)
        drawLine(c, Offset(size.width * 2f / 3f, 0f), Offset(size.width * 2f / 3f, size.height), 1f)
        drawLine(c, Offset(0f, size.height / 3f), Offset(size.width, size.height / 3f), 1f)
        drawLine(c, Offset(0f, size.height * 2f / 3f), Offset(size.width, size.height * 2f / 3f), 1f)
    }
}

@Composable
private fun ScannerFrame(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "scan")
    val progress by transition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(tween(1250), RepeatMode.Reverse),
        label = "scanLine"
    )
    Canvas(modifier) {
        val l = 22.dp.toPx()
        val w = 2.dp.toPx()
        fun corner(x: Float, y: Float, sx: Float, sy: Float) {
            drawLine(CyanAccent, Offset(x, y), Offset(x + sx * l, y), w)
            drawLine(CyanAccent, Offset(x, y), Offset(x, y + sy * l), w)
        }
        corner(0f, 0f, 1f, 1f)
        corner(size.width, 0f, -1f, 1f)
        corner(0f, size.height, 1f, -1f)
        corner(size.width, size.height, -1f, -1f)
        drawLine(CyanAccent.copy(alpha = 0.75f), Offset(0f, size.height * progress), Offset(size.width, size.height * progress), 1.5.dp.toPx())
    }
}

@Composable
private fun DarkCircleButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Surface(onClick = onClick, shape = CircleShape, color = Color.Black.copy(alpha = 0.45f), modifier = Modifier.size(40.dp)) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
    }
}
