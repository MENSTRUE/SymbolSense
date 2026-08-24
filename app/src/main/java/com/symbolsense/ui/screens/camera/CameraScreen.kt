package com.symbolsense.ui.screens.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.components.DomainChip
import com.symbolsense.ui.components.domainIcon
import com.symbolsense.ui.theme.CyanAccent

/**
 * Screen 4/15 — Camera Capture.
 *
 * NOTE: Preview kamera live (CameraX PreviewView) belum diintegrasikan di sini.
 * Ganti [CameraPreviewPlaceholder] dengan AndroidView yang membungkus
 * androidx.camera.view.PreviewView ketika menghubungkan CameraX.
 */
@Composable
fun CameraScreen(
    onClose: () -> Unit,
    onCapture: () -> Unit,
    onOpenGallery: () -> Unit
) {
    var domain by remember { mutableStateOf(SymbolDomain.AUTO) }
    var flashOn by remember { mutableStateOf(false) }
    var showDomainPicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1115))
    ) {
        CameraPreviewPlaceholder()

        // Grid guide
        GridGuideOverlay()

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CircleIconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = Color.White)
            }
            CircleIconButton(onClick = { flashOn = !flashOn }) {
                Icon(
                    Icons.Filled.FlashOn,
                    contentDescription = "Flash",
                    tint = if (flashOn) CyanAccent else Color.White
                )
            }
        }

        // Instruksi tengah
        Text(
            text = "Posisikan simbol di dalam kotak",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Domain picker chip row (opsional, di atas bottom controls)
        if (showDomainPicker) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 110.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(SymbolDomain.AUTO, SymbolDomain.MATH, SymbolDomain.CHEMISTRY, SymbolDomain.ELECTRONICS)
                    .forEach { d ->
                        DomainChip(
                            label = d.label,
                            icon = domainIcon(d),
                            selected = domain == d,
                            onClick = { domain = d },
                            accentColor = CyanAccent
                        )
                    }
            }
        }

        // Bottom controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconButton(onClick = onOpenGallery, background = Color.White.copy(alpha = 0.12f)) {
                Icon(Icons.Filled.Image, contentDescription = "Galeri", tint = Color.White)
            }

            // Shutter button
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(2.dp, CyanAccent),
                    onClick = onCapture,
                    modifier = Modifier.fillMaxSize()
                ) {}
            }

            CircleIconButton(
                onClick = { showDomainPicker = !showDomainPicker },
                background = Color.White.copy(alpha = 0.12f)
            ) {
                Icon(Icons.Filled.Sync, contentDescription = "Pilih domain", tint = Color.White)
            }
        }
    }
}

@Composable
private fun CameraPreviewPlaceholder() {
    // Placeholder untuk live preview kamera (CameraX PreviewView)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF15171D))
    )
}

@Composable
private fun GridGuideOverlay() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Garis vertikal & horizontal tipis (rule of thirds) + scan-frame corner brackets
        // bisa diimplementasikan dengan Canvas drawLine; disederhanakan jadi border frame di sini
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.85f)
                .height(220.dp)
                .border(
                    width = 1.dp,
                    color = CyanAccent.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp)
                )
        )
    }
}

@Composable
private fun CircleIconButton(
    onClick: () -> Unit,
    background: Color = Color.Black.copy(alpha = 0.4f),
    content: @Composable () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = background,
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
