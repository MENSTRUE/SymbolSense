package com.symbolsense.ai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.MediaStore;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0017\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\tH\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u000bH\u00c6\u0003J\t\u0010 \u001a\u00020\rH\u00c6\u0003JK\u0010!\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\rH\u00c6\u0001J\u0013\u0010\"\u001a\u00020\r2\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010$\u001a\u00020%H\u00d6\u0001J\t\u0010&\u001a\u00020\'H\u00d6\u0001R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a\u00a8\u0006("}, d2 = {"Lcom/symbolsense/ai/RecognizedSymbol;", "", "prediction", "Lcom/symbolsense/ai/SymbolPrediction;", "topK", "", "detectorConfidence", "", "boundingBox", "Lcom/symbolsense/ai/RecognitionBoundingBox;", "classifierInferenceTimeMs", "", "reliable", "", "(Lcom/symbolsense/ai/SymbolPrediction;Ljava/util/List;FLcom/symbolsense/ai/RecognitionBoundingBox;DZ)V", "getBoundingBox", "()Lcom/symbolsense/ai/RecognitionBoundingBox;", "getClassifierInferenceTimeMs", "()D", "getDetectorConfidence", "()F", "getPrediction", "()Lcom/symbolsense/ai/SymbolPrediction;", "getReliable", "()Z", "getTopK", "()Ljava/util/List;", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "toString", "", "app_debug"})
public final class RecognizedSymbol {
    @org.jetbrains.annotations.NotNull()
    private final com.symbolsense.ai.SymbolPrediction prediction = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.symbolsense.ai.SymbolPrediction> topK = null;
    private final float detectorConfidence = 0.0F;
    @org.jetbrains.annotations.NotNull()
    private final com.symbolsense.ai.RecognitionBoundingBox boundingBox = null;
    private final double classifierInferenceTimeMs = 0.0;
    private final boolean reliable = false;
    
    public RecognizedSymbol(@org.jetbrains.annotations.NotNull()
    com.symbolsense.ai.SymbolPrediction prediction, @org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.ai.SymbolPrediction> topK, float detectorConfidence, @org.jetbrains.annotations.NotNull()
    com.symbolsense.ai.RecognitionBoundingBox boundingBox, double classifierInferenceTimeMs, boolean reliable) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.SymbolPrediction getPrediction() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.symbolsense.ai.SymbolPrediction> getTopK() {
        return null;
    }
    
    public final float getDetectorConfidence() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.RecognitionBoundingBox getBoundingBox() {
        return null;
    }
    
    public final double getClassifierInferenceTimeMs() {
        return 0.0;
    }
    
    public final boolean getReliable() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.SymbolPrediction component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.symbolsense.ai.SymbolPrediction> component2() {
        return null;
    }
    
    public final float component3() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.RecognitionBoundingBox component4() {
        return null;
    }
    
    public final double component5() {
        return 0.0;
    }
    
    public final boolean component6() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.RecognizedSymbol copy(@org.jetbrains.annotations.NotNull()
    com.symbolsense.ai.SymbolPrediction prediction, @org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.ai.SymbolPrediction> topK, float detectorConfidence, @org.jetbrains.annotations.NotNull()
    com.symbolsense.ai.RecognitionBoundingBox boundingBox, double classifierInferenceTimeMs, boolean reliable) {
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