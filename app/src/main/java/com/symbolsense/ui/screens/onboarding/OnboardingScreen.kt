package com.symbolsense.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.symbolsense.ui.theme.AmberWarning
import com.symbolsense.ui.theme.BorderLight
import com.symbolsense.ui.theme.CyanAccent
import com.symbolsense.ui.theme.IndigoPrimary
import com.symbolsense.ui.theme.SurfaceRaisedLight
import com.symbolsense.ui.theme.SymbolMono
import com.symbolsense.ui.theme.TextSecondaryLight

private data class Slide(
    val title: String,
    val body: String,
    val type: Int
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val slides = listOf(
        Slide(
            "Pindai simbol apa pun",
            "Arahkan kamera ke simbol matematika, kimia, atau elektronika, atau pilih gambar dari galeri.",
            0
        ),
        Slide(
            "Lihat apa yang ditemukan",
            "Setiap simbol menampilkan nama, domain, dan tingkat kepercayaan. Deteksi yang meragukan ditandai untuk ditinjau.",
            1
        ),
        Slide(
            "Simpan dan ekspor",
            "Lihat hasil terstruktur dalam LaTeX, SMILES, Netlist, atau teks biasa lalu simpan ke riwayat atau ekspor.",
            2
        )
    )

    var index by remember { mutableIntStateOf(0) }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                "Lewati",
                modifier = Modifier
                    .clickable(onClick = onFinish)
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryLight
            )
        }

        AnimatedContent(
            targetState = index,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.weight(1f),
            label = "onboarding"
        ) { i ->
            val slide = slides[i]
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                OnboardingVisual(slide.type)
                Spacer(Modifier.height(30.dp))
                Text(slide.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(
                    slide.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryLight
                )
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                slides.indices.forEach { i ->
                    Box(
                        Modifier
                            .size(
                                width = if (i == index) 20.dp else 6.dp,
                                height = 6.dp
                            )
                            .background(
                                if (i == index) IndigoPrimary else BorderLight,
                                CircleShape
                            )
                            .clickable { index = i }
                    )
                }
            }

            Button(
                onClick = {
                    if (index == slides.lastIndex) onFinish() else index++
                },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(if (index == slides.lastIndex) "Mulai" else "Lanjut")
            }
        }
    }
}

@Composable
private fun OnboardingVisual(type: Int) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(SurfaceRaisedLight, shape)
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        when (type) {
            0 -> ScanVisual()
            1 -> DetectionVisual()
            else -> ExportVisual()
        }
    }
}

@Composable
private fun ScanVisual() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "∫  x²  dx   ≤   √x",
            fontFamily = SymbolMono,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium
        )

        Canvas(Modifier.fillMaxSize()) {
            val corner = 20.dp.toPx()
            val stroke = 2.dp.toPx()
            val left = size.width * 0.08f
            val right = size.width * 0.92f
            val top = size.height * 0.20f
            val bottom = size.height * 0.80f

            fun corner(x: Float, y: Float, sx: Float, sy: Float) {
                drawLine(
                    CyanAccent,
                    Offset(x, y),
                    Offset(x + sx * corner, y),
                    stroke
                )
                drawLine(
                    CyanAccent,
                    Offset(x, y),
                    Offset(x, y + sy * corner),
                    stroke
                )
            }

            corner(left, top, 1f, 1f)
            corner(right, top, -1f, 1f)
            corner(left, bottom, 1f, -1f)
            corner(right, bottom, -1f, -1f)
        }
    }
}

@Composable
private fun DetectionVisual() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DetectionRow("∫", "Integral", "97%", CyanAccent)
        DetectionRow("x²", "Eksponen", "93%", CyanAccent)
        DetectionRow("≤", "Kurang dari / sama dengan", "58%", AmberWarning)
    }
}

@Composable
private fun DetectionRow(
    glyph: String,
    name: String,
    confidence: String,
    accent: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(accent.copy(alpha = 0.10f), RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                glyph,
                fontFamily = SymbolMono,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Box(
                Modifier
                    .fillMaxWidth(if (accent == AmberWarning) 0.58f else 0.88f)
                    .height(3.dp)
                    .background(accent, CircleShape)
            )
        }

        Text(
            confidence,
            fontFamily = SymbolMono,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = accent
        )
    }
}

@Composable
private fun ExportVisual() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "HASIL TERSTRUKTUR",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryLight,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "\\int x^2 \\, dx = \\frac{x^3}{3} + C",
            fontFamily = SymbolMono,
            style = MaterialTheme.typography.titleMedium
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniPill("LaTeX")
            MiniPill("PDF")
            MiniPill("DOCX")
        }
    }
}

@Composable
private fun MiniPill(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(50)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp),
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondaryLight
    )
}
