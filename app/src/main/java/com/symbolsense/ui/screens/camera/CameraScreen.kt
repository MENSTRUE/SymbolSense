package com.symbolsense.ui.screens.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.symbolsense.data.model.SymbolDomain
import com.symbolsense.ui.components.SystemBarsForScreen
import com.symbolsense.ui.theme.CameraBlack
import com.symbolsense.ui.theme.CyanAccent
import java.io.File
import java.util.concurrent.Executor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CameraScreen(
    onClose: () -> Unit,
    onImageReady: (Uri) -> Unit
) {
    SystemBarsForScreen(darkIcons = false)

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember(context) { ContextCompat.getMainExecutor(context) }
    val scope = rememberCoroutineScope()

    /*
     * Camera framing guide.
     *
     * Ditampilkan otomatis hanya sekali saat user pertama kali masuk kamera.
     * Setelah itu user tetap bisa membukanya lagi lewat tombol info (?) di atas.
     *
     * Ini bukan aturan "kamera harus jauh".
     * Tujuannya memastikan seluruh rumus masuk frame dengan margin yang cukup,
     * karena detector lebih stabil ketika simbol tidak terlalu memenuhi layar.
     */
    val framingGuidePrefs = remember(context) {
        context.getSharedPreferences(
            "symbolsense_camera_guide",
            Context.MODE_PRIVATE
        )
    }

    var showFramingGuide by remember {
        mutableStateOf(
            !framingGuidePrefs.getBoolean(
                "framing_guide_seen",
                false
            )
        )
    }

    fun dismissFramingGuide() {
        framingGuidePrefs
            .edit()
            .putBoolean(
                "framing_guide_seen",
                true
            )
            .apply()

        showFramingGuide = false
    }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { sourceUri ->
        if (sourceUri != null) {
            scope.launch {
                val localUri = copyGalleryImageToAppStorage(context, sourceUri)
                if (localUri != null) onImageReady(localUri)
            }
        }
    }

    if (!hasPermission) {
        CameraPermissionScreen(
            onClose = onClose,
            onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            onOpenGallery = { galleryLauncher.launch("image/*") }
        )
        return
    }

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    var flashOn by remember { mutableStateOf(false) }
    var domain by remember { mutableStateOf(SymbolDomain.AUTO) }
    var showModes by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }
    var cameraReady by remember { mutableStateOf(false) }

    val modes = listOf(
        SymbolDomain.AUTO,
        SymbolDomain.MATH,
        SymbolDomain.CHEMISTRY,
        SymbolDomain.ELECTRONICS
    )

    DisposableEffect(lifecycleOwner, hasPermission) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        var provider: ProcessCameraProvider? = null

        val listener = Runnable {
            runCatching {
                provider = providerFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                provider?.unbindAll()
                provider?.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
                cameraReady = true
            }.onFailure {
                cameraReady = false
            }
        }

        providerFuture.addListener(listener, mainExecutor)

        onDispose {
            cameraReady = false
            runCatching { provider?.unbindAll() }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(CameraBlack)
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        RuleOfThirds()

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DarkCircleButton(onClick = onClose) {
                Icon(
                    Icons.Filled.Close,
                    "Tutup",
                    tint = Color.White
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DarkCircleButton(
                    onClick = {
                        showFramingGuide = true
                    }
                ) {
                    Icon(
                        Icons.Filled.Info,
                        "Panduan pemindaian",
                        tint = Color.White
                    )
                }

                DarkCircleButton(
                    onClick = {
                        flashOn = !flashOn
                    }
                ) {
                    Icon(
                        if (flashOn) {
                            Icons.Filled.FlashOn
                        } else {
                            Icons.Filled.FlashOff
                        },
                        "Flash",
                        tint = if (flashOn) {
                            Color(0xFFFACC15)
                        } else {
                            Color.White
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*
             * Frame dibuat lebih lebar daripada versi awal karena target utama
             * sekarang adalah formula multi-symbol, bukan hanya satu simbol.
             *
             * User tidak harus "memotret dari jauh"; yang penting seluruh rumus
             * masuk di area ini dan masih punya sedikit ruang di kiri-kanan.
             */
            ScannerFrame(
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .aspectRatio(2.15f)
            )

            Spacer(
                Modifier.height(14.dp)
            )

            Text(
                if (cameraReady) {
                    "Pastikan seluruh rumus masuk bingkai"
                } else {
                    "Menyiapkan kamera..."
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.82f)
            )

            if (cameraReady) {
                Spacer(
                    Modifier.height(6.dp)
                )

                Surface(
                    color = Color.Black.copy(alpha = 0.48f),
                    shape = MaterialTheme.shapes.large,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = 0.10f)
                    )
                ) {
                    Text(
                        "Beri sedikit jarak • hindari memenuhi seluruh layar",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.72f)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showModes,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 118.dp)
                .navigationBarsPadding()
        ) {
            Surface(
                color = Color(0xEE121010),
                shape = MaterialTheme.shapes.large,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.10f)
                )
            ) {
                Column(Modifier.fillMaxWidth()) {
                    modes.forEachIndexed { index, item ->
                        if (index > 0) {
                            androidx.compose.material3.HorizontalDivider(
                                color = Color.White.copy(alpha = 0.08f)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    domain = item
                                    showModes = false
                                }
                                .padding(horizontal = 16.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                item.label,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (domain == item) {
                                    CyanAccent
                                } else {
                                    Color.White.copy(alpha = 0.76f)
                                }
                            )
                            if (domain == item) {
                                Icon(
                                    Icons.Filled.Check,
                                    null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.50f))
                .navigationBarsPadding()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { galleryLauncher.launch("image/*") }
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Filled.Image,
                    "Galeri",
                    tint = Color.White.copy(alpha = 0.76f),
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    "Galeri",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.54f)
                )
            }

            Surface(
                onClick = {
                    if (!isCapturing && cameraReady) {
                        isCapturing = true
                        capturePhoto(
                            context = context,
                            imageCapture = imageCapture,
                            executor = mainExecutor,
                            flashOn = flashOn,
                            rotation = previewView.display?.rotation,
                            onSaved = { uri ->
                                isCapturing = false
                                onImageReady(uri)
                            },
                            onError = {
                                isCapturing = false
                            }
                        )
                    }
                },
                shape = CircleShape,
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(2.5.dp, CyanAccent),
                modifier = Modifier.size(68.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(7.dp)
                        .border(2.dp, Color.Black.copy(alpha = 0.10f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = CyanAccent,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { showModes = !showModes }
                    .padding(8.dp)
            ) {
                Text(
                    "Mode",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.48f)
                )
                Text(
                    domain.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.88f)
                )
            }
        }

        /*
         * First-time framing guide / coachmark.
         *
         * Tidak muncul setiap kali scan. Setelah "Mengerti", preferensi disimpan.
         * Tombol info di bagian atas bisa membuka panduan ini lagi kapan saja.
         */
        if (showFramingGuide) {
            CameraFramingGuide(
                onDismiss = {
                    dismissFramingGuide()
                }
            )
        }
    }
}

@Composable
private fun CameraFramingGuide(
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(
                    alpha = 0.64f
                )
            )
            .padding(
                horizontal = 24.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xF21B1919),
            shape = MaterialTheme.shapes.extraLarge,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color.White.copy(
                    alpha = 0.12f
                )
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 20.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CyanAccent.copy(
                            alpha = 0.14f
                        ),
                        modifier = Modifier.size(
                            42.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.PhotoCamera,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(
                                    21.dp
                                )
                            )
                        }
                    }

                    Spacer(
                        Modifier.size(
                            12.dp
                        )
                    )

                    Column {
                        Text(
                            "Posisikan rumus dengan benar",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )

                        Text(
                            "Tidak perlu terlalu dekat dengan tulisan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(
                                alpha = 0.62f
                            )
                        )
                    }
                }

                Spacer(
                    Modifier.height(
                        18.dp
                    )
                )

                /*
                 * Visual mini-frame so the instruction is immediately obvious.
                 */
                Surface(
                    color = Color.Black.copy(
                        alpha = 0.34f
                    ),
                    shape = MaterialTheme.shapes.medium,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        CyanAccent.copy(
                            alpha = 0.48f
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(
                            2.5f
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = 22.dp,
                                vertical = 12.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "a + b ÷ 2",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                    }
                }

                Spacer(
                    Modifier.height(
                        16.dp
                    )
                )

                FramingGuideRow(
                    text = "Pastikan seluruh rumus terlihat."
                )

                Spacer(
                    Modifier.height(
                        8.dp
                    )
                )

                FramingGuideRow(
                    text = "Sisakan sedikit ruang di kiri, kanan, atas, dan bawah."
                )

                Spacer(
                    Modifier.height(
                        8.dp
                    )
                )

                FramingGuideRow(
                    text = "Hindari zoom terlalu dekat hingga simbol memenuhi layar."
                )

                Spacer(
                    Modifier.height(
                        8.dp
                    )
                )

                FramingGuideRow(
                    text = "Usahakan kamera sejajar dan gambar tidak miring."
                )

                Spacer(
                    Modifier.height(
                        20.dp
                    )
                )

                Surface(
                    onClick = onDismiss,
                    shape = MaterialTheme.shapes.medium,
                    color = CyanAccent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            48.dp
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(
                                18.dp
                            )
                        )

                        Spacer(
                            Modifier.size(
                                8.dp
                            )
                        )

                        Text(
                            "Mengerti",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FramingGuideRow(
    text: String
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Text(
            "✓",
            style = MaterialTheme.typography.bodyMedium,
            color = CyanAccent
        )

        Spacer(
            Modifier.size(
                10.dp
            )
        )

        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(
                alpha = 0.82f
            )
        )
    }
}

