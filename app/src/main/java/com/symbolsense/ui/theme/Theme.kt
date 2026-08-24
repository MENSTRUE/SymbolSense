package com.symbolsense.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    secondary = CyanAccent,
    onSecondary = Color.White,
    tertiary = AmberWarning,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = DividerLight,
    onSurfaceVariant = TextSecondaryLight,
    error = RedDanger
)

private val DarkColors = darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = Color.White,
    secondary = CyanAccent,
    onSecondary = Color.Black,
    tertiary = AmberWarning,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = DividerDark,
    onSurfaceVariant = TextSecondaryDark,
    error = RedDanger
)

val SymbolSenseShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun SymbolSenseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        val context = view.context
        if (context is Activity) {
            val window = context.window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SymbolSenseTypography,
        shapes = SymbolSenseShapes,
        content = content
    )
}
