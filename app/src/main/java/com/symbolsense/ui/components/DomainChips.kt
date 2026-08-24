package com.symbolsense.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.theme.DomainColors

/**
 * Chip pemilih domain (dipakai di Home - Screen 3, Domain Confirmation - Screen 6,
 * dan filter Riwayat/Pustaka - Screen 11 & 13).
 */
@Composable
fun DomainChip(
    label: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    val containerColor = if (selected) accentColor else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) Color.White else accentColor
    val border = if (selected) null else BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = border,
        onClick = onClick
    ) {
        Text(
            text = "$icon  $label",
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

/** Badge kecil berwarna sesuai domain, dipakai di History list & Result Editor. */
@Composable
fun DomainBadge(domain: SymbolDomain) {
    val (bg, fg) = when (domain) {
        SymbolDomain.MATH -> DomainColors.MathBg to DomainColors.MathText
        SymbolDomain.CHEMISTRY -> DomainColors.ChemBg to DomainColors.ChemText
        SymbolDomain.ELECTRONICS -> DomainColors.ElectroBg to DomainColors.ElectroText
        else -> DomainColors.GeneralBg to DomainColors.GeneralText
    }
    Surface(shape = RoundedCornerShape(8.dp), color = bg) {
        Text(
            text = domain.label,
            style = MaterialTheme.typography.labelMedium,
            color = fg,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/** Mengembalikan glyph ikon sederhana untuk tiap domain (dipakai pada chip & badge thumbnail). */
fun domainIcon(domain: SymbolDomain): String = when (domain) {
    SymbolDomain.AUTO -> "⚡"
    SymbolDomain.MATH -> "∫"
    SymbolDomain.CHEMISTRY -> "⌬"
    SymbolDomain.ELECTRONICS -> "⊗"
    SymbolDomain.GENERAL -> "◎"
}
