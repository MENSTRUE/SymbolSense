package com.symbolsense.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.symbolsense.data.model.SampleData
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.components.BottomNavTab
import com.symbolsense.ui.screens.camera.CameraScreen
import com.symbolsense.ui.screens.detection.DetectionResultScreen
import com.symbolsense.ui.screens.domain.DomainConfirmationSheet
import com.symbolsense.ui.screens.editor.ResultEditorScreen
import com.symbolsense.ui.screens.export.ExportBottomSheet
import com.symbolsense.ui.screens.history.HistoryScreen
import com.symbolsense.ui.screens.historydetail.HistoryDetailScreen
import com.symbolsense.ui.screens.home.HomeScreen
import com.symbolsense.ui.screens.library.SymbolLibraryScreen
import com.symbolsense.ui.screens.onboarding.OnboardingScreen
import com.symbolsense.ui.screens.preview.ImagePreviewScreen
import com.symbolsense.ui.screens.processing.ProcessingScreen
import com.symbolsense.ui.screens.settings.SettingsScreen
import com.symbolsense.ui.screens.splash.SplashScreen
import com.symbolsense.ui.screens.symboldetail.SymbolDetailScreen

/**
 * NavGraph utama.
 *
 * PENTING - Bottom nav logic:
 * Home, History, Library, Settings adalah "main tabs" — mereka
 * SEMUA punya bottom nav. Kita simpan selectedTab di level NavGraph
 * dan pass ke tiap main screen via parameter, sehingga tab switch
 * cukup satu kali navigate tanpa popBackStack berantai.
 *
 * Camera, Preview, Processing, Detection, Editor adalah "flow screens" —
 * tidak punya bottom nav, punya top bar back button.
 */
