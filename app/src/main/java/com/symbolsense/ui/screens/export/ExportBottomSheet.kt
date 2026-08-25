package com.symbolsense.ui.screens.export

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.ui.components.SDivider
import com.symbolsense.ui.components.SecondaryActionButton
import com.symbolsense.ui.theme.TextSecondaryLight
import com.symbolsense.ui.theme.TextTertiaryLight

private data class ExportOption(val title: String, val subtitle: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportBottomSheet(
    codeLabel: String,
    onDismiss: () -> Unit,
    onAction: (String) -> Unit
) {
    val options = listOf(
        ExportOption("Salin teks", "Teks biasa ke papan klip", Icons.Filled.ContentCopy),
        ExportOption("Salin $codeLabel", "Kode $codeLabel ke papan klip", Icons.Filled.ContentCopy),
        ExportOption("Ekspor PDF", "Simpan sebagai dokumen PDF", Icons.Filled.Description),
        ExportOption("Ekspor DOCX", "Simpan sebagai dokumen Word", Icons.Filled.Description),
        ExportOption("Bagikan…", "Buka opsi berbagi sistem", Icons.Filled.Share)
    )
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxWidth().navigationBarsPadding()) {
            Text("EKSPOR HASIL", style = MaterialTheme.typography.labelSmall, color = TextTertiaryLight, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            SDivider()
            options.forEachIndexed { index, option ->
                if (index > 0) SDivider(indent = 16)
                Row(Modifier.fillMaxWidth().clickable { onAction(option.title) }.padding(horizontal = 16.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small, modifier = Modifier.size(36.dp)) {
                        androidx.compose.foundation.layout.Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Icon(option.icon, null, tint = TextSecondaryLight, modifier = Modifier.size(18.dp)) }
                    }
                    Spacer(Modifier.size(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(option.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        Text(option.subtitle, style = MaterialTheme.typography.bodySmall, color = TextTertiaryLight)
                    }
                    Icon(Icons.Filled.KeyboardArrowRight, null, tint = TextTertiaryLight, modifier = Modifier.size(18.dp))
                }
            }
            SDivider()
            SecondaryActionButton("Batal", onClick = onDismiss, modifier = Modifier.fillMaxWidth().padding(16.dp))
        }
    }
}