private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    flashOn: Boolean,
    rotation: Int?,
    onSaved: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val directory = File(context.filesDir, "scans").apply { mkdirs() }
    val file = File(directory, "scan_${System.currentTimeMillis()}.jpg")

    if (rotation != null) imageCapture.targetRotation = rotation
    imageCapture.flashMode = if (flashOn) {
        ImageCapture.FLASH_MODE_ON
    } else {
        ImageCapture.FLASH_MODE_OFF
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onSaved(Uri.fromFile(file))
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}

private suspend fun copyGalleryImageToAppStorage(context: Context, sourceUri: Uri): Uri? {
    return withContext(Dispatchers.IO) {
        runCatching {
            val mime = context.contentResolver.getType(sourceUri).orEmpty()
            val extension = when {
                mime.contains("png", ignoreCase = true) -> "png"
                mime.contains("webp", ignoreCase = true) -> "webp"
                else -> "jpg"
            }

            val directory = File(context.filesDir, "scans").apply { mkdirs() }
            val destination = File(
                directory,
                "gallery_${System.currentTimeMillis()}.$extension"
            )

            context.contentResolver.openInputStream(sourceUri).use { input ->
                requireNotNull(input) { "Tidak dapat membuka gambar." }
                destination.outputStream().use { output -> input.copyTo(output) }
            }

            Uri.fromFile(destination)
        }.getOrNull()
    }
}

