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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0015\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u0014\n\u0002\b\b\u0018\u0000 -2\u00020\u0001:\u0001-B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0007J\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\b\b\u0002\u0010\u001b\u001a\u00020\u001cJ\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u001d\u001a\u00020\u001e2\b\b\u0002\u0010\u001b\u001a\u00020\u001cJ\b\u0010\u001f\u001a\u00020 H\u0016J\u0010\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$H\u0002J\u0010\u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020$H\u0002J\u0010\u0010(\u001a\u00020\u001a2\u0006\u0010\u001d\u001a\u00020\u001eH\u0002J\u000e\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012H\u0002J\b\u0010*\u001a\u00020\"H\u0002J\u0010\u0010+\u001a\u00020$2\u0006\u0010,\u001a\u00020\u001aH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\n \n*\u0004\u0018\u00010\f0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\r\u001a\n \n*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0014\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0015\u001a\n \n*\u0004\u0018\u00010\f0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0016\u001a\n \n*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006."}, d2 = {"Lcom/symbolsense/ai/SymbolClassifier;", "Ljava/io/Closeable;", "context", "Landroid/content/Context;", "modelFileName", "", "mappingFileName", "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)V", "inputQuantization", "Lorg/tensorflow/lite/Tensor$QuantizationParams;", "kotlin.jvm.PlatformType", "inputShape", "", "inputTensor", "Lorg/tensorflow/lite/Tensor;", "interpreter", "Lorg/tensorflow/lite/Interpreter;", "labels", "", "Lcom/symbolsense/ai/SymbolLabel;", "outputQuantization", "outputShape", "outputTensor", "classify", "Lcom/symbolsense/ai/SymbolRecognitionResult;", "bitmap", "Landroid/graphics/Bitmap;", "topK", "", "uri", "Landroid/net/Uri;", "close", "", "createInputBuffer", "Ljava/nio/ByteBuffer;", "pixels", "", "dequantizeOutput", "", "output", "loadBitmap", "loadLabels", "loadModel", "preprocess", "sourceBitmap", "Companion", "app_debug"})
public final class SymbolClassifier implements java.io.Closeable {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String modelFileName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String mappingFileName = null;
    private static final int INPUT_SIZE = 64;
    private static final int MARGIN = 5;
    private static final float MIN_CONFIDENCE = 0.6F;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.symbolsense.ai.SymbolLabel> labels = null;
    @org.jetbrains.annotations.NotNull()
    private final org.tensorflow.lite.Interpreter interpreter = null;
    private final org.tensorflow.lite.Tensor inputTensor = null;
    private final org.tensorflow.lite.Tensor outputTensor = null;
    private final int[] inputShape = null;
    private final int[] outputShape = null;
    private final org.tensorflow.lite.Tensor.QuantizationParams inputQuantization = null;
    private final org.tensorflow.lite.Tensor.QuantizationParams outputQuantization = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.symbolsense.ai.SymbolClassifier.Companion Companion = null;
    
    public SymbolClassifier(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String modelFileName, @org.jetbrains.annotations.NotNull()
    java.lang.String mappingFileName) {
        super();
    }
    
    /**
     * Untuk hasil crop yang sudah berupa Bitmap.
     */
    @kotlin.jvm.Synchronized()
    @org.jetbrains.annotations.NotNull()
    public final synchronized com.symbolsense.ai.SymbolRecognitionResult classify(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, int topK) {
        return null;
    }
    
    /**
     * Bisa langsung menerima Uri hasil Camera/Gallery/Crop.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.SymbolRecognitionResult classify(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri, int topK) {
        return null;
    }
    
    /**
     * Preprocessing ini mengikuti training Kaggle:
     *
     * grayscale
     * ↓
     * white background / black ink
     * ↓
     * crop foreground
     * ↓
     * preserve aspect ratio
     * ↓
     * margin
     * ↓
     * 64x64
     *
     * OUTPUT:
     * ByteArray grayscale 0..255
     */
    private final byte[] preprocess(android.graphics.Bitmap sourceBitmap) {
        return null;
    }
    
    /**
     * Real value saat training:
     *
     * pixel / 255.0
     *
     * Lalu dikonversi ke UINT8 sesuai
     * quantization parameter model.
     */
    private final java.nio.ByteBuffer createInputBuffer(byte[] pixels) {
        return null;
    }
    
    private final float[] dequantizeOutput(byte[] output) {
        return null;
    }
    
    private final java.util.List<com.symbolsense.ai.SymbolLabel> loadLabels() {
        return null;
    }
    
    private final java.nio.ByteBuffer loadModel() {
        return null;
    }
    
    @kotlin.Suppress(names = {"DEPRECATION"})
    private final android.graphics.Bitmap loadBitmap(android.net.Uri uri) {
        return null;
    }
    
    @java.lang.Override()
    public void close() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/symbolsense/ai/SymbolClassifier$Companion;", "", "()V", "INPUT_SIZE", "", "MARGIN", "MIN_CONFIDENCE", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}