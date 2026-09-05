package com.symbolsense.ui.screens.processing

import android.net.Uri
import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.ai.SymbolAiEffect
import com.symbolsense.ai.SymbolRecognitionResult
import com.symbolsense.ui.theme.CyanAccent
import com.symbolsense.ui.theme.GreenSuccess
import com.symbolsense.ui.theme.TextSecondaryLight
import com.symbolsense.ui.theme.TextTertiaryLight
import kotlinx.coroutines.delay

private const val TAG = "SymbolSenseAI"

@Composable
fun ProcessingScreen(
    imageUri: Uri?,
    onDone: (SymbolRecognitionResult) -> Unit,
    onError: (Throwable) -> Unit = {}
) {

    val steps = listOf(
        "Gambar disiapkan",
        "Menjalankan model AI",
        "Simbol dikenali",
        "Menyiapkan hasil"
    )

    var current by remember(imageUri) {
        mutableIntStateOf(0)
    }

    var aiResult by remember(imageUri) {
        mutableStateOf<SymbolRecognitionResult?>(null)
    }

    var errorMessage by remember(imageUri) {
        mutableStateOf<String?>(null)
    }

    /*
     * =========================================================
     * UI PROGRESS
     * =========================================================
     *
     * Step 0:
     * image diterima
     *
     * Step 1:
     * TFLite sedang dijalankan
     *
     * Step 2:
     * prediction sudah keluar
     *
     * Step 3:
     * siap dikirim ke ResultScreen
     */

    LaunchedEffect(imageUri) {

        current = 0
        aiResult = null
        errorMessage = null

        if (imageUri != null) {

            delay(250)

            current = 1
        }
    }

    /*
     * =========================================================
     * REAL AI INFERENCE
     * =========================================================
     */

    SymbolAiEffect(
        imageUri = imageUri,

        onSuccess = { result ->

            aiResult = result
            current = 2

            Log.d(
                TAG,
                "========================================"
            )

            Log.d(
                TAG,
                "AI RESULT"
            )

            Log.d(
                TAG,
                "id          = ${result.best.id}"
            )

            Log.d(
                TAG,
                "name        = ${result.best.name}"
            )

            Log.d(
                TAG,
                "display     = ${result.best.display}"
            )

            Log.d(
                TAG,
                "latex       = ${result.best.latex}"
            )

            Log.d(
                TAG,
                "confidence  = ${result.best.confidence}"
            )

            Log.d(
                TAG,
                "reliable    = ${result.reliable}"
            )

            Log.d(
                TAG,
                "time        = ${result.inferenceTimeMs} ms"
            )

            Log.d(
                TAG,
                "TOP ${result.topK.size}"
            )

            result.topK.forEachIndexed { index, prediction ->

                Log.d(
                    TAG,
                    "${index + 1}. " +
                            "${prediction.display} | " +
                            "${prediction.name} | " +
                            "${prediction.confidence}"
                )
            }

            Log.d(
                TAG,
                "========================================"
            )
        },

        onError = { throwable ->

            Log.e(
                TAG,
                "AI inference failed",
                throwable
            )

            errorMessage =
                throwable.message
                    ?: "Terjadi kesalahan saat menjalankan AI."

            onError(
                throwable
            )
        }
    )

    /*
     * =========================================================
     * AFTER AI SUCCESS
     * =========================================================
     */

    LaunchedEffect(aiResult) {

        val result =
            aiResult
                ?: return@LaunchedEffect

        delay(300)

        current = 3

        delay(350)

        /*
         * Kirim REAL AI result
         * ke navigation / ResultScreen.
         */
        onDone(
            result
        )
    }

    /*
     * =========================================================
     * UI
     * =========================================================
     */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(
                horizontal = 24.dp
            ),

        verticalArrangement =
            Arrangement.Center
    ) {

        ScanMiniAnimation()

        Spacer(
            Modifier.height(
                28.dp
            )
        )

        Text(
            text = if (errorMessage == null) {
                "Menganalisis gambar"
            } else {
                "Gagal menganalisis"
            },

            style =
                MaterialTheme.typography.titleLarge
        )

        Spacer(
            Modifier.height(
                6.dp
            )
        )

        Text(
            text = when {

                errorMessage != null -> {
                    errorMessage
                        ?: "Terjadi kesalahan."
                }

                aiResult != null -> {

                    val confidence =
                        (
                                aiResult!!
                                    .best
                                    .confidence *
                                        100f
                                ).toInt()

                    "Simbol ${
                        aiResult!!
                            .best
                            .display
                    } dikenali dengan confidence $confidence%."
                }

                else -> {
                    "Menjalankan pengenalan simbol dengan AI."
                }
            },

            style =
                MaterialTheme.typography.bodyMedium,

            color = if (
                errorMessage == null
            ) {
                TextSecondaryLight
            } else {
                MaterialTheme.colorScheme.error
            }
        )

        Spacer(
            Modifier.height(
                28.dp
            )
        )

        steps.forEachIndexed { index, step ->

            val done =
                index < current

            val active =
                index == current

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 9.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                when {

                    done -> {

                        Icon(
                            imageVector =
                                Icons.Filled.Check,

                            contentDescription =
                                null,

                            tint =
                                GreenSuccess,

                            modifier =
                                Modifier.size(
                                    18.dp
                                )
                        )
                    }

                    active -> {

                        Icon(
                            imageVector =
                                Icons.Filled.Circle,

                            contentDescription =
                                null,

                            tint =
                                CyanAccent,

                            modifier =
                                Modifier.size(
                                    12.dp
                                )
                        )
                    }

                    else -> {

                        Icon(
                            imageVector =
                                Icons.Filled.Circle,

                            contentDescription =
                                null,

                            tint =
                                TextTertiaryLight
                                    .copy(
                                        alpha = 0.45f
                                    ),

                            modifier =
                                Modifier.size(
                                    10.dp
                                )
                        )
                    }
                }

                Spacer(
                    Modifier.size(
                        12.dp
                    )
                )

                Text(
                    text = step,

                    style =
                        MaterialTheme.typography.bodyMedium,

                    color =
                        if (
                            active || done
                        ) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            TextTertiaryLight
                        },

                    fontWeight =
                        if (active) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        }
                )
            }
        }

        /*
         * =====================================================
         * OPTIONAL DEBUG RESULT
         * =====================================================
         *
         * Ini sementara berguna untuk memastikan
         * AI benar-benar bekerja di Android.
         */

        aiResult?.let { result ->

            Spacer(
                Modifier.height(
                    24.dp
                )
            )

            Text(
                text =
                    "AI: ${result.best.display}  •  " +
                            "${(result.best.confidence * 100).toInt()}%",

                style =
                    MaterialTheme.typography.bodySmall,

                color =
                    CyanAccent,

                fontWeight =
                    FontWeight.SemiBold
            )

            Text(
                text =
                    "Inference: " +
                            String.format(
                                "%.2f ms",
                                result.inferenceTimeMs
                            ),

                style =
                    MaterialTheme.typography.bodySmall,

                color =
                    TextTertiaryLight
            )
        }
    }
}


/*
 * =============================================================
 * SCANNING ANIMATION
 * =============================================================
 */

@Composable
private fun ScanMiniAnimation() {

    val transition =
        rememberInfiniteTransition(
            label = "processing"
        )

    val progress by
    transition.animateFloat(
        initialValue = 0.12f,

        targetValue = 0.88f,

        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 900
                    ),

                repeatMode =
                    RepeatMode.Reverse
            ),

        label = "line"
    )

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(
                    90.dp
                )
                .background(
                    color =
                        MaterialTheme
                            .colorScheme
                            .surface,

                    shape =
                        MaterialTheme
                            .shapes
                            .medium
                )
    ) {

        Canvas(
            modifier =
                Modifier.fillMaxSize()
        ) {

            drawLine(
                color =
                    CyanAccent.copy(
                        alpha = 0.85f
                    ),

                start =
                    Offset(
                        x =
                            size.width *
                                    0.08f,

                        y =
                            size.height *
                                    progress
                    ),

                end =
                    Offset(
                        x =
                            size.width *
                                    0.92f,

                        y =
                            size.height *
                                    progress
                    ),

                strokeWidth =
                    1.5.dp.toPx()
            )
        }
    }
}