package com.symbolsense.ui.theme

import androidx.compose.ui.graphics.Color

// Brand
val IndigoPrimary = Color(0xFF4F46E5)
val IndigoPrimaryDark = Color(0xFF6366F1)
val CyanAccent = Color(0xFF06B6D4)
val AmberWarning = Color(0xFFF59E0B)
val GreenSuccess = Color(0xFF22C55E)
val RedDanger = Color(0xFFEF4444)

// Light theme surfaces
val BackgroundLight = Color(0xFFF8F9FC)
val SurfaceLight = Color(0xFFFFFFFF)
val TextPrimaryLight = Color(0xFF1A1B25)
val TextSecondaryLight = Color(0xFF6B7280)
val DividerLight = Color(0xFFE5E7EB)

// Dark theme surfaces
val BackgroundDark = Color(0xFF0F1115)
val SurfaceDark = Color(0xFF1A1D24)
val TextPrimaryDark = Color(0xFFF4F5F7)
val TextSecondaryDark = Color(0xFF9CA3AF)
val DividerDark = Color(0xFF2A2D36)

// Domain accent colors (badge background + text)
object DomainColors {
    val MathBg = Color(0xFFE0E7FF)
    val MathText = Color(0xFF4F46E5)

    val ChemBg = Color(0xFFD1FAE5)
    val ChemText = Color(0xFF059669)

    val ElectroBg = Color(0xFFFFEDD5)
    val ElectroText = Color(0xFFEA580C)

    val GeneralBg = Color(0xFFE5E7EB)
    val GeneralText = Color(0xFF4B5563)
}

// Confidence badge colors
val ConfidenceHigh = GreenSuccess
val ConfidenceMedium = CyanAccent
val ConfidenceLow = AmberWarning
