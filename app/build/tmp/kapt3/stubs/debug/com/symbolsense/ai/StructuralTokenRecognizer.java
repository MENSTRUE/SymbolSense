package com.symbolsense.ai;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Separate structural recognizer for tokens intentionally outside the exact
 * 32-class classifier. V3 scope: '=' only.
 *
 * IMPORTANT: this recognizer is intentionally conservative. It is only called
 * after classifier inference and should not turn arbitrary textured crops into '='.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0015\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J \u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\u0004H\u0002J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rJ\u0018\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\f\u001a\u00020\rJ \u0010\u0014\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\r2\u0006\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0017\u001a\u00020\u0004H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/symbolsense/ai/StructuralTokenRecognizer;", "", "()V", "EQUAL_ID", "", "borderMedian", "gray", "", "w", "h", "isMeaningfulCrop", "", "bitmap", "Landroid/graphics/Bitmap;", "percentile", "values", "p", "", "recognizeEqual", "Lcom/symbolsense/ai/SymbolPrediction;", "resizeGray", "source", "outW", "outH", "app_debug"})
public final class StructuralTokenRecognizer {
    private static final int EQUAL_ID = -100;
    @org.jetbrains.annotations.NotNull()
    public static final com.symbolsense.ai.StructuralTokenRecognizer INSTANCE = null;
    
    private StructuralTokenRecognizer() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.symbolsense.ai.SymbolPrediction recognizeEqual(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap) {
        return null;
    }
    
    public final boolean isMeaningfulCrop(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap) {
        return false;
    }
    
    private final int[] resizeGray(android.graphics.Bitmap source, int outW, int outH) {
        return null;
    }
    
    private final int borderMedian(int[] gray, int w, int h) {
        return 0;
    }
    
    private final int percentile(int[] values, float p) {
        return 0;
    }
}