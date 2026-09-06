package com.symbolsense.ui.screens.editor

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.ScanResult
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.components.ConfidenceText
import com.symbolsense.ui.components.GlyphTile
import com.symbolsense.ui.components.SDivider
import com.symbolsense.ui.components.SecondaryActionButton
import com.symbolsense.ui.components.SectionLabel
import com.symbolsense.ui.components.TrailingAction
import com.symbolsense.ui.components.domainCodeLabel
import com.symbolsense.ui.theme.BorderLight
import com.symbolsense.ui.theme.CodeBlack
import com.symbolsense.ui.theme.IndigoPrimary
import com.symbolsense.ui.theme.SymbolMono
import com.symbolsense.ui.theme.TextSecondaryLight
import com.symbolsense.ui.theme.TextTertiaryLight

@Composable
fun ResultEditorScreen(
    result: ScanResult,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onOpenHistory: () -> Unit,
    onExport: () -> Unit
) {
    var tab by remember {
        mutableStateOf(0)
    }

    var copied by remember {
        mutableStateOf(false)
    }

    val codeLabel =
        domainCodeLabel(
            result.domain
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
    ) {

        /*
         * =====================================================
         * TOP BAR
         * =====================================================
         */

        AppTopBar(
            title = "Hasil",
            onBack = onBack,
            trailingIcon = TrailingAction.SHARE,
            onTrailingClick = onShare,
            titleBadge = result.domain.label
        )

        /*
         * =====================================================
         * TAB
         * =====================================================
         */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface
                )
        ) {

            listOf(
                "Pratinjau",
                "Kode $codeLabel"
            ).forEachIndexed { index, title ->

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            tab = index
                        }
                        .padding(
                            top = 11.dp
                        ),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = title,
                        style =
                            MaterialTheme.typography.bodyMedium,
                        fontWeight =
                            if (tab == index) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            },
                        color =
                            if (tab == index) {
                                IndigoPrimary
                            } else {
                                TextSecondaryLight
                            }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                9.dp
                            )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(
                                2.dp
                            )
                            .background(
                                if (tab == index) {
                                    IndigoPrimary
                                } else {
                                    Color.Transparent
                                }
                            )
                    )
                }
            }
        }

        HorizontalDivider(
            color = BorderLight
        )

        /*
         * =====================================================
         * PREVIEW TAB
         * =====================================================
         */

        if (tab == 0) {

            LazyColumn(
                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {

                item {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                16.dp
                            )
                            .background(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.shapes.medium
                            )
                            .padding(
                                horizontal = 20.dp,
                                vertical = 28.dp
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                result.structuredOutput,
                            fontFamily =
                                SymbolMono,
                            style =
                                MaterialTheme.typography.titleMedium,
                            color =
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                item {
                    SectionLabel(
                        "Simbol yang dikenali"
                    )
                }

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
                            modifier =
                                Modifier.size(
                                    12.dp
                                )
                        )

                        Text(
                            text =
                                symbol.label,
                            modifier =
                                Modifier.weight(
                                    1f
                                ),
                            style =
                                MaterialTheme.typography.bodyMedium
                        )

                        ConfidenceText(
                            symbol.confidence
                        )
                    }
                }
            }

        } else {

            /*
             * =================================================
             * CODE TAB
             * =================================================
             */

            Column(
                modifier = Modifier
                    .weight(
                        1f
                    )
                    .padding(
                        16.dp
                    )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            CodeBlack,
                            MaterialTheme.shapes.medium
                        )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 14.dp,
                                vertical = 10.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                codeLabel,
                            style =
                                MaterialTheme.typography.labelSmall,
                            color =
                                Color.White.copy(
                                    alpha = 0.38f
                                ),
                            modifier =
                                Modifier.weight(
                                    1f
                                )
                        )

                        Row(
                            modifier = Modifier
                                .clickable {
                                    copied = true
                                }
                                .padding(
                                    5.dp
                                ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    if (copied) {
                                        Icons.Filled.Check
                                    } else {
                                        Icons.Filled.ContentCopy
                                    },
                                contentDescription = null,
                                tint =
                                    if (copied) {
                                        Color(
                                            0xFF4ADE80
                                        )
                                    } else {
                                        Color.White.copy(
                                            alpha = 0.55f
                                        )
                                    },
                                modifier =
                                    Modifier.size(
                                        14.dp
                                    )
                            )

                            Spacer(
                                modifier =
                                    Modifier.size(
                                        5.dp
                                    )
                            )

                            Text(
                                text =
                                    if (copied) {
                                        "Tersalin"
                                    } else {
                                        "Salin"
                                    },
                                style =
                                    MaterialTheme.typography.labelMedium,
                                color =
                                    if (copied) {
                                        Color(
                                            0xFF4ADE80
                                        )
                                    } else {
                                        Color.White.copy(
                                            alpha = 0.55f
                                        )
                                    }
                            )
                        }
                    }

                    HorizontalDivider(
                        color =
                            Color.White.copy(
                                alpha = 0.08f
                            )
                    )

                    Text(
                        text =
                            result.latexOrCode,
                        fontFamily =
                            SymbolMono,
                        color =
                            Color.White.copy(
                                alpha = 0.80f
                            ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                14.dp
                            ),
                        style =
                            MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )

                Text(
                    text =
                        "Kode hasil pengenalan. Salin jika ingin digunakan di aplikasi lain.",
                    style =
                        MaterialTheme.typography.bodySmall,
                    color =
                        TextTertiaryLight
                )
            }
        }

        /*
         * =====================================================
         * BOTTOM ACTION
         *
         * FIX:
         * navigationBarsPadding() membuat tombol tidak lagi
         * ketabrak navigation bar Android.
         * =====================================================
         */

        HorizontalDivider(
            color = BorderLight
        )

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
                Arrangement.spacedBy(
                    8.dp
                )
        ) {

            SecondaryActionButton(
                text = "Simpan",
                onClick = onOpenHistory,
                modifier =
                    Modifier.weight(
                        1f
                    )
            )

            Button(
                onClick =
                    onExport,
                modifier = Modifier
                    .weight(
                        1f
                    )
                    .height(
                        46.dp
                    ),
                shape =
                    MaterialTheme.shapes.medium,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            IndigoPrimary
                    ),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation =
                            0.dp
                    )
            ) {

                Text(
                    text = "Ekspor"
                )
            }
        }
    }
}