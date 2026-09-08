package com.symbolsense.navigation

import android.net.Uri
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.symbolsense.ai.SymbolRecognitionResult
import com.symbolsense.data.model.DetectedSymbol
import com.symbolsense.data.model.RelativeBoundingBox
import com.symbolsense.data.model.SampleData
import com.symbolsense.data.model.ScanResult
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

    val history by
    historyViewModel.history.collectAsState()

    /*
     * =========================================================
     * CURRENT SCAN STATE
     * =========================================================
     */

    var currentImageUri by
    rememberSaveable {

        mutableStateOf<String?>(
            null
        )
    }

    var currentAiResult by
    remember {

        mutableStateOf<SymbolRecognitionResult?>(
            null
        )
    }

    var currentLiveResult by
    remember {

        mutableStateOf<ScanResult?>(
            null
        )
    }

    /*
     * =========================================================
     * ONBOARDING
     * =========================================================
     */

    val context =
        LocalContext.current

    val onboardingPrefs =
        remember {

            context.getSharedPreferences(
                "symbolsense_onboarding",
                android.content.Context.MODE_PRIVATE
            )
        }

    var onboardingCompleted by
    remember {

        mutableStateOf(
            onboardingPrefs.getBoolean(
                "completed",
                false
            )
        )
    }

    /*
     * =========================================================
     * ROOT NAVIGATION
     * =========================================================
     *
     * Tidak pakai saveState/restoreState dahulu.
     *
     * Ini supaya route lama:
     *
     * Camera
     * Preview
     * Detection
     * Editor
     *
     * tidak direstore ketika pindah tab.
     */

    fun openTab(
        tab: BottomNavTab
    ) {

        val route =
            when (tab) {

                BottomNavTab.HOME ->
                    Screen.Home.route

                BottomNavTab.SCAN ->
                    Screen.Camera.route

                BottomNavTab.LIBRARY ->
                    Screen.Library.route

                BottomNavTab.HISTORY ->
                    Screen.History.route
            }

        navController.navigate(
            route
        ) {

            launchSingleTop = true

            popUpTo(
                Screen.Home.route
            ) {

                inclusive = false
            }
        }
    }

    /*
     * =========================================================
     * NAV HOST
     * =========================================================
     */

    NavHost(
        navController =
            navController,
        startDestination =
            Screen.Splash.route
    ) {

        /*
         * =====================================================
         * SPLASH
         * =====================================================
         */

        composable(
            Screen.Splash.route
        ) {

            SplashScreen(
                onTimeout = {

                    val destination =
                        if (
                            onboardingCompleted
                        ) {

                            Screen.Home.route

                        } else {

                            Screen.Onboarding.route
                        }

                    navController.navigate(
                        destination
                    ) {

                        popUpTo(
                            Screen.Splash.route
                        ) {

                            inclusive = true
                        }
                    }
                }
            )
        }

        /*
         * =====================================================
         * ONBOARDING
         * =====================================================
         */

        composable(
            Screen.Onboarding.route
        ) {

            OnboardingScreen {

                onboardingPrefs
                    .edit()
                    .putBoolean(
                        "completed",
                        true
                    )
                    .apply()

                onboardingCompleted =
                    true

                navController.navigate(
                    Screen.Home.route
                ) {

                    popUpTo(
                        Screen.Onboarding.route
                    ) {

                        inclusive = true
                    }
                }
            }
        }

        /*
         * =====================================================
         * HOME
         * =====================================================
         */

        composable(
            Screen.Home.route
        ) {

            HomeScreen(
                selectedTab =
                    BottomNavTab.HOME,

                recentHistory =
                    history.take(3),

                onTabSelected =
                    ::openTab,

                onOpenCamera = {

                    openTab(
                        BottomNavTab.SCAN
                    )
                },

                onOpenHistory = {

                    openTab(
                        BottomNavTab.HISTORY
                    )
                },

                onOpenHistoryDetail = { scanId ->

                    navController.navigate(
                        Screen.HistoryDetail.build(
                            scanId
                        )
                    )
                },

                onOpenLibrary = {

                    openTab(
                        BottomNavTab.LIBRARY
                    )
                },

                onOpenSettings = {

                    navController.navigate(
                        Screen.Settings.route
                    )
                }
            )
        }

        /*
         * =====================================================
         * CAMERA
         * =====================================================
         */

        composable(
            Screen.Camera.route
        ) {

            CameraScreen(

                onClose = {

                    navController.popBackStack()
                },

                onImageReady = { uri ->

                    /*
                     * Scan baru.
                     */
                    currentAiResult =
                        null

                    currentLiveResult =
                        null

                    currentImageUri =
                        uri.toString()

                    navController.navigate(
                        Screen.Preview.route
                    )
                }
            )
        }

        /*
         * =====================================================
         * PREVIEW / CROP
         * =====================================================
         */

        composable(
            Screen.Preview.route
        ) {

            var showDomain by
            remember {

                mutableStateOf(
                    false
                )
            }

            ImagePreviewScreen(

                imageUri =
                    currentImageUri
                        ?.let(
                            Uri::parse
                        ),

                onBack = {

                    navController.popBackStack()
                },

                onConfirm = { croppedUri ->

                    /*
                     * INI gambar yang dikirim
                     * ke classifier.
                     */
                    currentImageUri =
                        croppedUri.toString()

                    currentAiResult =
                        null

                    currentLiveResult =
                        null

                    showDomain =
                        true
                }
            )

            if (showDomain) {

                DomainConfirmationSheet(

                    detectedDomain =
                        SymbolDomain.MATH,

                    /*
                     * Domain detection sendiri
                     * belum model AI.
                     *
                     * Untuk tahap sekarang kita memang
                     * hanya Mathematics.
                     */
                    confidence =
                        0.94f,

                    onDismiss = {

                        showDomain =
                            false
                    },

                    onProcess = {

                        showDomain =
                            false

                        navController.navigate(
                            Screen.Processing.route
                        )
                    }
                )
            }
        }

        /*
         * =====================================================
         * PROCESSING
         * =====================================================
         */

        composable(
            Screen.Processing.route
        ) {

            val imageUri =
                currentImageUri
                    ?.let(
                        Uri::parse
                    )

            ProcessingScreen(

                imageUri =
                    imageUri,

                onDone = { result ->

                    /*
                     * =========================================
                     * REAL TFLITE RESULT
                     * =========================================
                     */

                    currentAiResult =
                        result

                    val now =
                        System.currentTimeMillis()

                    /*
                     * =========================================
                     * BUILD REAL DETECTED SYMBOL LIST
                     * =========================================
                     *
                     * Formula pipeline:
                     * detector bbox -> classifier -> parser.
                     *
                     * If result came from an old isolated call,
                     * keep the previous full-crop fallback.
                     */

                    val detectedSymbols =
                        if (result.symbols.isNotEmpty()) {

                            result.symbols.mapIndexed { index, symbol ->

                                DetectedSymbol(
                                    id = "ai_${index}_${symbol.prediction.id}_$now",
                                    label = symbol.prediction.name,
                                    displayGlyph = symbol.prediction.display,
                                    confidence = symbol.prediction.confidence,
                                    boundingBox = RelativeBoundingBox(
                                        left = symbol.boundingBox.left,
                                        top = symbol.boundingBox.top,
                                        right = symbol.boundingBox.right,
                                        bottom = symbol.boundingBox.bottom
                                    )
                                )
                            }

                        } else {

                            listOf(
                                DetectedSymbol(
                                    id = "ai_${result.best.id}_$now",
                                    label = result.best.name,
                                    displayGlyph = result.best.display,
                                    confidence = result.best.confidence,
                                    boundingBox = RelativeBoundingBox(
                                        0f,
                                        0f,
                                        1f,
                                        1f
                                    )
                                )
                            )
                        }

                    /*
                     * =========================================
                     * BUILD REAL SCAN RESULT
                     * =========================================
                     */

                    currentLiveResult =
                        SampleData
                            .mathResult
                            .copy(

                                id =
                                    "live_math_$now",

                                timestampLabel =
                                    "Baru saja",

                                rawPreviewText =
                                    result.structuredDisplay,

                                structuredOutput =
                                    result.structuredDisplay,

                                latexOrCode =
                                    result.structuredLatex,

                                detectedSymbols =
                                    detectedSymbols,

                                imageUri =
                                    currentImageUri
                            )

                    /*
                     * Debug log.
                     */

                    println(
                        "========================================"
                    )

                    println(
                        "SYMBOLSENSE FORMULA AI"
                    )

                    println(
                        "IMAGE      : $currentImageUri"
                    )

                    println(
                        "MODE       : ${result.mode}"
                    )

                    println(
                        "SYMBOLS    : ${result.symbols.size}"
                    )

                    println(
                        "DISPLAY    : ${result.structuredDisplay}"
                    )

                    println(
                        "LATEX      : ${result.structuredLatex}"
                    )

                    println(
                        "RELIABLE   : ${result.reliable}"
                    )

                    println(
                        "DETECTOR   : ${result.detectorInferenceTimeMs} ms"
                    )

                    println(
                        "TOTAL      : ${result.inferenceTimeMs} ms"
                    )

                    println(
                        "========================================"
                    )

                    navController.navigate(
                        Screen.Detection.route
                    ) {

                        popUpTo(
                            Screen.Processing.route
                        ) {

                            inclusive = true
                        }
                    }
                },

                onError = { throwable ->

                    println(
                        "========================================"
                    )

                    println(
                        "SYMBOLSENSE AI ERROR"
                    )

                    println(
                        throwable.message
                    )

                    throwable.printStackTrace()

                    println(
                        "========================================"
                    )
                }
            )
        }

        /*
         * =====================================================
         * DETECTION RESULT
         * =====================================================
         */

        composable(
            Screen.Detection.route
        ) {

            val aiResult =
                currentAiResult

            if (
                aiResult == null
            ) {

                LoadingHistoryItem()

            } else {

                DetectionResultScreen(

                    result =
                        aiResult,

                    imageUri =
                        currentImageUri
                            ?.let(
                                Uri::parse
                            ),

                    onBack = {

                        navController.popBackStack()
                    },

                    onViewStructuredResult = {

                        val liveResult =
                            currentLiveResult

                        if (
                            liveResult != null
                        ) {

                            navController.navigate(
                                Screen.Editor.build(
                                    liveResult.id
                                )
                            )
                        }
                    }
                )
            }
        }

        /*
         * =====================================================
         * EDITOR / STRUCTURED RESULT
         * =====================================================
         */

        composable(
            Screen.Editor.route
        ) { entry ->

            val scanId =
                entry.arguments
                    ?.getString(
                        "scanId"
                    )

            val result =
                when {

                    /*
                     * Scan yang baru saja dilakukan.
                     */
                    scanId != null &&
                            scanId ==
                            currentLiveResult?.id -> {

                        currentLiveResult
                    }

                    /*
                     * Scan dari Room / history.
                     */
                    scanId != null -> {

                        history.find {
                            it.id == scanId
                        }
                    }

                    else -> {

                        null
                    }
                }

            if (
                result == null
            ) {

                LoadingHistoryItem()

            } else {

                var showExport by
                remember {

                    mutableStateOf(
                        false
                    )
                }

                ResultEditorScreen(

                    result =
                        result,

                    onBack = {

                        navController.popBackStack()
                    },

                    onShare = {

                        showExport =
                            true
                    },

                    /*
                     * =========================================
                     * SAVE REAL RESULT TO ROOM
                     * =========================================
                     */

                    onOpenHistory = {

                        historyViewModel
                            .saveScan(
                                result
                            ) {

                                /*
                                 * Bersihkan scan flow.
                                 *
                                 * Hasil:
                                 *
                                 * Home
                                 * ↓
                                 * History
                                 *
                                 * Tidak:
                                 *
                                 * Home
                                 * Camera
                                 * Preview
                                 * Detection
                                 * Editor
                                 * History
                                 */

                                navController.navigate(
                                    Screen.History.route
                                ) {

                                    launchSingleTop =
                                        true

                                    popUpTo(
                                        Screen.Home.route
                                    ) {

                                        inclusive =
                                            false
                                    }
                                }

                                /*
                                 * Scan sudah masuk Room.
                                 * Runtime state boleh dibersihkan.
                                 */

                                currentAiResult =
                                    null

                                currentLiveResult =
                                    null

                                currentImageUri =
                                    null
                            }
                    },

                    onExport = {

                        showExport =
                            true
                    }
                )

                if (
                    showExport
                ) {

                    val label =
                        when (
                            result.domain
                        ) {

                            SymbolDomain.MATH ->
                                "LaTeX"

                            SymbolDomain.CHEMISTRY ->
                                "SMILES"

                            SymbolDomain.ELECTRONICS ->
                                "Netlist"

                            else ->
                                "Teks"
                        }

                    ExportBottomSheet(

                        codeLabel =
                            label,

                        onDismiss = {

                            showExport =
                                false
                        },

                        onAction = {

                            showExport =
                                false
                        }
                    )
                }
            }
        }

        /*
         * =====================================================
         * HISTORY
         * =====================================================
         */

        composable(
            Screen.History.route
        ) {

            HistoryScreen(

                selectedTab =
                    BottomNavTab.HISTORY,

                historyItems =
                    history,

                onTabSelected =
                    ::openTab,

                onOpenDetail = { scanId ->

                    navController.navigate(
                        Screen.HistoryDetail.build(
                            scanId
                        )
                    )
                }
            )
        }

        /*
         * =====================================================
         * HISTORY DETAIL
         * =====================================================
         */

        composable(
            Screen.HistoryDetail.route
        ) { entry ->

            val scanId =
                entry.arguments
                    ?.getString(
                        "scanId"
                    )

            val result =
                scanId
                    ?.let { id ->

                        history.find {
                            it.id == id
                        }
                    }

            if (
                result == null
            ) {

                LoadingHistoryItem()

            } else {

                HistoryDetailScreen(

                    result =
                        result,

                    onBack = {

                        navController.popBackStack()
                    },

                    onDelete = {

                        historyViewModel
                            .deleteScan(
                                result.id
                            ) {

                                navController
                                    .popBackStack()
                            }
                    },

                    onExport = {

                        navController.navigate(
                            Screen.Editor.build(
                                result.id
                            )
                        )
                    }
                )
            }
        }

        /*
         * =====================================================
         * SYMBOL LIBRARY
         * =====================================================
         */

        composable(
            Screen.Library.route
        ) {

            SymbolLibraryScreen(

                selectedTab =
                    BottomNavTab.LIBRARY,

                onTabSelected =
                    ::openTab,

                onOpenSymbol = { symbolId ->

                    navController.navigate(
                        Screen.SymbolDetail.build(
                            symbolId
                        )
                    )
                }
            )
        }

        /*
         * =====================================================
         * SYMBOL DETAIL
         * =====================================================
         */

        composable(
            Screen.SymbolDetail.route
        ) { entry ->

            val id =
                entry.arguments
                    ?.getString(
                        "symbolId"
                    )

            /*
             * Ini boleh SampleData.
             *
             * Karena Symbol Library adalah
             * static reference library,
             * bukan output scan AI.
             */

            val symbol =
                SampleData
                    .symbolLibrary
                    .find {
                        it.id == id
                    }
                    ?: SampleData
                        .symbolLibrary
                        .first()

            SymbolDetailScreen(

                entry =
                    symbol,

                onBack = {

                    navController.popBackStack()
                },

                onSpeak = {
                    // TODO
                }
            )
        }

        /*
         * =====================================================
         * SETTINGS
         * =====================================================
         */

        composable(
            Screen.Settings.route
        ) {

            SettingsScreen(

                onBack = {

                    navController.popBackStack()
                },

                onOpenHistory = {

                    openTab(
                        BottomNavTab.HISTORY
                    )
                },

                onClearHistory = {

                    historyViewModel
                        .clearHistory()
                },

                onAbout = {
                    // TODO
                }
            )
        }
    }
}


/*
 * =============================================================
 * LOADING
 * =============================================================
 */

@Composable
private fun LoadingHistoryItem() {

    Box(
        modifier =
            Modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
    ) {

        androidx.compose.foundation.layout.Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator()

            Text(
                text =
                    "Memuat data...",
                style =
                    MaterialTheme.typography.bodySmall
            )
        }
    }
}