package com.symbolsense.ui.screens.detection

import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.symbolsense.ai.FormulaDebugStore
import com.symbolsense.ai.RecognitionMode
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
    val symbols = result.symbols
    val symbolCount = symbols.size.coerceAtLeast(1)
    val isMultiPipeline = result.mode != RecognitionMode.ISOLATED
    val debugCrops = FormulaDebugStore.latest

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar(
            title = "Hasil pemindaian",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
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
                            contentDescription = "Gambar yang dikirim ke model",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(
                            text = "Gambar input tidak tersedia",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.60f)
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    )
                ) {
                    Text(
                        text = "$symbolCount simbol dikenali",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(Modifier.height(3.dp))

                    Text(
                        text = if (isMultiPipeline) {
                            "Mode formula • detector → classifier → parser V2"
                        } else {
                            "Mode classifier • 1 crop = 1 simbol"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryLight
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.shapes.medium
                        )
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = result.structuredDisplay.ifBlank { result.best.display },
                        fontFamily = SymbolMono,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = result.structuredLatex.ifBlank { result.best.latex },
                        fontFamily = SymbolMono,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryLight
                    )
                }
            }

            item {
                SDivider(indent = 16)

                Column(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    )
                ) {
                    Text(
                        text = "Detail inferensi",
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(Modifier.height(8.dp))

                    DetailRow(
                        label = "Mode",
                        value = when (result.mode) {
                            RecognitionMode.ISOLATED -> "Isolated classifier"
                            RecognitionMode.MULTI_SYMBOL -> "Multi-symbol"
                            RecognitionMode.DETECTOR_FALLBACK -> "Detector fallback"
                        }
                    )

                    Spacer(Modifier.height(5.dp))

                    DetailRow(
                        label = "Jumlah simbol",
                        value = symbolCount.toString()
                    )

                    Spacer(Modifier.height(5.dp))

                    DetailRow(
                        label = "Detector",
                        value = String.format("%.2f ms", result.detectorInferenceTimeMs)
                    )

                    Spacer(Modifier.height(5.dp))

                    DetailRow(
                        label = "Total pipeline",
                        value = String.format("%.2f ms", result.inferenceTimeMs)
                    )

                    Spacer(Modifier.height(5.dp))

                    DetailRow(
                        label = "LaTeX",
                        value = result.structuredLatex.ifBlank { result.best.latex },
                        mono = true
                    )
                }
            }


            if (debugCrops.isNotEmpty() && isMultiPipeline) {
                item {
                    SDivider(indent = 16)

                    Column(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 14.dp
                        )
                    ) {
                        Text(
                            text = "DEBUG crop → classifier",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(Modifier.height(3.dp))

                        Text(
                            text = "Kiri = crop detector • kanan = input 64×64 classifier",
                            style = MaterialTheme.typography.bodySmall,
                            color = AmberWarning
                        )
                    }
                }

                itemsIndexed(debugCrops) { index, entry ->
                    if (index > 0) {
                        SDivider(indent = 16)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "D${(entry.detectorIndex + 1).toString().padStart(2, '0')}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiaryLight,
                                modifier = Modifier.padding(end = 10.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = entry.rawCrop.asImageBitmap(),
                                    contentDescription = "Raw detector crop",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(Modifier.size(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = entry.canonicalClassifierInput.asImageBitmap(),
                                    contentDescription = "Canonical classifier input",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(Modifier.size(10.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                val bestDebug = entry.classifierTopK.firstOrNull()

                                Text(
                                    text = bestDebug?.let {
                                        "${it.name} ${percent(it.confidence)}"
                                    } ?: "no prediction",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = "det ${percent(entry.detectorConfidence)} • raw ${entry.rawWidth}×${entry.rawHeight}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryLight
                                )
                            }
                        }

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "Top-3: " + entry.classifierTopK.joinToString("  |  ") {
                                "${it.name} ${percent(it.confidence)}"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryLight
                        )

                        Text(
                            text = String.format(
                                "bbox L%.3f T%.3f R%.3f B%.3f",
                                entry.boundingBox.left,
                                entry.boundingBox.top,
                                entry.boundingBox.right,
                                entry.boundingBox.bottom
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiaryLight
                        )
                    }
                }
            }

            item {
                SDivider(indent = 16)
                Text(
                    text = "Simbol terdeteksi",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    )
                )
            }

            if (symbols.isNotEmpty()) {
                itemsIndexed(symbols) { index, symbol ->
                    if (index > 0) {
                        SDivider(indent = 68)
                    }

                    val prediction = symbol.prediction

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}".padStart(2, '0'),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiaryLight,
                            modifier = Modifier.padding(end = 10.dp)
                        )

                        GlyphTile(prediction.display)

                        Spacer(Modifier.size(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = prediction.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "det ${percent(symbol.detectorConfidence)} • ${prediction.latex}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryLight
                            )

                            if (!symbol.reliable) {
                                Text(
                                    text = "Perlu ditinjau",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AmberWarning
                                )
                            }
                        }

                        ConfidenceText(prediction.confidence)
                    }
                }
            } else {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "01",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiaryLight,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        GlyphTile(result.best.display)
                        Spacer(Modifier.size(12.dp))
                        Text(
                            text = result.best.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        ConfidenceText(result.best.confidence)
                    }
                }
            }

            if (symbolCount == 1) {
                item {
                    SDivider(indent = 16)
                    Text(
                        text = "Kandidat classifier",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 14.dp
                        )
                    )
                }

                itemsIndexed(result.topK) { index, prediction ->
                    if (index > 0) {
                        SDivider(indent = 68)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}".padStart(2, '0'),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiaryLight,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        GlyphTile(prediction.display)
                        Spacer(Modifier.size(12.dp))
                        Text(
                            text = prediction.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        ConfidenceText(prediction.confidence)
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            PrimaryActionButton(
                text = "Lihat hasil terstruktur",
                onClick = onViewStructuredResult,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun percent(value: Float): String {
    return "${(value.coerceIn(0f, 1f) * 100f).toInt()}%"
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    mono: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryLight
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = if (mono) {
                SymbolMono
            } else {
                androidx.compose.ui.text.font.FontFamily.Default
            }
        )
    }
}
