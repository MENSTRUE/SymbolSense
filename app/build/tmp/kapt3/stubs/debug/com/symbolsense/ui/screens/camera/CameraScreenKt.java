package com.symbolsense.ui.screens.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.compose.animation.core.RepeatMode;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.material.icons.Icons;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.core.content.ContextCompat;
import com.symbolsense.data.model.SymbolDomain;
import java.io.File;
import java.util.concurrent.Executor;
import kotlinx.coroutines.Dispatchers;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000T\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a2\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a*\u0010\u0006\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\bH\u0007\u001a)\u0010\n\u001a\u00020\u00012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\u0011\u0010\f\u001a\r\u0012\u0004\u0012\u00020\u00010\u0003\u00a2\u0006\u0002\b\rH\u0003\u001a\b\u0010\u000e\u001a\u00020\u0001H\u0003\u001a\u0012\u0010\u000f\u001a\u00020\u00012\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0003\u001a_\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\u0012\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\b2\u0012\u0010\u001e\u001a\u000e\u0012\u0004\u0012\u00020\u001f\u0012\u0004\u0012\u00020\u00010\bH\u0002\u00a2\u0006\u0002\u0010 \u001a \u0010!\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\"\u001a\u00020\tH\u0082@\u00a2\u0006\u0002\u0010#\u00a8\u0006$"}, d2 = {"CameraPermissionScreen", "", "onClose", "Lkotlin/Function0;", "onRequestPermission", "onOpenGallery", "CameraScreen", "onImageReady", "Lkotlin/Function1;", "Landroid/net/Uri;", "DarkCircleButton", "onClick", "content", "Landroidx/compose/runtime/Composable;", "RuleOfThirds", "ScannerFrame", "modifier", "Landroidx/compose/ui/Modifier;", "capturePhoto", "context", "Landroid/content/Context;", "imageCapture", "Landroidx/camera/core/ImageCapture;", "executor", "Ljava/util/concurrent/Executor;", "flashOn", "", "rotation", "", "onSaved", "onError", "Landroidx/camera/core/ImageCaptureException;", "(Landroid/content/Context;Landroidx/camera/core/ImageCapture;Ljava/util/concurrent/Executor;ZLjava/lang/Integer;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "copyGalleryImageToAppStorage", "sourceUri", "(Landroid/content/Context;Landroid/net/Uri;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class CameraScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void CameraScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClose, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super android.net.Uri, kotlin.Unit> onImageReady) {
    }
    
    private static final void capturePhoto(android.content.Context context, androidx.camera.core.ImageCapture imageCapture, java.util.concurrent.Executor executor, boolean flashOn, java.lang.Integer rotation, kotlin.jvm.functions.Function1<? super android.net.Uri, kotlin.Unit> onSaved, kotlin.jvm.functions.Function1<? super androidx.camera.core.ImageCaptureException, kotlin.Unit> onError) {
    }
    
    private static final java.lang.Object copyGalleryImageToAppStorage(android.content.Context context, android.net.Uri sourceUri, kotlin.coroutines.Continuation<? super android.net.Uri> $completion) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CameraPermissionScreen(kotlin.jvm.functions.Function0<kotlin.Unit> onClose, kotlin.jvm.functions.Function0<kotlin.Unit> onRequestPermission, kotlin.jvm.functions.Function0<kotlin.Unit> onOpenGallery) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void RuleOfThirds() {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ScannerFrame(androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DarkCircleButton(kotlin.jvm.functions.Function0<kotlin.Unit> onClick, kotlin.jvm.functions.Function0<kotlin.Unit> content) {
    }
}