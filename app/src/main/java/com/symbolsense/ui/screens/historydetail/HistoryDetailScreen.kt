package com.symbolsense.ui.screens.historydetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.symbolsense.data.model.ScanResult
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.ConfidenceText
import com.symbolsense.ui.components.GlyphTile
import com.symbolsense.ui.components.SDivider
import com.symbolsense.ui.components.SectionLabel
import com.symbolsense.ui.components.domainCodeLabel
import com.symbolsense.ui.theme.CodeBlack
import com.symbolsense.ui.theme.SymbolMono
import com.symbolsense.ui.theme.TextSecondaryLight
import com.symbolsense.ui.theme.TextTertiaryLight

@Composable
fun HistoryDetailScreen(
    result: ScanResult,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onExport: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
    ) {

        AppTopBar(
            title = "Detail riwayat",
            subtitle =
                result.timestampLabel,
            onBack = onBack
        )

        LazyColumn(
            modifier =
                Modifier.weight(1f)
        ) {

            /*
             * =================================================
             * SCANNED IMAGE
             * =================================================
             */

            if (!result.imageUri.isNullOrBlank()) {

                item {

                    SectionLabel(
                        "Gambar yang dipindai"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            )
                            .background(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.shapes.medium
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        AsyncImage(
                            model =
                                result.imageUri,
                            contentDescription =
                                "Gambar riwayat",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentScale =
                                ContentScale.Fit
                        )
                    }
                }
            }

            /*
             * =================================================
             * CONTENT
             * =================================================
             */

            item {

                SectionLabel(
                    "Konten yang dikenali"
                )
            }

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surface
                        )
                        .padding(16.dp)
                ) {

                    Text(
                        text =
                            result.rawPreviewText,
                        fontFamily =
                            SymbolMono,
                        style =
                            MaterialTheme.typography.bodyLarge
                    )

                    Spacer(
                        Modifier.size(6.dp)
                    )

                    Text(
                        text =
                            result.domain.label,
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            TextSecondaryLight
                    )
                }

                SDivider()
            }

            /*
             * =================================================
             * DETECTED SYMBOLS
             * =================================================
             */

            item {

                SectionLabel(
                    "Simbol terdeteksi"
                )
            }

            if (
                result.detectedSymbols.isEmpty()
            ) {

                item {

                    Text(
                        text =
                            "Tidak ada simbol yang tersimpan.",
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 14.dp
                        ),
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            TextTertiaryLight
                    )
                }

            } else {

                itemsIndexed(
                    result.detectedSymbols
                ) { index, symbol ->

                    if (index > 0) {

                        SDivider(
                            indent = 56
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surface
                            )
                            .padding(
                                horizontal = 16.dp,
                                vertical = 11.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        GlyphTile(
                            symbol.displayGlyph,
                            size = 32
                        )

                        Spacer(
                            Modifier.size(12.dp)
                        )

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    symbol.label,
                                style =
                                    MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text =
                                    "Matematika",
                                style =
                                    MaterialTheme.typography.bodySmall,
                                color =
                                    TextSecondaryLight
                            )
                        }

                        ConfidenceText(
                            symbol.confidence
                        )
                    }
                }
            }

            /*
             * =================================================
             * STRUCTURED RESULT
             * =================================================
             */

            item {

                SectionLabel(
                    "Hasil terstruktur"
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CodeBlack)
                        .padding(16.dp)
                ) {

                    Text(
                        text =
                            domainCodeLabel(
                                result.domain
                            ),
                        style =
                            MaterialTheme.typography.labelSmall,
                        color =
                            Color.White.copy(
                                alpha = 0.40f
                            )
                    )

                    Spacer(
                        Modifier.size(8.dp)
                    )

                    Text(
                        text =
                            result.latexOrCode,
                        fontFamily =
                            SymbolMono,
                        color =
                            Color.White.copy(
                                alpha = 0.82f
                            ),
                        style =
                            MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {

                Spacer(
                    Modifier.height(12.dp)
                )
            }
        }

        /*
         * =====================================================
         * SAFE BOTTOM ACTION
         * =====================================================
         */

        SDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface
                )
                .navigationBarsPadding()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick =
                    onDelete
            ) {

                Icon(
                    imageVector =
                        Icons.Filled.Delete,
                    contentDescription =
                        "Hapus"
                )
            }

            Button(
                onClick =
                    onExport,
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                shape =
                    MaterialTheme.shapes.medium,
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp
                    )
            ) {

                Text(
                    text = "Ekspor"
                )
            }
        }
    }
}