package com.symbolsense.ui.screens.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.TrailingAction
import com.symbolsense.ui.theme.CyanAccent
import com.symbolsense.ui.theme.IndigoPrimary
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack

/**
 * Screen 5/15 — Image Preview & Crop.
 *
 * NOTE: Crop handles di sini bersifat dekoratif/statis. Untuk implementasi nyata,
 * pertimbangkan library seperti uCrop atau gambar handle draggable dengan
 * pointerInput + offset state per-corner.
 */
@Composable
fun ImagePreviewScreen(
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    var autoPerspective by remember { mutableStateOf(true) }
    var contrastBoost by remember { mutableStateOf(false) }
    var rotation by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1115))
    ) {
        AppTopBarDark(
            title = "Sesuaikan Gambar",
            onBack = onBack,
            onConfirm = onConfirm
        )

        Spacer(Modifier.size(24.dp))

        // Preview area dengan crop handles
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(240.dp)
                .background(Color(0xFF1A1D24), RoundedCornerShape(12.dp))
        ) {
            // Garis bantu grid 3x3
            GridLines()

            Text(
                text = "∫₀^1 f(x) dx\n= lim_{n→∞} Σf(xᵢ)Δx\n= F(1) - F(0)",
                color = Color(0xFF9CA3AF),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.Center)
            )

            // Crop handles di 4 sudut + tengah sisi
            CropHandle(Modifier.align(Alignment.TopStart))
            CropHandle(Modifier.align(Alignment.TopEnd))
            CropHandle(Modifier.align(Alignment.BottomStart))
            CropHandle(Modifier.align(Alignment.BottomEnd))
            CropHandle(Modifier.align(Alignment.TopCenter), filled = false)
            CropHandle(Modifier.align(Alignment.CenterStart), filled = false)
            CropHandle(Modifier.align(Alignment.CenterEnd), filled = false)
            CropHandle(Modifier.align(Alignment.BottomCenter), filled = false)
        }

        Spacer(Modifier.weight(1f))

        // Toolbar toggles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ToolToggle(
                icon = Icons.Filled.Replay,
                label = "Rotasi",
                active = false,
                onClick = { rotation = (rotation + 90) % 360 },
                modifier = Modifier.weight(1f)
            )
            ToolToggle(
                icon = Icons.Filled.CropFree,
                label = "Auto-perspektif",
                active = autoPerspective,
                onClick = { autoPerspective = !autoPerspective },
                modifier = Modifier.weight(1f)
            )
            ToolToggle(
                icon = Icons.Filled.BrightnessHigh,
                label = "Kontras",
                active = contrastBoost,
                onClick = { contrastBoost = !contrastBoost },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.size(20.dp))

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
        ) {
            Text("Lanjutkan", modifier = Modifier.padding(vertical = 6.dp))
        }

        Spacer(Modifier.size(24.dp))
    }
}

@Composable
private fun AppTopBarDark(title: String, onBack: () -> Unit, onConfirm: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.08f), onClick = onBack) {
            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                Icon(
                    androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.White
                )
            }
        }
        Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium)
        Surface(shape = CircleShape, color = CyanAccent.copy(alpha = 0.15f), onClick = onConfirm) {
            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Check, contentDescription = "Lanjut", tint = CyanAccent)
            }
        }
    }
}

@Composable
private fun GridLines() {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            repeat(3) { index ->
                Spacer(Modifier.weight(1f))
                if (index < 2) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(Color.White.copy(alpha = 0.08f))
                    )
                }
            }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            repeat(3) { index ->
                Spacer(Modifier.weight(1f))
                if (index < 2) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.08f))
                    )
                }
            }
        }
    }
}

@Composable
private fun CropHandle(modifier: Modifier, filled: Boolean = true) {
    Box(
        modifier = modifier
            .padding(6.dp)
            .size(14.dp)
            .background(
                color = if (filled) CyanAccent else Color.Transparent,
                shape = CircleShape
            )
            .then(
                if (!filled) Modifier.border(2.dp, CyanAccent, CircleShape) else Modifier
            )
    )
}

@Composable
private fun ToolToggle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (active) CyanAccent.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
        border = if (active) androidx.compose.foundation.BorderStroke(1.dp, CyanAccent) else null,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = if (active) CyanAccent else Color.White)
            Spacer(Modifier.size(4.dp))
            Text(label, color = Color.White, style = MaterialTheme.typography.labelMedium)
        }
    }
}
