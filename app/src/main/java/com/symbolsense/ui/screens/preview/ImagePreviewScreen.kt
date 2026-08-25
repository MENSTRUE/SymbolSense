package com.symbolsense.ui.screens.preview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas as AndroidCanvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.symbolsense.ui.components.SystemBarsForScreen
import com.symbolsense.ui.theme.CameraBlack
import com.symbolsense.ui.theme.CyanAccent
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class CropBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    companion object {
        val Default = CropBounds(0.08f, 0.10f, 0.92f, 0.90f)
    }
}

private enum class DragTarget {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
    WHOLE,
    NONE
}

@Composable
fun ImagePreviewScreen(
    imageUri: Uri?,
    onBack: () -> Unit,
    onConfirm: (Uri) -> Unit
) {
    SystemBarsForScreen(darkIcons = false)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var crop by remember(imageUri) { mutableStateOf(CropBounds.Default) }
    var contrast by remember(imageUri) { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    var imageAspectRatio by remember(imageUri) { mutableStateOf(4f / 3f) }

    fun confirmCrop() {
        val source = imageUri ?: return
        if (isProcessing) return

        scope.launch {
            isProcessing = true
            errorText = null

            val cropped = cropImageToInternalStorage(
                context = context,
                sourceUri = source,
                crop = crop,
                increaseContrast = contrast
            )

            isProcessing = false

            if (cropped != null) {
                onConfirm(cropped)
            } else {
                errorText = "Gagal memproses gambar. Coba ambil ulang foto."
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(CameraBlack)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DarkAction(onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    "Kembali",
                    tint = Color.White
                )
            }

            Text(
                "Pangkas & perbaiki",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White.copy(alpha = 0.92f)
            )

            Surface(
                onClick = ::confirmCrop,
                shape = CircleShape,
                color = CyanAccent.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    CyanAccent.copy(alpha = 0.45f)
                ),
                modifier = Modifier.size(40.dp),
                enabled = imageUri != null && !isProcessing
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = CyanAccent
                        )
                    } else {
                        Icon(
                            Icons.Filled.Check,
                            "Lanjut",
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(imageAspectRatio.coerceIn(0.45f, 2.40f))
                    .background(
                        Color.White.copy(alpha = 0.04f),
                        MaterialTheme.shapes.medium
                    )
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Gambar hasil pindai",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        onSuccess = { state ->
                            val intrinsic = state.painter.intrinsicSize
                            if (intrinsic.width > 0f && intrinsic.height > 0f) {
                                imageAspectRatio = intrinsic.width / intrinsic.height
                            }
                        }
                    )

                    InteractiveCropOverlay(
                        crop = crop,
                        onCropChanged = { crop = it }
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Gambar tidak tersedia",
                            color = Color.White.copy(alpha = 0.46f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.48f))
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                "Geser sudut kotak untuk mengatur area pangkas. Geser bagian tengah untuk memindahkan kotak.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.56f)
            )

            Spacer(Modifier.height(6.dp))

            ToggleRow("Tingkatkan kontras", contrast) { contrast = it }

            errorText?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF8A80),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    onClick = {
                        crop = CropBounds.Default
                        contrast = false
                        errorText = null
                    },
                    shape = MaterialTheme.shapes.medium,
                    color = Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = 0.16f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    enabled = !isProcessing
                ) {
                    Row(
                        Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.RotateLeft,
                            null,
                            tint = Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(
                            "Reset",
                            color = Color.White.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Surface(
                    onClick = ::confirmCrop,
                    shape = MaterialTheme.shapes.medium,
                    color = CyanAccent,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    enabled = imageUri != null && !isProcessing
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                "Konfirmasi",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveCropOverlay(
    crop: CropBounds,
    onCropChanged: (CropBounds) -> Unit
) {
    var dragTarget by remember { mutableStateOf(DragTarget.NONE) }

    // Keep the pointer detector alive while the crop state changes.
    // If `crop` is used as the pointerInput key, every pixel of movement
    // restarts the gesture detector and makes dragging feel sticky/heavy.
    val latestCrop = rememberUpdatedState(crop)
    val latestOnCropChanged = rememberUpdatedState(onCropChanged)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { position ->
                        dragTarget = detectTarget(
                            position = position,
                            size = Size(size.width.toFloat(), size.height.toFloat()),
                            crop = latestCrop.value,
                            cornerHitRadiusPx = 44.dp.toPx(),
                            edgeHitRadiusPx = 24.dp.toPx()
                        )
                    },
                    onDragEnd = { dragTarget = DragTarget.NONE },
                    onDragCancel = { dragTarget = DragTarget.NONE },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        if (dragTarget == DragTarget.NONE) return@detectDragGestures

                        val dx = dragAmount.x / size.width.toFloat().coerceAtLeast(1f)
                        val dy = dragAmount.y / size.height.toFloat().coerceAtLeast(1f)

                        latestOnCropChanged.value(
                            moveCrop(
                                crop = latestCrop.value,
                                target = dragTarget,
                                dx = dx,
                                dy = dy
                            )
                        )
                    }
                )
            }
    ) {
        val left = crop.left * size.width
        val right = crop.right * size.width
        val top = crop.top * size.height
        val bottom = crop.bottom * size.height

        val shade = Color.Black.copy(alpha = 0.48f)

        drawRect(shade, Offset.Zero, Size(size.width, top))
        drawRect(shade, Offset(0f, bottom), Size(size.width, size.height - bottom))
        drawRect(shade, Offset(0f, top), Size(left, bottom - top))
        drawRect(shade, Offset(right, top), Size(size.width - right, bottom - top))

        drawRect(
            color = CyanAccent,
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top),
            style = Stroke(2.dp.toPx())
        )

        // Visible corner handles are small, but the invisible touch target is
        // intentionally much larger so the user does not need pixel-perfect taps.
        val cornerRadius = 7.dp.toPx()
        listOf(
            Offset(left, top),
            Offset(right, top),
            Offset(left, bottom),
            Offset(right, bottom)
        ).forEach { point ->
            drawCircle(CyanAccent, cornerRadius, point)
            drawCircle(Color.White, cornerRadius * 0.45f, point)
        }

        // Small edge handles indicate that every side can also be dragged.
        val edgeRadius = 4.dp.toPx()
        listOf(
            Offset((left + right) / 2f, top),
            Offset((left + right) / 2f, bottom),
            Offset(left, (top + bottom) / 2f),
            Offset(right, (top + bottom) / 2f)
        ).forEach { point ->
            drawCircle(CyanAccent, edgeRadius, point)
        }
    }
}

