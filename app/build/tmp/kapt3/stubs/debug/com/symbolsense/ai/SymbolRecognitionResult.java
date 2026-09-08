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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u001d\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001Bc\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0005\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\r\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\u0002\u0010\u0012J\t\u0010\"\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005H\u00c6\u0003J\t\u0010$\u001a\u00020\u0007H\u00c6\u0003J\t\u0010%\u001a\u00020\tH\u00c6\u0003J\u000f\u0010&\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0005H\u00c6\u0003J\t\u0010\'\u001a\u00020\rH\u00c6\u0003J\t\u0010(\u001a\u00020\rH\u00c6\u0003J\t\u0010)\u001a\u00020\u0007H\u00c6\u0003J\t\u0010*\u001a\u00020\u0011H\u00c6\u0003Jo\u0010+\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00052\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\r2\b\b\u0002\u0010\u000f\u001a\u00020\u00072\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u00c6\u0001J\u0013\u0010,\u001a\u00020\t2\b\u0010-\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010.\u001a\u00020/H\u00d6\u0001J\t\u00100\u001a\u00020\rH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u000f\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0016R\u0011\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u000e\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001dR\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010 \u00a8\u00061"}, d2 = {"Lcom/symbolsense/ai/SymbolRecognitionResult;", "", "best", "Lcom/symbolsense/ai/SymbolPrediction;", "topK", "", "inferenceTimeMs", "", "reliable", "", "symbols", "Lcom/symbolsense/ai/RecognizedSymbol;", "structuredDisplay", "", "structuredLatex", "detectorInferenceTimeMs", "mode", "Lcom/symbolsense/ai/RecognitionMode;", "(Lcom/symbolsense/ai/SymbolPrediction;Ljava/util/List;DZLjava/util/List;Ljava/lang/String;Ljava/lang/String;DLcom/symbolsense/ai/RecognitionMode;)V", "getBest", "()Lcom/symbolsense/ai/SymbolPrediction;", "getDetectorInferenceTimeMs", "()D", "getInferenceTimeMs", "getMode", "()Lcom/symbolsense/ai/RecognitionMode;", "getReliable", "()Z", "getStructuredDisplay", "()Ljava/lang/String;", "getStructuredLatex", "getSymbols", "()Ljava/util/List;", "getTopK", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class SymbolRecognitionResult {
    @org.jetbrains.annotations.NotNull()
    private final com.symbolsense.ai.SymbolPrediction best = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.symbolsense.ai.SymbolPrediction> topK = null;
    private final double inferenceTimeMs = 0.0;
    private final boolean reliable = false;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.symbolsense.ai.RecognizedSymbol> symbols = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String structuredDisplay = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String structuredLatex = null;
    private final double detectorInferenceTimeMs = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final com.symbolsense.ai.RecognitionMode mode = null;
    
    public SymbolRecognitionResult(@org.jetbrains.annotations.NotNull()
    com.symbolsense.ai.SymbolPrediction best, @org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.ai.SymbolPrediction> topK, double inferenceTimeMs, boolean reliable, @org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.ai.RecognizedSymbol> symbols, @org.jetbrains.annotations.NotNull()
    java.lang.String structuredDisplay, @org.jetbrains.annotations.NotNull()
    java.lang.String structuredLatex, double detectorInferenceTimeMs, @org.jetbrains.annotations.NotNull()
    com.symbolsense.ai.RecognitionMode mode) {
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
    public final java.util.List<com.symbolsense.ai.RecognizedSymbol> getSymbols() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getStructuredDisplay() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getStructuredLatex() {
        return null;
    }
    
    public final double getDetectorInferenceTimeMs() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.RecognitionMode getMode() {
        return null;
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
    public final java.util.List<com.symbolsense.ai.RecognizedSymbol> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    public final double component8() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.RecognitionMode component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.SymbolRecognitionResult copy(@org.jetbrains.annotations.NotNull()
    com.symbolsense.ai.SymbolPrediction best, @org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.ai.SymbolPrediction> topK, double inferenceTimeMs, boolean reliable, @org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.ai.RecognizedSymbol> symbols, @org.jetbrains.annotations.NotNull()
    java.lang.String structuredDisplay, @org.jetbrains.annotations.NotNull()
    java.lang.String structuredLatex, double detectorInferenceTimeMs, @org.jetbrains.annotations.NotNull()
    com.symbolsense.ai.RecognitionMode mode) {
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