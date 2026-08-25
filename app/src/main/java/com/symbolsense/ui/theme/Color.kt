package com.symbolsense.ui.theme

import androidx.compose.ui.graphics.Color

// SymbolSense V3 — tokens dari desain Figma final.
val IndigoPrimary = Color(0xFF4F46E5)
val IndigoPrimaryContainer = Color(0xFFEEF2FF)
val CyanAccent = Color(0xFF06B6D4)
val CyanContainer = Color(0xFFECFEFF)
val AmberWarning = Color(0xFFB45309)
val AmberContainer = Color(0xFFFEF3C7)
val GreenSuccess = Color(0xFF15803D)
val GreenContainer = Color(0xFFF0FDF4)
val RedDanger = Color(0xFFDC2626)

val BackgroundLight = Color(0xFFFAFAF9)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceRaisedLight = Color(0xFFF5F5F4)
val BorderLight = Color(0xFFE7E5E4)
val BorderStrongLight = Color(0xFFD6D3D1)
val TextPrimaryLight = Color(0xFF1C1917)
val TextSecondaryLight = Color(0xFF57534E)
val TextTertiaryLight = Color(0xFFA8A29E)

val CameraBlack = Color(0xFF111010)
val CodeBlack = Color(0xFF1C1917)

// Alias lama agar komponen lama yang belum dipakai tetap aman saat dikompilasi.
val BackgroundDark = Color(0xFF111010)
val SurfaceDark = Color(0xFF1C1917)
val TextPrimaryDark = Color(0xFFFAFAF9)
val TextSecondaryDark = Color(0xFFD6D3D1)
val DividerLight = BorderLight
val DividerDark = Color(0xFF292524)
val IndigoPrimaryDark = Color(0xFF818CF8)

object DomainColors {
    val MathBg = IndigoPrimaryContainer
    val MathText = IndigoPrimary
    val ChemBg = GreenContainer
    val ChemText = GreenSuccess
    val ElectroBg = AmberContainer
    val ElectroText = AmberWarning
    val GeneralBg = SurfaceRaisedLight
    val GeneralText = TextSecondaryLight
}

val ConfidenceHigh = CyanAccent
val ConfidenceMedium = CyanAccent
val ConfidenceLow = AmberWarning
