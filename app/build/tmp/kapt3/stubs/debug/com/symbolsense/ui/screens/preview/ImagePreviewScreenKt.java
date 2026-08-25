package com.symbolsense.ui.screens.preview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.material.icons.Icons;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.drawscope.Stroke;
import androidx.compose.ui.layout.ContentScale;
import java.io.File;
import java.io.FileOutputStream;
import kotlinx.coroutines.Dispatchers;
import android.graphics.Canvas;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000n\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\u001a)\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\u0011\u0010\u0004\u001a\r\u0012\u0004\u0012\u00020\u00010\u0003\u00a2\u0006\u0002\b\u0005H\u0003\u001a4\u0010\u0006\u001a\u00020\u00012\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u000bH\u0007\u001a$\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000e2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00010\u000bH\u0003\u001a,\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u000bH\u0003\u001a\u0018\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u0019\u001a\u00020\u001aH\u0002\u001a0\u0010\u001b\u001a\u0004\u0018\u00010\b2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u001f\u001a\u00020\u0014H\u0082@\u00a2\u0006\u0002\u0010 \u001a\u001a\u0010!\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\"\u001a\u00020\bH\u0002\u001a:\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020(2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010)\u001a\u00020\u001a2\u0006\u0010*\u001a\u00020\u001aH\u0002\u00f8\u0001\u0000\u00a2\u0006\u0004\b+\u0010,\u001a(\u0010-\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010.\u001a\u00020$2\u0006\u0010/\u001a\u00020\u001a2\u0006\u00100\u001a\u00020\u001aH\u0002\u001a\u001a\u00101\u001a\u0004\u0018\u0001022\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\"\u001a\u00020\bH\u0002\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u00063"}, d2 = {"DarkAction", "", "onClick", "Lkotlin/Function0;", "content", "Landroidx/compose/runtime/Composable;", "ImagePreviewScreen", "imageUri", "Landroid/net/Uri;", "onBack", "onConfirm", "Lkotlin/Function1;", "InteractiveCropOverlay", "crop", "Lcom/symbolsense/ui/screens/preview/CropBounds;", "onCropChanged", "ToggleRow", "label", "", "checked", "", "onChange", "applyContrast", "Landroid/graphics/Bitmap;", "source", "factor", "", "cropImageToInternalStorage", "context", "Landroid/content/Context;", "sourceUri", "increaseContrast", "(Landroid/content/Context;Landroid/net/Uri;Lcom/symbolsense/ui/screens/preview/CropBounds;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "decodeBitmap", "uri", "detectTarget", "Lcom/symbolsense/ui/screens/preview/DragTarget;", "position", "Landroidx/compose/ui/geometry/Offset;", "size", "Landroidx/compose/ui/geometry/Size;", "cornerHitRadiusPx", "edgeHitRadiusPx", "detectTarget-gpVolqo", "(JJLcom/symbolsense/ui/screens/preview/CropBounds;FF)Lcom/symbolsense/ui/screens/preview/DragTarget;", "moveCrop", "target", "dx", "dy", "openInputStream", "Ljava/io/InputStream;", "app_debug"})
public final class ImagePreviewScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void ImagePreviewScreen(@org.jetbrains.annotations.Nullable()
    android.net.Uri imageUri, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super android.net.Uri, kotlin.Unit> onConfirm) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void InteractiveCropOverlay(com.symbolsense.ui.screens.preview.CropBounds crop, kotlin.jvm.functions.Function1<? super com.symbolsense.ui.screens.preview.CropBounds, kotlin.Unit> onCropChanged) {
    }
    
    private static final com.symbolsense.ui.screens.preview.CropBounds moveCrop(com.symbolsense.ui.screens.preview.CropBounds crop, com.symbolsense.ui.screens.preview.DragTarget target, float dx, float dy) {
        return null;
    }
    
    private static final java.lang.Object cropImageToInternalStorage(android.content.Context context, android.net.Uri sourceUri, com.symbolsense.ui.screens.preview.CropBounds crop, boolean increaseContrast, kotlin.coroutines.Continuation<? super android.net.Uri> $completion) {
        return null;
    }
    
    private static final android.graphics.Bitmap decodeBitmap(android.content.Context context, android.net.Uri uri) {
        return null;
    }
    
    private static final java.io.InputStream openInputStream(android.content.Context context, android.net.Uri uri) {
        return null;
    }
    
    private static final android.graphics.Bitmap applyContrast(android.graphics.Bitmap source, float factor) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ToggleRow(java.lang.String label, boolean checked, kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onChange) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DarkAction(kotlin.jvm.functions.Function0<kotlin.Unit> onClick, kotlin.jvm.functions.Function0<kotlin.Unit> content) {
    }
}