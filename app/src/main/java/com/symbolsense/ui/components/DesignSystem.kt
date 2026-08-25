package com.symbolsense.ui.components

import android.app.Activity
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.theme.AmberWarning
import com.symbolsense.ui.theme.BorderLight
import com.symbolsense.ui.theme.IndigoPrimary
import com.symbolsense.ui.theme.SurfaceRaisedLight
import com.symbolsense.ui.theme.SymbolMono
import com.symbolsense.ui.theme.TextSecondaryLight
import com.symbolsense.ui.theme.TextTertiaryLight

val PagePadding = 16.dp
val StandardRadius = 10.dp

@Composable
fun SDivider(indent: Int = 0) {
    HorizontalDivider(
        modifier = Modifier.padding(start = indent.dp),
        thickness = 1.dp,
        color = BorderLight
    )
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = TextTertiaryLight,
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp)
    )
}

@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leading: (@Composable () -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(StandardRadius),
        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge, color = Color.White)
    }
}

@Composable
fun SecondaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(StandardRadius),
        border = BorderStroke(1.dp, BorderLight),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondaryLight)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun GlyphTile(glyph: String, modifier: Modifier = Modifier, size: Int = 40) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceRaisedLight)
            .border(1.dp, BorderLight, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = glyph,
            fontFamily = SymbolMono,
            fontWeight = FontWeight.Bold,
            fontSize = if (glyph.length > 2) 14.sp else 20.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ConfidenceText(value: Float) {
    val percent = (value * 100).toInt()
    Text(
        text = "$percent%",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = if (percent < 70) AmberWarning else TextSecondaryLight
    )
}

fun domainCodeLabel(domain: SymbolDomain): String = when (domain) {
    SymbolDomain.MATH -> "LaTeX"
    SymbolDomain.CHEMISTRY -> "SMILES"
    SymbolDomain.ELECTRONICS -> "Netlist"
    SymbolDomain.GENERAL, SymbolDomain.AUTO -> "Teks"
}

fun domainGlyph(domain: SymbolDomain): String = when (domain) {
    SymbolDomain.AUTO -> "◎"
    SymbolDomain.MATH -> "∫"
    SymbolDomain.CHEMISTRY -> "⬡"
    SymbolDomain.ELECTRONICS -> "R"
    SymbolDomain.GENERAL -> "◇"
}

@Composable
fun SystemBarsForScreen(darkIcons: Boolean) {
    val view = LocalView.current
    DisposableEffect(darkIcons) {
        val activity = view.context as? Activity
        val controller = activity?.window?.let { WindowCompat.getInsetsController(it, view) }
        controller?.isAppearanceLightStatusBars = darkIcons
        controller?.isAppearanceLightNavigationBars = darkIcons
        onDispose {
            controller?.isAppearanceLightStatusBars = true
            controller?.isAppearanceLightNavigationBars = true
        }
    }
}
