package com.symbolsense.ui.screens.domain

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.components.DomainChip
import com.symbolsense.ui.components.domainIcon
import com.symbolsense.ui.theme.DomainColors
import com.symbolsense.ui.theme.GreenSuccess

/**
 * Screen 6/15 — Domain Confirmation Bottom Sheet.
 *
 * [detectedDomain] adalah hasil prediksi domain classifier, [confidence] dalam 0f..1f.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DomainConfirmationSheet(
    detectedDomain: SymbolDomain,
    confidence: Float,
    onDismiss: () -> Unit,
    onProcess: (SymbolDomain) -> Unit
) {
    var selected by remember { mutableStateOf(detectedDomain) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        domainIcon(detectedDomain),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Domain Terdeteksi:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        detectedDomain.label,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GreenSuccess.copy(alpha = 0.15f)
                ) {
                    Text(
                        "${(confidence * 100).toInt()}%",
                        color = GreenSuccess,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.size(16.dp))

            Text(
                "Bukan domain yang tepat? Pilih manual:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.size(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(SymbolDomain.MATH, SymbolDomain.CHEMISTRY, SymbolDomain.ELECTRONICS).forEach { d ->
                    DomainChip(
                        label = d.label,
                        icon = domainIcon(d),
                        selected = selected == d,
                        onClick = { selected = d },
                        accentColor = domainAccent(d)
                    )
                }
            }

            Spacer(Modifier.size(8.dp))

            Row {
                DomainChip(
                    label = SymbolDomain.GENERAL.label,
                    icon = domainIcon(SymbolDomain.GENERAL),
                    selected = selected == SymbolDomain.GENERAL,
                    onClick = { selected = SymbolDomain.GENERAL },
                    accentColor = domainAccent(SymbolDomain.GENERAL)
                )
            }

            Spacer(Modifier.size(20.dp))

            Button(
                onClick = { onProcess(selected) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Proses Sekarang", modifier = Modifier.padding(vertical = 6.dp))
            }

            Spacer(Modifier.size(8.dp))
        }
    }
}

private fun domainAccent(domain: SymbolDomain) = when (domain) {
    SymbolDomain.MATH -> DomainColors.MathText
    SymbolDomain.CHEMISTRY -> DomainColors.ChemText
    SymbolDomain.ELECTRONICS -> DomainColors.ElectroText
    else -> DomainColors.GeneralText
}