@Composable
private fun CameraPermissionScreen(
    onClose: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenGallery: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CameraBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        DarkCircleButton(
            onClick = onClose,
            content = { Icon(Icons.Filled.Close, "Tutup", tint = Color.White) }
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = CyanAccent.copy(alpha = 0.12f),
                modifier = Modifier.size(72.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.PhotoCamera,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "Izin kamera diperlukan",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Izinkan akses kamera untuk memindai simbol. Kamu tetap bisa memilih gambar dari galeri.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.62f)
            )
            Spacer(Modifier.height(22.dp))
            Surface(
                onClick = onRequestPermission,
                shape = MaterialTheme.shapes.medium,
                color = CyanAccent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Izinkan kamera", color = Color.White)
                }
            }
            Spacer(Modifier.height(10.dp))
            Surface(
                onClick = onOpenGallery,
                shape = MaterialTheme.shapes.medium,
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Pilih dari galeri", color = Color.White.copy(alpha = 0.84f))
                }
            }
        }
    }
}

@Composable
private fun RuleOfThirds() {
    Canvas(Modifier.fillMaxSize()) {
        val color = Color.White.copy(alpha = 0.06f)
        drawLine(
            color,
            Offset(size.width / 3f, 0f),
            Offset(size.width / 3f, size.height),
            1f
        )
        drawLine(
            color,
            Offset(size.width * 2f / 3f, 0f),
            Offset(size.width * 2f / 3f, size.height),
            1f
        )
        drawLine(
            color,
            Offset(0f, size.height / 3f),
            Offset(size.width, size.height / 3f),
            1f
        )
        drawLine(
            color,
            Offset(0f, size.height * 2f / 3f),
            Offset(size.width, size.height * 2f / 3f),
            1f
        )
    }
}

@Composable
private fun ScannerFrame(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "scan")
    val progress by transition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(tween(1250), RepeatMode.Reverse),
        label = "scanLine"
    )

    Canvas(modifier) {
        val length = 22.dp.toPx()
        val width = 2.dp.toPx()

        fun corner(x: Float, y: Float, sx: Float, sy: Float) {
            drawLine(CyanAccent, Offset(x, y), Offset(x + sx * length, y), width)
            drawLine(CyanAccent, Offset(x, y), Offset(x, y + sy * length), width)
        }

        corner(0f, 0f, 1f, 1f)
        corner(size.width, 0f, -1f, 1f)
        corner(0f, size.height, 1f, -1f)
        corner(size.width, size.height, -1f, -1f)

        drawLine(
            CyanAccent.copy(alpha = 0.75f),
            Offset(0f, size.height * progress),
            Offset(size.width, size.height * progress),
            1.5.dp.toPx()
        )
    }
}

@Composable
private fun DarkCircleButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.45f),
        modifier = Modifier.size(40.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}