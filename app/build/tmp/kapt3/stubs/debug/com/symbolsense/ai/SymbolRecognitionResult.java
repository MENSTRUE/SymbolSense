package com.symbolsense.ai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0011\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\tH\u00c6\u0003J7\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u00c6\u0001J\u0013\u0010\u0018\u001a\u00020\t2\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001J\t\u0010\u001c\u001a\u00020\u001dH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006\u001e"}, d2 = {"Lcom/symbolsense/ai/SymbolRecognitionResult;", "", "best", "Lcom/symbolsense/ai/SymbolPrediction;", "topK", "", "inferenceTimeMs", "", "reliable", "", "(Lcom/symbolsense/ai/SymbolPrediction;Ljava/util/List;DZ)V", "getBest", "()Lcom/symbolsense/ai/SymbolPrediction;", "getInferenceTimeMs", "()D", "getReliable", "()Z", "getTopK", "()Ljava/util/List;", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "", "toString", "", "app_debug"})
public final class SymbolRecognitionResult {
    @org.jetbrains.annotations.NotNull()
    private final com.symbolsense.ai.SymbolPrediction best = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.symbolsense.ai.SymbolPrediction> topK = null;
    private final double inferenceTimeMs = 0.0;
    private final boolean reliable = false;
    
    public SymbolRecognitionResult(@org.jetbrains.annotations.NotNull()
    com.symbolsense.ai.SymbolPrediction best, @org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.ai.SymbolPrediction> topK, double inferenceTimeMs, boolean reliable) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.SymbolPrediction getBest() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.symbolsense.ai.SymbolPrediction> getTopK() {
        return null;
    }
    
    public final double getInferenceTimeMs() {
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
    
    public final double component3() {
        return 0.0;
    }
    
    public final boolean component4() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.SymbolRecognitionResult copy(@org.jetbrains.annotations.NotNull()
    com.symbolsense.ai.SymbolPrediction best, @org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.ai.SymbolPrediction> topK, double inferenceTimeMs, boolean reliable) {
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