private fun detectTarget(
    position: Offset,
    size: Size,
    crop: CropBounds,
    cornerHitRadiusPx: Float,
    edgeHitRadiusPx: Float
): DragTarget {
    val left = crop.left * size.width
    val right = crop.right * size.width
    val top = crop.top * size.height
    val bottom = crop.bottom * size.height

    val corners = listOf(
        DragTarget.TOP_LEFT to Offset(left, top),
        DragTarget.TOP_RIGHT to Offset(right, top),
        DragTarget.BOTTOM_LEFT to Offset(left, bottom),
        DragTarget.BOTTOM_RIGHT to Offset(right, bottom)
    )

    corners.minByOrNull { (_, point) ->
        val dx = position.x - point.x
        val dy = position.y - point.y
        dx * dx + dy * dy
    }?.let { (target, point) ->
        val dx = position.x - point.x
        val dy = position.y - point.y
        if (dx * dx + dy * dy <= cornerHitRadiusPx * cornerHitRadiusPx) {
            return target
        }
    }

    // Edges have generous invisible hit zones. Corners are checked first.
    val withinHorizontalSpan = position.x in left..right
    val withinVerticalSpan = position.y in top..bottom

    if (withinVerticalSpan && abs(position.x - left) <= edgeHitRadiusPx) {
        return DragTarget.LEFT
    }
    if (withinVerticalSpan && abs(position.x - right) <= edgeHitRadiusPx) {
        return DragTarget.RIGHT
    }
    if (withinHorizontalSpan && abs(position.y - top) <= edgeHitRadiusPx) {
        return DragTarget.TOP
    }
    if (withinHorizontalSpan && abs(position.y - bottom) <= edgeHitRadiusPx) {
        return DragTarget.BOTTOM
    }

    val inside = position.x in left..right && position.y in top..bottom
    return if (inside) DragTarget.WHOLE else DragTarget.NONE
}

