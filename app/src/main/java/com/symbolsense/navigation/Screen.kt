package com.symbolsense.navigation

/** Semua route layar dalam aplikasi SymbolSense. */
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object Camera : Screen("camera")
    data object Preview : Screen("preview")
    data object Processing : Screen("processing")
    data object Detection : Screen("detection")
    data object Editor : Screen("editor/{scanId}") {
        fun build(scanId: String) = "editor/$scanId"
    }
    data object History : Screen("history")
    data object HistoryDetail : Screen("history_detail/{scanId}") {
        fun build(scanId: String) = "history_detail/$scanId"
    }
    data object Library : Screen("library")
    data object SymbolDetail : Screen("symbol_detail/{symbolId}") {
        fun build(symbolId: String) = "symbol_detail/$symbolId"
    }
    data object Settings : Screen("settings")
}