@Composable
fun SymbolSenseNavGraph(navController: NavHostController = rememberNavController()) {

    // Tab state dibawa di level NavGraph agar konsisten antar main screens
    var selectedTab by remember { mutableStateOf(BottomNavTab.HOME) }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        // ── SPLASH ────────────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(onTimeout = {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        // ── ONBOARDING ────────────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinish = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }

        // ── HOME ──────────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            selectedTab = BottomNavTab.HOME
            HomeScreen(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        BottomNavTab.HOME -> {}
                        BottomNavTab.HISTORY -> navController.navigate(Screen.History.route) {
                            launchSingleTop = true
                        }
                        BottomNavTab.LIBRARY -> navController.navigate(Screen.Library.route) {
                            launchSingleTop = true
                        }
                        BottomNavTab.SETTINGS -> navController.navigate(Screen.Settings.route) {
                            launchSingleTop = true
                        }
                    }
                },
                onOpenCamera = { navController.navigate(Screen.Camera.route) },
                onOpenHistoryDetail = { id -> navController.navigate(Screen.HistoryDetail.build(id)) }
            )
        }

        // ── CAMERA ────────────────────────────────────────────────────────────
        composable(Screen.Camera.route) {
            CameraScreen(
                onClose = { navController.popBackStack() },
                onCapture = { navController.navigate(Screen.Preview.route) },
                onOpenGallery = { navController.navigate(Screen.Preview.route) }
            )
        }

        // ── PREVIEW + DOMAIN CONFIRM ──────────────────────────────────────────
        composable(Screen.Preview.route) {
            var showDomainConfirm by remember { mutableStateOf(false) }

            ImagePreviewScreen(
                onBack = { navController.popBackStack() },
                onConfirm = { showDomainConfirm = true }
            )

            if (showDomainConfirm) {
                DomainConfirmationSheet(
                    detectedDomain = SymbolDomain.MATH,
                    confidence = 0.94f,
                    onDismiss = { showDomainConfirm = false },
                    onProcess = {
                        showDomainConfirm = false
                        navController.navigate(Screen.Processing.route)
                    }
                )
            }
        }

        // ── PROCESSING ────────────────────────────────────────────────────────
        composable(Screen.Processing.route) {
            ProcessingScreen(onDone = {
                navController.navigate(Screen.Detection.route) {
                    popUpTo(Screen.Processing.route) { inclusive = true }
                }
            })
        }

        // ── DETECTION ─────────────────────────────────────────────────────────
        composable(Screen.Detection.route) {
            DetectionResultScreen(
                symbols = SampleData.detectedSymbols,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.Editor.build(SampleData.mathResult.id)) },
                onViewStructuredResult = { navController.navigate(Screen.Editor.build(SampleData.mathResult.id)) }
            )
        }

        // ── EDITOR + EXPORT ───────────────────────────────────────────────────
        composable(Screen.Editor.route) { backStackEntry ->
            val scanId = backStackEntry.arguments?.getString("scanId")
            val result = SampleData.historyList.find { it.id == scanId } ?: SampleData.mathResult
            var showExport by remember { mutableStateOf(false) }

            ResultEditorScreen(
                result = result,
                onBack = { navController.popBackStack() },
                onShare = { showExport = true },
                onEditManual = { /* TODO */ },
                onOpenHistory = {
                    // Navigasi ke History tab tanpa destroy back stack scan flow
                    selectedTab = BottomNavTab.HISTORY
                    navController.navigate(Screen.History.route) { launchSingleTop = true }
                },
                onExport = { showExport = true },
                onScanLagi = {
                    // Kembali ke Home lalu langsung buka kamera
                    navController.navigate(Screen.Camera.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )

            if (showExport) {
                val codeLabel = when (result.domain) {
                    SymbolDomain.MATH -> "LaTeX"
                    SymbolDomain.CHEMISTRY -> "SMILES"
                    SymbolDomain.ELECTRONICS -> "Netlist"
                    else -> "Kode"
                }
                ExportBottomSheet(
                    codeLabel = codeLabel,
                    onDismiss = { showExport = false },
                    onAction = { showExport = false }
                )
            }
        }

        // ── HISTORY ───────────────────────────────────────────────────────────
        composable(Screen.History.route) {
            selectedTab = BottomNavTab.HISTORY
            HistoryScreen(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        BottomNavTab.HOME -> navController.navigate(Screen.Home.route) { launchSingleTop = true }
                        BottomNavTab.HISTORY -> {}
                        BottomNavTab.LIBRARY -> navController.navigate(Screen.Library.route) { launchSingleTop = true }
                        BottomNavTab.SETTINGS -> navController.navigate(Screen.Settings.route) { launchSingleTop = true }
                    }
                },
                onOpenCamera = { navController.navigate(Screen.Camera.route) },
                onOpenDetail = { id -> navController.navigate(Screen.HistoryDetail.build(id)) },
                onStartScan = { navController.navigate(Screen.Camera.route) }
            )
        }

        // ── HISTORY DETAIL ────────────────────────────────────────────────────
        composable(Screen.HistoryDetail.route) { backStackEntry ->
            val scanId = backStackEntry.arguments?.getString("scanId")
            val result = SampleData.historyList.find { it.id == scanId } ?: SampleData.historyList.first()

            HistoryDetailScreen(
                result = result,
                onBack = { navController.popBackStack() },
                onDelete = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.Editor.build(result.id)) },
                onExport = { navController.navigate(Screen.Editor.build(result.id)) }
            )
        }

        // ── LIBRARY ───────────────────────────────────────────────────────────
        composable(Screen.Library.route) {
            selectedTab = BottomNavTab.LIBRARY
            SymbolLibraryScreen(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        BottomNavTab.HOME -> navController.navigate(Screen.Home.route) { launchSingleTop = true }
                        BottomNavTab.HISTORY -> navController.navigate(Screen.History.route) { launchSingleTop = true }
                        BottomNavTab.LIBRARY -> {}
                        BottomNavTab.SETTINGS -> navController.navigate(Screen.Settings.route) { launchSingleTop = true }
                    }
                },
                onOpenCamera = { navController.navigate(Screen.Camera.route) },
                onOpenSymbol = { id -> navController.navigate(Screen.SymbolDetail.build(id)) }
            )
        }

        // ── SYMBOL DETAIL ─────────────────────────────────────────────────────
        composable(Screen.SymbolDetail.route) { backStackEntry ->
            val symbolId = backStackEntry.arguments?.getString("symbolId")
            val entry = SampleData.symbolLibrary.find { it.id == symbolId } ?: SampleData.symbolLibrary.first()

            SymbolDetailScreen(
                entry = entry,
                onBack = { navController.popBackStack() },
                onSpeak = { }
            )
        }

        // ── SETTINGS ──────────────────────────────────────────────────────────
        composable(Screen.Settings.route) {
            selectedTab = BottomNavTab.SETTINGS
            SettingsScreen(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        BottomNavTab.HOME -> navController.navigate(Screen.Home.route) { launchSingleTop = true }
                        BottomNavTab.HISTORY -> navController.navigate(Screen.History.route) { launchSingleTop = true }
                        BottomNavTab.LIBRARY -> navController.navigate(Screen.Library.route) { launchSingleTop = true }
                        BottomNavTab.SETTINGS -> {}
                    }
                },
                onOpenCamera = { navController.navigate(Screen.Camera.route) },
                onAbout = { }
            )
        }
    }
}