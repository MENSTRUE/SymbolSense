package com.symbolsense.ui.screens.onboarding

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val previewCode: String
)

private val pages = listOf(
    OnboardingPage(
        title = "Scan Simbol Apa Saja",
        subtitle = "Matematika, kimia, sampai skema elektronika — semua dalam satu aplikasi.",
        previewCode = "∫₀^∞ f(x)dx = 1\nE = mc²\n∇·B = 0"
    ),
    OnboardingPage(
        title = "Deteksi Akurat, Offline",
        subtitle = "AI mengenali simbol langsung di perangkat, tanpa internet.",
        previewCode = "[∫ 97%] [x² 93%] [dx 88%]\n[√ 62%] [≤ 58%]"
    ),
    OnboardingPage(
        title = "Edit & Export Instan",
        subtitle = "Salin ke catatan, simpan sebagai PDF, atau bagikan langsung.",
        previewCode = "∫ x² dx = x³/3 + C\n> Export: PDF · DOCX · LaTeX"
    )
)

/**
 * Screen 2/15 — Onboarding (3 slide carousel).
 */
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // "Lewati" di kanan atas
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onFinish) {
                Text("Lewati", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(pages[page])
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Page indicator dots
            Row {
                pages.indices.forEach { index ->
                    val isActive = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .size(if (isActive) 24.dp else 8.dp, 8.dp)
                            .background(
                                color = if (isActive) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }

            Button(
                onClick = {
                    if (pagerState.currentPage < pages.lastIndex) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onFinish()
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (pagerState.currentPage < pages.lastIndex) "Lanjut" else "Mulai")
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Preview "scan" card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    text = page.previewCode,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.surface,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
                // Scan line aksen
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .fillMaxWidth(0.4f)
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                )
            }
        }

        Spacer(Modifier.size(32.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.size(8.dp))

        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
