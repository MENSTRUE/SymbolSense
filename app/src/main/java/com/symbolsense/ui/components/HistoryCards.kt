package com.symbolsense.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.ScanResult
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.theme.DomainColors

/**
 * Card riwayat horizontal kecil untuk section "Riwayat Terbaru" di Home (Screen 3).
 */
@Composable
fun HistoryCardCompact(item: ScanResult, onClick: () -> Unit) {
    val thumbBg = when (item.domain) {
        SymbolDomain.MATH -> DomainColors.MathBg
        SymbolDomain.CHEMISTRY -> DomainColors.ChemBg
        SymbolDomain.ELECTRONICS -> DomainColors.ElectroBg
        else -> DomainColors.GeneralBg
    }
    val thumbFg = when (item.domain) {
        SymbolDomain.MATH -> DomainColors.MathText
        SymbolDomain.CHEMISTRY -> DomainColors.ChemText
        SymbolDomain.ELECTRONICS -> DomainColors.ElectroText
        else -> DomainColors.GeneralText
    }

    Card(
        modifier = Modifier.width(170.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(thumbBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(domainIcon(item.domain), color = thumbFg, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.size(8.dp))
            DomainBadge(item.domain)
            Spacer(Modifier.size(6.dp))
            Text(
                text = item.rawPreviewText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.timestampLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Card riwayat full-width untuk screen Riwayat (Screen 11).
 */
@Composable
fun HistoryCardFull(item: ScanResult, onClick: () -> Unit, onMenuClick: () -> Unit) {
    val thumbBg = when (item.domain) {
        SymbolDomain.MATH -> DomainColors.MathBg
        SymbolDomain.CHEMISTRY -> DomainColors.ChemBg
        SymbolDomain.ELECTRONICS -> DomainColors.ElectroBg
        else -> DomainColors.GeneralBg
    }
    val thumbFg = when (item.domain) {
        SymbolDomain.MATH -> DomainColors.MathText
        SymbolDomain.CHEMISTRY -> DomainColors.ChemText
        SymbolDomain.ELECTRONICS -> DomainColors.ElectroText
        else -> DomainColors.GeneralText
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(thumbBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(domainIcon(item.domain), color = thumbFg, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DomainBadge(item.domain)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = item.timestampLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.size(4.dp))
                Text(
                    text = item.rawPreviewText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Opsi")
            }
        }
    }
}

/**
 * Mini-card simbol terdeteksi (Screen 8 - Detection, Screen 13 - Library).
 */
@Composable
fun SymbolMiniCard(
    glyph: String,
    label: String,
    confidence: String? = null,
    confidenceColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.secondary,
    modifier: Modifier = Modifier.width(86.dp),
    onClick: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier,
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(glyph, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.size(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (confidence != null) {
                Text(confidence, style = MaterialTheme.typography.labelSmall, color = confidenceColor)
            }
        }
    }
}
