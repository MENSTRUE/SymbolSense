package com.symbolsense.ai;

import android.graphics.Bitmap;

/**
 * Temporary in-memory debug snapshot for the latest formula recognition.
 *
 * This is intentionally NOT persisted and NOT part of the production model
 * contract. It exists only to diagnose detector-crop -> classifier problems.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001BK\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u0003\u0012\u0006\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0010J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0005H\u00c6\u0003J\t\u0010 \u001a\u00020\u0005H\u00c6\u0003J\t\u0010!\u001a\u00020\bH\u00c6\u0003J\u000f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u00c6\u0003J\t\u0010#\u001a\u00020\rH\u00c6\u0003J\t\u0010$\u001a\u00020\u0003H\u00c6\u0003J\t\u0010%\u001a\u00020\u0003H\u00c6\u0003J_\u0010&\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\'\u001a\u00020(2\b\u0010)\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010*\u001a\u00020\u0003H\u00d6\u0001J\t\u0010+\u001a\u00020,H\u00d6\u0001R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0014R\u0011\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001aR\u0011\u0010\u000e\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001a\u00a8\u0006-"}, d2 = {"Lcom/symbolsense/ai/FormulaDebugCrop;", "", "detectorIndex", "", "rawCrop", "Landroid/graphics/Bitmap;", "canonicalClassifierInput", "detectorConfidence", "", "classifierTopK", "", "Lcom/symbolsense/ai/SymbolPrediction;", "boundingBox", "Lcom/symbolsense/ai/RecognitionBoundingBox;", "rawWidth", "rawHeight", "(ILandroid/graphics/Bitmap;Landroid/graphics/Bitmap;FLjava/util/List;Lcom/symbolsense/ai/RecognitionBoundingBox;II)V", "getBoundingBox", "()Lcom/symbolsense/ai/RecognitionBoundingBox;", "getCanonicalClassifierInput", "()Landroid/graphics/Bitmap;", "getClassifierTopK", "()Ljava/util/List;", "getDetectorConfidence", "()F", "getDetectorIndex", "()I", "getRawCrop", "getRawHeight", "getRawWidth", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
public final class FormulaDebugCrop {
    private final int detectorIndex = 0;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Bitmap rawCrop = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Bitmap canonicalClassifierInput = null;
    private final float detectorConfidence = 0.0F;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.symbolsense.ai.SymbolPrediction> classifierTopK = null;
    @org.jetbrains.annotations.NotNull()
    private final com.symbolsense.ai.RecognitionBoundingBox boundingBox = null;
    private final int rawWidth = 0;
    private final int rawHeight = 0;
    
    public FormulaDebugCrop(int detectorIndex, @org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap rawCrop, @org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap canonicalClassifierInput, float detectorConfidence, @org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.ai.SymbolPrediction> classifierTopK, @org.jetbrains.annotations.NotNull()
    com.symbolsense.ai.RecognitionBoundingBox boundingBox, int rawWidth, int rawHeight) {
        super();
    }
    
    public final int getDetectorIndex() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap getRawCrop() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap getCanonicalClassifierInput() {
        return null;
    }
    
    public final float getDetectorConfidence() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.symbolsense.ai.SymbolPrediction> getClassifierTopK() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.RecognitionBoundingBox getBoundingBox() {
        return null;
    }
    
    public final int getRawWidth() {
        return 0;
    }
    
    public final int getRawHeight() {
        return 0;
    }
    
    public final int component1() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap component3() {
        return null;
    }
    
    public final float component4() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.symbolsense.ai.SymbolPrediction> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.RecognitionBoundingBox component6() {
        return null;
    }
    
    public final int component7() {
        return 0;
    }
    
    public final int component8() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.FormulaDebugCrop copy(int detectorIndex, @org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap rawCrop, @org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap canonicalClassifierInput, float detectorConfidence, @org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.ai.SymbolPrediction> classifierTopK, @org.jetbrains.annotations.NotNull()
    com.symbolsense.ai.RecognitionBoundingBox boundingBox, int rawWidth, int rawHeight) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}