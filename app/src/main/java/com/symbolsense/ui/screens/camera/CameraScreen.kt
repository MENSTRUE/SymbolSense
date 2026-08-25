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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DarkCircleButton(onClick = onClose) {
                Icon(Icons.Filled.Close, "Tutup", tint = Color.White)
            }
            DarkCircleButton(onClick = { flashOn = !flashOn }) {
                Icon(
                    if (flashOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                    "Flash",
                    tint = if (flashOn) Color(0xFFFACC15) else Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScannerFrame(Modifier.fillMaxWidth(0.72f).aspectRatio(1.2f))
            Spacer(Modifier.height(14.dp))
            Text(
                if (cameraReady) "Posisikan simbol dalam bingkai" else "Menyiapkan kamera...",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.68f)
            )
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
