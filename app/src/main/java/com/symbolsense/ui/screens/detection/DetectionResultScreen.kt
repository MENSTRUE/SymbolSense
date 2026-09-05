package com.symbolsense.ui.screens.detection

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.symbolsense.ai.SymbolRecognitionResult
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.ConfidenceText
import com.symbolsense.ui.components.GlyphTile
import com.symbolsense.ui.components.PrimaryActionButton
import com.symbolsense.ui.components.SDivider
import com.symbolsense.ui.theme.AmberWarning
import com.symbolsense.ui.theme.CameraBlack
import com.symbolsense.ui.theme.SymbolMono
import com.symbolsense.ui.theme.TextSecondaryLight
import com.symbolsense.ui.theme.TextTertiaryLight

@Composable
fun DetectionResultScreen(
    result: SymbolRecognitionResult,
    imageUri: Uri?,
    onBack: () -> Unit,
    onViewStructuredResult: () -> Unit
) {

    val best = result.best

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
    ) {

        AppTopBar(
            title = "Hasil pemindaian",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            /*
             * =================================================
             * GAMBAR YANG BENAR-BENAR MASUK KE MODEL
             * =================================================
             *
             * Ini penting buat debugging.
             *
             * Jadi sekarang kita bisa melihat:
             *
             * Foto/Crop yang dikirim
             * ↓
             * hasil model
             */

            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(CameraBlack),
                    contentAlignment = Alignment.Center
                ) {

                    if (imageUri != null) {

                        AsyncImage(
                            model = imageUri,
                            contentDescription =
                                "Gambar yang dikirim ke model",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            contentScale = ContentScale.Fit
                        )

                    } else {

                        Text(
                            text = "Gambar input tidak tersedia",
                            style =
                                MaterialTheme.typography.bodyMedium,
                            color =
                                Color.White.copy(alpha = 0.60f)
                        )
                    }
                }
            }

            /*
             * =================================================
             * MAIN AI RESULT
             * =================================================
             */

            item {

                Column(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    )
                ) {

                    Text(
                        text = "1 simbol dikenali",
                        style =
                            MaterialTheme.typography.titleSmall
                    )

                    Spacer(
                        Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            "Mode classifier • 1 crop = 1 simbol",
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            TextSecondaryLight
                    )
                }
            }

            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "01",
                        style =
                            MaterialTheme.typography.labelSmall,
                        color =
                            TextTertiaryLight,
                        modifier =
                            Modifier.padding(end = 10.dp)
                    )

                    GlyphTile(
                        best.display
                    )

                    Spacer(
                        Modifier.size(12.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = best.name,
                            style =
                                MaterialTheme.typography.bodyMedium,
                            fontWeight =
                                FontWeight.Medium
                        )

                        Text(
                            text =
                                "Matematika • Prediksi utama",
                            style =
                                MaterialTheme.typography.bodySmall,
                            color =
                                TextSecondaryLight
                        )

                        if (!result.reliable) {

                            Text(
                                text = "Perlu ditinjau",
                                style =
                                    MaterialTheme.typography.bodySmall,
                                color =
                                    AmberWarning
                            )
                        }
                    }

                    ConfidenceText(
                        best.confidence
                    )
                }
            }

            /*
             * =================================================
             * DETAIL INFERENCE
             * =================================================
             */

            item {

                SDivider(
                    indent = 16
                )

                Column(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    )
                ) {

                    Text(
                        text = "Detail inferensi",
                        style =
                            MaterialTheme.typography.titleSmall
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    DetailRow(
                        label = "Class ID",
                        value =
                            best.id.toString()
                    )

                    Spacer(
                        Modifier.height(5.dp)
                    )

                    DetailRow(
                        label = "Class",
                        value = best.name
                    )

                    Spacer(
                        Modifier.height(5.dp)
                    )

                    DetailRow(
                        label = "LaTeX",
                        value = best.latex,
                        mono = true
                    )

                    Spacer(
                        Modifier.height(5.dp)
                    )

                    DetailRow(
                        label = "Confidence",
                        value =
                            "${(best.confidence * 100f).toInt()}%"
                    )

                    Spacer(
                        Modifier.height(5.dp)
                    )

                    DetailRow(
                        label = "Inference",
                        value = String.format(
                            "%.2f ms",
                            result.inferenceTimeMs
                        )
                    )
                }
            }

            /*
             * =================================================
             * TOP-K
             * =================================================
             */

            item {

                SDivider(
                    indent = 16
                )

                Text(
                    text = "Kandidat model",
                    style =
                        MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    )
                )
            }

            itemsIndexed(
                result.topK
            ) { index, prediction ->

                if (index > 0) {

                    SDivider(
                        indent = 68
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            "${index + 1}"
                                .padStart(2, '0'),
                        style =
                            MaterialTheme.typography.labelSmall,
                        color =
                            TextTertiaryLight,
                        modifier =
                            Modifier.padding(end = 10.dp)
                    )

                    GlyphTile(
                        prediction.display
                    )

                    Spacer(
                        Modifier.size(12.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                prediction.name,
                            style =
                                MaterialTheme.typography.bodyMedium,
                            fontWeight =
                                FontWeight.Medium
                        )

                        Text(
                            text =
                                if (index == 0) {
                                    "Prediksi utama"
                                } else {
                                    "Alternatif ${index + 1}"
                                },
                            style =
                                MaterialTheme.typography.bodySmall,
                            color =
                                TextSecondaryLight
                        )
                    }

                    ConfidenceText(
                        prediction.confidence
                    )
                }
            }

            item {
                Spacer(
                    Modifier.height(8.dp)
                )
            }
        }

        /*
         * =====================================================
         * SAFE BOTTOM AREA
         * =====================================================
         */

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.background
                )
                .navigationBarsPadding()
                .padding(
                    horizontal = 16.dp,
                    vertical = 10.dp
                )
        ) {

            PrimaryActionButton(
                text =
                    "Lihat hasil terstruktur",
                onClick =
                    onViewStructuredResult,
                modifier =
                    Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    mono: Boolean = false
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = label,
            modifier =
                Modifier.weight(1f),
            style =
                MaterialTheme.typography.bodySmall,
            color =
                TextSecondaryLight
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.bodySmall,
            fontFamily =
                if (mono) {
                    SymbolMono
                } else {
                    androidx.compose.ui.text.font.FontFamily.Default
                }
        )
    }
}