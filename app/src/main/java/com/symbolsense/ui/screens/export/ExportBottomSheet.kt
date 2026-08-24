package com.symbolsense.ui.screens.export

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.ui.theme.IndigoPrimary
import com.symbolsense.ui.theme.RedDanger

/** Aksi export yang tersedia di Screen 10. */
enum class ExportAction { COPY_TEXT, COPY_CODE, SAVE_PDF, SAVE_DOCX, SHARE }

/**
 * Screen 10/15 — Export Options Bottom Sheet.
 *
 * "Salin LaTeX/SMILES" label disesuaikan via [codeLabel].
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ExportBottomSheet(
    codeLabel: String = "LaTeX",
    onDismiss: () -> Unit,
    onAction: (ExportAction) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Export Hasil", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.size(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ExportOptionCard(
                    icon = Icons.Filled.ContentCopy,
                    label = "Salin Teks",
                    tint = IndigoPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(ExportAction.COPY_TEXT) }
                )
                ExportOptionCard(
                    icon = Icons.Filled.Code,
                    label = "Salin $codeLabel",
                    tint = IndigoPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(ExportAction.COPY_CODE) }
                )
            }

            Spacer(Modifier.size(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ExportOptionCard(
                    icon = Icons.Filled.PictureAsPdf,
                    label = "Simpan PDF",
                    tint = RedDanger,
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(ExportAction.SAVE_PDF) }
                )
                ExportOptionCard(
                    icon = Icons.AutoMirrored.Filled.InsertDriveFile,
                    label = "Simpan DOCX",
                    tint = IndigoPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(ExportAction.SAVE_DOCX) }
                )
            }

            Spacer(Modifier.size(16.dp))

            OutlinedButton(
                onClick = { onAction(ExportAction.SHARE) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(8.dp))
                Text("Bagikan via...", color = IndigoPrimary)
            }

            Spacer(Modifier.size(8.dp))
        }
    }
}

@Composable
private fun ExportOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = RoundedCornerShape(50), color = tint.copy(alpha = 0.12f)) {
                Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.padding(10.dp).size(22.dp))
            }
            Spacer(Modifier.size(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
