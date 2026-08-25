package com.symbolsense.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.symbolsense.ui.viewmodel.ScanHistoryViewModel

@Composable
fun SymbolSenseNavGraph(
    navController: NavHostController = rememberNavController(),
    historyViewModel: ScanHistoryViewModel = viewModel()
) {
    val history by historyViewModel.history.collectAsState()

    fun openTab(tab: BottomNavTab) {
        val route = when (tab) {
            BottomNavTab.HOME -> Screen.Home.route
            BottomNavTab.SCAN -> Screen.Camera.route
            BottomNavTab.LIBRARY -> Screen.Library.route
            BottomNavTab.HISTORY -> Screen.History.route
        }

        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(Screen.Home.route) {
                saveState = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }
        }

        composable(Screen.Home.route) {
            HomeScreen(
                selectedTab = BottomNavTab.HOME,
                recentHistory = history.take(3),
                onTabSelected = ::openTab,
                onOpenCamera = { navController.navigate(Screen.Camera.route) },
                onOpenHistory = { navController.navigate(Screen.History.route) },
                onOpenHistoryDetail = { scanId ->
                    navController.navigate(Screen.HistoryDetail.build(scanId))
                },
                onOpenLibrary = { navController.navigate(Screen.Library.route) },
                onOpenSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onClose = { navController.popBackStack() },
                onCapture = { navController.navigate(Screen.Preview.route) },
                onOpenGallery = { navController.navigate(Screen.Preview.route) }
            )
        }

        composable(Screen.Preview.route) {
            var showDomain by remember { mutableStateOf(false) }

            ImagePreviewScreen(
                onBack = { navController.popBackStack() },
                onConfirm = { showDomain = true }
            )

            if (showDomain) {
                DomainConfirmationSheet(
                    detectedDomain = SymbolDomain.MATH,
                    confidence = 0.94f,
                    onDismiss = { showDomain = false },
                    onProcess = {
                        showDomain = false
                        navController.navigate(Screen.Processing.route)
                    }
                )
            }
        }

        composable(Screen.Processing.route) {
            ProcessingScreen {
                navController.navigate(Screen.Detection.route) {
                    popUpTo(Screen.Processing.route) { inclusive = true }
                }
            }
        }

        composable(Screen.Detection.route) {
            DetectionResultScreen(
                symbols = SampleData.detectedSymbols,
                onBack = { navController.popBackStack() },
                onEdit = {
                    navController.navigate(Screen.Editor.build(SampleData.mathResult.id))
                },
                onViewStructuredResult = {
                    navController.navigate(Screen.Editor.build(SampleData.mathResult.id))
                }
            )
        }

        composable(Screen.Editor.route) { entry ->
            val scanId = entry.arguments?.getString("scanId")
            val result = when {
                scanId == SampleData.mathResult.id -> SampleData.mathResult
                scanId != null -> history.find { it.id == scanId }
                else -> null
            }

            if (result == null) {
                LoadingHistoryItem()
            } else {
                var showExport by remember { mutableStateOf(false) }

                ResultEditorScreen(
                    result = result,
                    onBack = { navController.popBackStack() },
                    onShare = { showExport = true },
                    onEditManual = { },
                    onOpenHistory = {
                        historyViewModel.saveScan(result) {
                            navController.navigate(Screen.History.route) {
                                launchSingleTop = true
                            }
                        }
                    },
                    onExport = { showExport = true },
                    onScanLagi = { navController.navigate(Screen.Camera.route) }
                )

                if (showExport) {
                    val label = when (result.domain) {
                        SymbolDomain.MATH -> "LaTeX"
                        SymbolDomain.CHEMISTRY -> "SMILES"
                        SymbolDomain.ELECTRONICS -> "Netlist"
                        else -> "Teks"
                    }

                    ExportBottomSheet(
                        codeLabel = label,
                        onDismiss = { showExport = false },
                        onAction = { showExport = false }
                    )
                }
            }
        }

        composable(Screen.History.route) {
            HistoryScreen(
                selectedTab = BottomNavTab.HISTORY,
                historyItems = history,
                onTabSelected = ::openTab,
                onOpenDetail = { scanId ->
                    navController.navigate(Screen.HistoryDetail.build(scanId))
                }
            )
        }

        composable(Screen.HistoryDetail.route) { entry ->
            val scanId = entry.arguments?.getString("scanId")
            val result = scanId?.let { id -> history.find { it.id == id } }

            if (result == null) {
                LoadingHistoryItem()
            } else {
                HistoryDetailScreen(
                    result = result,
                    onBack = { navController.popBackStack() },
                    onDelete = {
                        historyViewModel.deleteScan(result.id) {
                            navController.popBackStack()
                        }
                    },
                    onEdit = {
                        navController.navigate(Screen.Editor.build(result.id))
                    },
                    onExport = {
                        navController.navigate(Screen.Editor.build(result.id))
                    }
                )
            }
        }

        composable(Screen.Library.route) {
            SymbolLibraryScreen(
                selectedTab = BottomNavTab.LIBRARY,
                onTabSelected = ::openTab,
                onOpenSymbol = { symbolId ->
                    navController.navigate(Screen.SymbolDetail.build(symbolId))
                }
            )
        }

        composable(Screen.SymbolDetail.route) { entry ->
            val id = entry.arguments?.getString("symbolId")
            val symbol = SampleData.symbolLibrary.find { it.id == id }
                ?: SampleData.symbolLibrary.first()

            SymbolDetailScreen(
                entry = symbol,
                onBack = { navController.popBackStack() },
                onSpeak = { }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenHistory = { navController.navigate(Screen.History.route) },
                onClearHistory = { historyViewModel.clearHistory() },
                onAbout = { }
            )
        }
    }
}

@Composable
private fun LoadingHistoryItem() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Text(
                text = "Memuat data...",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
