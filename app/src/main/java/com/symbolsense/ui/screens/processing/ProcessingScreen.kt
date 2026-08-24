package com.symbolsense.ui.screens.processing

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.ui.theme.CyanAccent
import com.symbolsense.ui.theme.GreenSuccess
import kotlinx.coroutines.delay

private enum class ProcessingStep(val label: String) {
    PREPROCESS("Memproses gambar"),
    DETECT("Mendeteksi simbol"),
    STRUCTURE("Menyusun struktur"),
    FINALIZE("Menyiapkan hasil")
}

/**
 * Screen 7/15 — Processing / Loading.
 *
 * [onDone] dipanggil otomatis setelah seluruh step "selesai" (simulasi).
 * Saat integrasi ML nyata, ganti [LaunchedEffect] simulasi ini dengan
 * observasi state dari ViewModel (preprocessing -> inference -> postprocessing).
 */
@Composable
fun ProcessingScreen(onDone: () -> Unit) {
    var currentStepIndex by remember { mutableStateOf(0) }
    val steps = ProcessingStep.values()

    LaunchedEffect(Unit) {
        for (i in steps.indices) {
            currentStepIndex = i
            delay(900L)
        }
        currentStepIndex = steps.size // semua selesai
        delay(300L)
        onDone()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.size(48.dp))

        // Preview gambar dengan highlight simbol bergantian
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    text = "∫₀^∞ e^{-x²} dx\n= ∇×B 2\n∇ × B = μ₀J\nC₆H₅OH → φ-OH",
                    color = MaterialTheme.colorScheme.surface,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace
                )

                // Highlight box bergerak sederhana berdasarkan currentStepIndex
                val highlightVisible = currentStepIndex == ProcessingStep.DETECT.ordinal
                if (highlightVisible) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 36.dp, start = 0.dp)
                            .background(CyanAccent.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                            .border(1.dp, CyanAccent, RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            "  ∇ × B  ",
                            color = CyanAccent,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(Modifier.size(40.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            steps.forEachIndexed { index, step ->
                ProcessingStepRow(
                    label = step.label,
                    state = when {
                        index < currentStepIndex -> StepState.DONE
                        index == currentStepIndex -> StepState.ACTIVE
                        else -> StepState.PENDING
                    }
                )
                if (index != steps.lastIndex) Spacer(Modifier.size(16.dp))
            }
        }

        Spacer(Modifier.size(32.dp))

        val activeLabel = steps.getOrNull(currentStepIndex)?.label ?: "Selesai"
        Text(
            "Sedang ${activeLabel.lowercase()}...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private enum class StepState { DONE, ACTIVE, PENDING }

@Composable
private fun ProcessingStepRow(label: String, state: StepState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        when (state) {
            StepState.DONE -> Surface(shape = CircleShape, color = GreenSuccess) {
                Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
            StepState.ACTIVE -> Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = CyanAccent
                )
            }
            StepState.PENDING -> Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            )
        }

        Spacer(Modifier.size(12.dp))

        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (state == StepState.ACTIVE) FontWeight.Bold else FontWeight.Normal,
            color = when (state) {
                StepState.DONE -> GreenSuccess
                StepState.ACTIVE -> MaterialTheme.colorScheme.onBackground
                StepState.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}
