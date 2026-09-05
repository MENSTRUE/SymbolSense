package com.symbolsense.ui.screens.detection;

import android.net.Uri;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.text.font.FontWeight;
import com.symbolsense.ai.SymbolRecognitionResult;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000,\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\"\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u0003\u001a6\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\rH\u0007\u00a8\u0006\u000f"}, d2 = {"DetailRow", "", "label", "", "value", "mono", "", "DetectionResultScreen", "result", "Lcom/symbolsense/ai/SymbolRecognitionResult;", "imageUri", "Landroid/net/Uri;", "onBack", "Lkotlin/Function0;", "onViewStructuredResult", "app_debug"})
public final class DetectionResultScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void DetectionResultScreen(@org.jetbrains.annotations.NotNull()
    com.symbolsense.ai.SymbolRecognitionResult result, @org.jetbrains.annotations.Nullable()
    android.net.Uri imageUri, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onViewStructuredResult) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DetailRow(java.lang.String label, java.lang.String value, boolean mono) {
    }
}