private fun moveCrop(
    crop: CropBounds,
    target: DragTarget,
    dx: Float,
    dy: Float
): CropBounds {
    val minSize = 0.10f

    return when (target) {
        DragTarget.TOP_LEFT -> crop.copy(
            left = (crop.left + dx).coerceIn(0f, crop.right - minSize),
            top = (crop.top + dy).coerceIn(0f, crop.bottom - minSize)
        )

        DragTarget.TOP_RIGHT -> crop.copy(
            right = (crop.right + dx).coerceIn(crop.left + minSize, 1f),
            top = (crop.top + dy).coerceIn(0f, crop.bottom - minSize)
        )

        DragTarget.BOTTOM_LEFT -> crop.copy(
            left = (crop.left + dx).coerceIn(0f, crop.right - minSize),
            bottom = (crop.bottom + dy).coerceIn(crop.top + minSize, 1f)
        )

        DragTarget.BOTTOM_RIGHT -> crop.copy(
            right = (crop.right + dx).coerceIn(crop.left + minSize, 1f),
            bottom = (crop.bottom + dy).coerceIn(crop.top + minSize, 1f)
        )

        DragTarget.LEFT -> crop.copy(
            left = (crop.left + dx).coerceIn(0f, crop.right - minSize)
        )

        DragTarget.RIGHT -> crop.copy(
            right = (crop.right + dx).coerceIn(crop.left + minSize, 1f)
        )

        DragTarget.TOP -> crop.copy(
            top = (crop.top + dy).coerceIn(0f, crop.bottom - minSize)
        )

        DragTarget.BOTTOM -> crop.copy(
            bottom = (crop.bottom + dy).coerceIn(crop.top + minSize, 1f)
        )

        DragTarget.WHOLE -> {
            val width = crop.right - crop.left
            val height = crop.bottom - crop.top

            val newLeft = (crop.left + dx).coerceIn(0f, 1f - width)
            val newTop = (crop.top + dy).coerceIn(0f, 1f - height)

            CropBounds(
                left = newLeft,
                top = newTop,
                right = newLeft + width,
                bottom = newTop + height
            )
        }

        DragTarget.NONE -> crop
    }
}

private suspend fun cropImageToInternalStorage(
    context: Context,
    sourceUri: Uri,
    crop: CropBounds,
    increaseContrast: Boolean
): Uri? {
    return withContext(Dispatchers.IO) {
        runCatching {
            val sourceBitmap = decodeBitmap(context, sourceUri)
                ?: error("Bitmap tidak dapat dibaca")

            val left = (crop.left * sourceBitmap.width)
                .toInt()
                .coerceIn(0, sourceBitmap.width - 1)
            val top = (crop.top * sourceBitmap.height)
                .toInt()
                .coerceIn(0, sourceBitmap.height - 1)
            val right = (crop.right * sourceBitmap.width)
                .toInt()
                .coerceIn(left + 1, sourceBitmap.width)
            val bottom = (crop.bottom * sourceBitmap.height)
                .toInt()
                .coerceIn(top + 1, sourceBitmap.height)

            val cropped = Bitmap.createBitmap(
                sourceBitmap,
                left,
                top,
                right - left,
                bottom - top
            )

            val finalBitmap = if (increaseContrast) {
                applyContrast(cropped, 1.28f)
            } else {
                cropped
            }

            val directory = File(context.filesDir, "scans/cropped").apply { mkdirs() }
            val outputFile = File(directory, "crop_${System.currentTimeMillis()}.jpg")

            FileOutputStream(outputFile).use { output ->
                check(finalBitmap.compress(Bitmap.CompressFormat.JPEG, 94, output)) {
                    "Gagal menyimpan crop"
                }
            }

            if (finalBitmap !== cropped) finalBitmap.recycle()
            cropped.recycle()
            sourceBitmap.recycle()

            Uri.fromFile(outputFile)
        }.getOrNull()
    }
}

private fun decodeBitmap(context: Context, uri: Uri): Bitmap? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        runCatching {
            val source = when (uri.scheme) {
                "file" -> ImageDecoder.createSource(File(requireNotNull(uri.path)))
                else -> ImageDecoder.createSource(context.contentResolver, uri)
            }

            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                decoder.isMutableRequired = false
            }
        }.getOrNull()
    } else {
        openInputStream(context, uri)?.use { BitmapFactory.decodeStream(it) }
    }
}

private fun openInputStream(context: Context, uri: Uri) = when (uri.scheme) {
    "file" -> uri.path?.let { File(it).inputStream() }
    else -> context.contentResolver.openInputStream(uri)
}

private fun applyContrast(source: Bitmap, factor: Float): Bitmap {
    val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(output)
    val translate = (-0.5f * factor + 0.5f) * 255f

    val matrix = ColorMatrix(
        floatArrayOf(
            factor, 0f, 0f, 0f, translate,
            0f, factor, 0f, 0f, translate,
            0f, 0f, factor, 0f, translate,
            0f, 0f, 0f, 1f, 0f
        )
    )

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = ColorMatrixColorFilter(matrix)
    }

    canvas.drawBitmap(source, 0f, 0f, paint)
    return output
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(46.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.78f)
        )
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun DarkAction(onClick: () -> Unit, content: @Composable () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.10f),
        modifier = Modifier.size(40.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}
