package com.symbolsense.ai;

import android.net.Uri;
import android.util.Log;
import androidx.compose.runtime.Composable;
import kotlinx.coroutines.Dispatchers;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000$\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0000\u001a:\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00030\u00072\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00030\u0007H\u0007\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"FORMULA_TAG", "", "FormulaAiEffect", "", "imageUri", "Landroid/net/Uri;", "onSuccess", "Lkotlin/Function1;", "Lcom/symbolsense/ai/SymbolRecognitionResult;", "onError", "", "app_debug"})
public final class FormulaAiEffectKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String FORMULA_TAG = "SymbolSenseFormulaAI";
    
    /**
     * Compose bridge for the full V1 formula pipeline.
     *
     * image -> detector -> many crops -> SymbolClassifier -> linear parser
     */
    @androidx.compose.runtime.Composable()
    public static final void FormulaAiEffect(@org.jetbrains.annotations.Nullable()
    android.net.Uri imageUri, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.symbolsense.ai.SymbolRecognitionResult, kotlin.Unit> onSuccess, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Throwable, kotlin.Unit> onError) {
    }
}