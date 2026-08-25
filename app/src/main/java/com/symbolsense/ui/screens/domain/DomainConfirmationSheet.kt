package com.symbolsense.ui.screens.domain

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
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
import com.symbolsense.ui.components.PrimaryActionButton
import com.symbolsense.ui.components.SDivider
import com.symbolsense.ui.theme.GreenContainer
import com.symbolsense.ui.theme.GreenSuccess
import com.symbolsense.ui.theme.IndigoPrimary
import com.symbolsense.ui.theme.TextSecondaryLight
import com.symbolsense.ui.theme.TextTertiaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DomainConfirmationSheet(
    detectedDomain: SymbolDomain,
    confidence: Float,
    onDismiss: () -> Unit,
    onProcess: (SymbolDomain) -> Unit
) {
    var selected by remember { mutableStateOf(detectedDomain) }
    val options = listOf(SymbolDomain.MATH, SymbolDomain.CHEMISTRY, SymbolDomain.ELECTRONICS, SymbolDomain.GENERAL)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { androidx.compose.material3.BottomSheetDefaults.DragHandle() }
    ) {
        Column(Modifier.fillMaxWidth().navigationBarsPadding()) {
            Text("DOMAIN TERDETEKSI", style = MaterialTheme.typography.labelSmall, color = TextTertiaryLight, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("∫", style = MaterialTheme.typography.titleLarge, color = IndigoPrimary)
                Spacer(Modifier.size(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(selected.label, style = MaterialTheme.typography.titleMedium)
                    Text("Hasil deteksi otomatis", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                }
                androidx.compose.material3.Surface(color = GreenContainer, shape = MaterialTheme.shapes.extraLarge) {
                    Row(Modifier.padding(horizontal = 9.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Check, null, tint = GreenSuccess, modifier = Modifier.size(13.dp)); Spacer(Modifier.size(4.dp))
                        Text("${(confidence * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = GreenSuccess, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Text("Bukan domain yang tepat? Pilih manual:", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
            SDivider()
            options.forEachIndexed { index, domain ->
                if (index > 0) SDivider(indent = 16)
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { selected = domain }.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selected == domain, onClick = { selected = domain })
                    Text(domain.label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                }
            }
            SDivider()
            PrimaryActionButton("Lanjutkan", onClick = { onProcess(selected) }, modifier = Modifier.padding(16.dp))
        }
    }
}
