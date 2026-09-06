package com.symbolsense.ui.screens.domain

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.theme.IndigoPrimary
import com.symbolsense.ui.theme.TextSecondaryLight
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DomainConfirmationSheet(
    detectedDomain: SymbolDomain,
    confidence: Float,
    onDismiss: () -> Unit,
    onProcess: () -> Unit
) {
    val confidencePercent =
        (confidence.coerceIn(0f, 1f) * 100f).roundToInt()

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 16.dp
                )
        ) {
            Text(
                text = "Domain terdeteksi",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondaryLight
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.medium
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = detectedDomain.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = "Hasil deteksi otomatis",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryLight
                    )
                }

                Text(
                    text = "$confidencePercent%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = IndigoPrimary
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Button(
                onClick = onProcess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = IndigoPrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Lanjutkan",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}