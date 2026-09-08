package com.symbolsense.ai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00a0\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0015\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0019\u0018\u0000 W2\u00020\u0001:\u0006WXYZ[\\B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0007J(\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u000b2\u0006\u0010\u001c\u001a\u00020\u000bH\u0002J \u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020 2\u0006\u0010\"\u001a\u00020#H\u0002J\b\u0010$\u001a\u00020%H\u0016J \u0010&\u001a\u00020\'2\u0006\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020)2\u0006\u0010\"\u001a\u00020#H\u0002J\u000e\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020.J\b\u0010/\u001a\u00020 H\u0002J\u0018\u00100\u001a\u00020\u000b2\u0006\u00101\u001a\u00020\u000b2\u0006\u00102\u001a\u00020\u000bH\u0002J\u0018\u00103\u001a\u00020 2\u0006\u00104\u001a\u0002052\u0006\u00106\u001a\u000205H\u0002J(\u00107\u001a\u00020\u001e2\u0006\u0010(\u001a\u00020)2\u0006\u00108\u001a\u00020\u000b2\u0006\u00109\u001a\u00020\u000b2\u0006\u0010:\u001a\u00020 H\u0002J\b\u0010;\u001a\u00020\tH\u0002J\b\u0010<\u001a\u00020=H\u0002J\u0010\u0010>\u001a\u00020\u000b2\u0006\u0010?\u001a\u00020\u000bH\u0002J\u001a\u0010@\u001a\u0004\u0018\u00010A2\u0006\u0010B\u001a\u0002052\u0006\u0010\"\u001a\u00020#H\u0002J\u001c\u0010C\u001a\b\u0012\u0004\u0012\u0002050D2\f\u0010E\u001a\b\u0012\u0004\u0012\u0002050DH\u0002J \u0010F\u001a\u00020\u000b2\u0006\u0010G\u001a\u00020\u00182\u0006\u0010H\u001a\u00020\u000b2\u0006\u0010I\u001a\u00020 H\u0002J\u0010\u0010J\u001a\u00020#2\u0006\u0010K\u001a\u00020.H\u0002J\u0018\u0010L\u001a\u00020)2\u0006\u0010M\u001a\u00020=2\u0006\u0010N\u001a\u00020\u000bH\u0002J \u0010O\u001a\u00020\u000b2\u0006\u00101\u001a\u00020\u000b2\u0006\u00102\u001a\u00020\u000b2\u0006\u0010P\u001a\u00020\u000bH\u0002J0\u0010Q\u001a\u00020\u000b2\u0006\u0010R\u001a\u00020\u00182\u0006\u0010S\u001a\u00020\u000b2\u0006\u0010T\u001a\u00020\u000b2\u0006\u0010U\u001a\u00020\u000b2\u0006\u0010V\u001a\u00020\u000bH\u0002R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000e\u001a\n \u0010*\u0004\u0018\u00010\u000f0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0011\u001a\n \u0010*\u0004\u0018\u00010\u00120\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0013\u001a\n \u0010*\u0004\u0018\u00010\r0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006]"}, d2 = {"Lcom/symbolsense/ai/SymbolDetector;", "Ljava/io/Closeable;", "context", "Landroid/content/Context;", "modelFileName", "", "configFileName", "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)V", "config", "Lcom/symbolsense/ai/SymbolDetector$DetectorConfig;", "heatmapOutputIndex", "", "heatmapOutputType", "Lorg/tensorflow/lite/DataType;", "inputQuant", "Lorg/tensorflow/lite/Tensor$QuantizationParams;", "kotlin.jvm.PlatformType", "inputTensor", "Lorg/tensorflow/lite/Tensor;", "inputType", "interpreter", "Lorg/tensorflow/lite/Interpreter;", "sizeOutputIndex", "boxBlur", "", "src", "width", "height", "radius", "centerInsideValidImage", "", "centerX", "", "centerY", "prepared", "Lcom/symbolsense/ai/SymbolDetector$PreparedInput;", "close", "", "decode", "Lcom/symbolsense/ai/SymbolDetector$DecodeResult;", "heat", "", "size", "detect", "Lcom/symbolsense/ai/SymbolDetectionResult;", "bitmap", "Landroid/graphics/Bitmap;", "effectiveBaseThreshold", "heatIndex", "y", "x", "iou", "a", "Lcom/symbolsense/ai/SymbolDetector$RawBox;", "b", "isLocalPeak", "gy", "gx", "score", "loadConfig", "loadModel", "Ljava/nio/ByteBuffer;", "luminance", "color", "mapBackToOriginal", "Lcom/symbolsense/ai/DetectorBox;", "box", "nonMaximumSuppression", "", "candidates", "percentileFromHistogram", "histogram", "count", "percentile", "prepareInput", "source", "readOutputFloat", "buffer", "outputIndex", "sizeIndex", "channel", "validBorderMedian", "gray", "left", "top", "right", "bottom", "Companion", "DecodeResult", "DetectorConfig", "Peak", "PreparedInput", "RawBox", "app_debug"})
public final class SymbolDetector implements java.io.Closeable {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String modelFileName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String configFileName = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String MODEL_FILE_NAME = "symbolsense_detector_model.tflite";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CONFIG_FILE_NAME = "detector_config.json";
    private static final int INPUT_H = 256;
    private static final int INPUT_W = 384;
    private static final int GRID_H = 64;
    private static final int GRID_W = 96;
    private static final int STRIDE = 4;
    private static final float PEAK_SCAN_FLOOR = 0.12F;
    private static final float MAX_REASONABLE_BOX_WIDTH_RATIO = 0.78F;
    private static final float MAX_REASONABLE_BOX_HEIGHT_RATIO = 0.86F;
    private static final int EDGE_GUARD_PX = 3;
    @org.jetbrains.annotations.NotNull()
    private final com.symbolsense.ai.SymbolDetector.DetectorConfig config = null;
    @org.jetbrains.annotations.NotNull()
    private final org.tensorflow.lite.Interpreter interpreter = null;
    private final org.tensorflow.lite.Tensor inputTensor = null;
    private final org.tensorflow.lite.DataType inputType = null;
    private final org.tensorflow.lite.Tensor.QuantizationParams inputQuant = null;
    private final int heatmapOutputIndex = 0;
    private final int sizeOutputIndex = 0;
    @org.jetbrains.annotations.NotNull()
    private final org.tensorflow.lite.DataType heatmapOutputType = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.symbolsense.ai.SymbolDetector.Companion Companion = null;
    
    public SymbolDetector(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String modelFileName, @org.jetbrains.annotations.NotNull()
    java.lang.String configFileName) {
        super();
    }
    
    @kotlin.jvm.Synchronized()
    @org.jetbrains.annotations.NotNull()
    public final synchronized com.symbolsense.ai.SymbolDetectionResult detect(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap) {
        return null;
    }
    
    /**
     * Formula-wide preprocessing that preserves geometry.
     *
     * Instead of assuming black strokes on a white page, it estimates a local
     * background and converts absolute local contrast into dark ink on white.
     * This handles examples such as yellow/white symbols on a green screen.
     */
    private final com.symbolsense.ai.SymbolDetector.PreparedInput prepareInput(android.graphics.Bitmap source) {
        return null;
    }
    
    private final com.symbolsense.ai.SymbolDetector.DecodeResult decode(float[] heat, float[] size, com.symbolsense.ai.SymbolDetector.PreparedInput prepared) {
        return null;
    }
    
    private final float effectiveBaseThreshold() {
        return 0.0F;
    }
    
    private final boolean centerInsideValidImage(float centerX, float centerY, com.symbolsense.ai.SymbolDetector.PreparedInput prepared) {
        return false;
    }
    
    private final boolean isLocalPeak(float[] heat, int gy, int gx, float score) {
        return false;
    }
    
    private final java.util.List<com.symbolsense.ai.SymbolDetector.RawBox> nonMaximumSuppression(java.util.List<com.symbolsense.ai.SymbolDetector.RawBox> candidates) {
        return null;
    }
    
    private final com.symbolsense.ai.DetectorBox mapBackToOriginal(com.symbolsense.ai.SymbolDetector.RawBox box, com.symbolsense.ai.SymbolDetector.PreparedInput prepared) {
        return null;
    }
    
    private final float[] readOutputFloat(java.nio.ByteBuffer buffer, int outputIndex) {
        return null;
    }
    
    private final int heatIndex(int y, int x) {
        return 0;
    }
    
    private final int sizeIndex(int y, int x, int channel) {
        return 0;
    }
    
    private final float iou(com.symbolsense.ai.SymbolDetector.RawBox a, com.symbolsense.ai.SymbolDetector.RawBox b) {
        return 0.0F;
    }
    
    private final com.symbolsense.ai.SymbolDetector.DetectorConfig loadConfig() {
        return null;
    }
    
    private final java.nio.ByteBuffer loadModel() {
        return null;
    }
    
    private final int luminance(int color) {
        return 0;
    }
    
    private final int validBorderMedian(int[] gray, int left, int top, int right, int bottom) {
        return 0;
    }
    
    /**
     * Fast edge-clamped box blur using an integral image.
     */
    private final int[] boxBlur(int[] src, int width, int height, int radius) {
        return null;
    }
    
    private final int percentileFromHistogram(int[] histogram, int count, float percentile) {
        return 0;
    }
    
    @java.lang.Override()
    public void close() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\fX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/symbolsense/ai/SymbolDetector$Companion;", "", "()V", "CONFIG_FILE_NAME", "", "EDGE_GUARD_PX", "", "GRID_H", "GRID_W", "INPUT_H", "INPUT_W", "MAX_REASONABLE_BOX_HEIGHT_RATIO", "", "MAX_REASONABLE_BOX_WIDTH_RATIO", "MODEL_FILE_NAME", "PEAK_SCAN_FLOOR", "STRIDE", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B#\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\u000f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\bH\u00c6\u0003J-\u0010\u0013\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0006H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u001a"}, d2 = {"Lcom/symbolsense/ai/SymbolDetector$DecodeResult;", "", "boxes", "", "Lcom/symbolsense/ai/SymbolDetector$RawBox;", "rawPeakCount", "", "threshold", "", "(Ljava/util/List;IF)V", "getBoxes", "()Ljava/util/List;", "getRawPeakCount", "()I", "getThreshold", "()F", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
    static final class DecodeResult {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.symbolsense.ai.SymbolDetector.RawBox> boxes = null;
        private final int rawPeakCount = 0;
        private final float threshold = 0.0F;
        
        public DecodeResult(@org.jetbrains.annotations.NotNull()
        java.util.List<com.symbolsense.ai.SymbolDetector.RawBox> boxes, int rawPeakCount, float threshold) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.symbolsense.ai.SymbolDetector.RawBox> getBoxes() {
            return null;
        }
        
        public final int getRawPeakCount() {
            return 0;
        }
        
        public final float getThreshold() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.symbolsense.ai.SymbolDetector.RawBox> component1() {
            return null;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final float component3() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.symbolsense.ai.SymbolDetector.DecodeResult copy(@org.jetbrains.annotations.NotNull()
        java.util.List<com.symbolsense.ai.SymbolDetector.RawBox> boxes, int rawPeakCount, float threshold) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003JE\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001d\u001a\u00020\u0006H\u00d6\u0001J\t\u0010\u001e\u001a\u00020\u001fH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\fR\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\f\u00a8\u0006 "}, d2 = {"Lcom/symbolsense/ai/SymbolDetector$DetectorConfig;", "", "confidenceThreshold", "", "nmsIouThreshold", "maxDetections", "", "maxRawPeaks", "minConfidenceFloat", "minConfidenceUInt8", "(FFIIFF)V", "getConfidenceThreshold", "()F", "getMaxDetections", "()I", "getMaxRawPeaks", "getMinConfidenceFloat", "getMinConfidenceUInt8", "getNmsIouThreshold", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
    static final class DetectorConfig {
        private final float confidenceThreshold = 0.0F;
        private final float nmsIouThreshold = 0.0F;
        private final int maxDetections = 0;
        private final int maxRawPeaks = 0;
        private final float minConfidenceFloat = 0.0F;
        private final float minConfidenceUInt8 = 0.0F;
        
        public DetectorConfig(float confidenceThreshold, float nmsIouThreshold, int maxDetections, int maxRawPeaks, float minConfidenceFloat, float minConfidenceUInt8) {
            super();
        }
        
        public final float getConfidenceThreshold() {
            return 0.0F;
        }
        
        public final float getNmsIouThreshold() {
            return 0.0F;
        }
        
        public final int getMaxDetections() {
            return 0;
        }
        
        public final int getMaxRawPeaks() {
            return 0;
        }
        
        public final float getMinConfidenceFloat() {
            return 0.0F;
        }
        
        public final float getMinConfidenceUInt8() {
            return 0.0F;
        }
        
        public final float component1() {
            return 0.0F;
        }
        
        public final float component2() {
            return 0.0F;
        }
        
        public final int component3() {
            return 0;
        }
        
        public final int component4() {
            return 0;
        }
        
        public final float component5() {
            return 0.0F;
        }
        
        public final float component6() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.symbolsense.ai.SymbolDetector.DetectorConfig copy(float confidenceThreshold, float nmsIouThreshold, int maxDetections, int maxRawPeaks, float minConfidenceFloat, float minConfidenceUInt8) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0006H\u00c6\u0003J\'\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0017"}, d2 = {"Lcom/symbolsense/ai/SymbolDetector$Peak;", "", "gy", "", "gx", "score", "", "(IIF)V", "getGx", "()I", "getGy", "getScore", "()F", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
    static final class Peak {
        private final int gy = 0;
        private final int gx = 0;
        private final float score = 0.0F;
        
        public Peak(int gy, int gx, float score) {
            super();
        }
        
        public final int getGy() {
            return 0;
        }
        
        public final int getGx() {
            return 0;
        }
        
        public final float getScore() {
            return 0.0F;
        }
        
        public final int component1() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final float component3() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.symbolsense.ai.SymbolDetector.Peak copy(int gy, int gx, float score) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0019\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\t\u0012\u0006\u0010\f\u001a\u00020\t\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\tH\u00c6\u0003J\t\u0010\u001e\u001a\u00020\tH\u00c6\u0003J\t\u0010\u001f\u001a\u00020\tH\u00c6\u0003J\t\u0010 \u001a\u00020\tH\u00c6\u0003JY\u0010!\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\t2\b\b\u0002\u0010\f\u001a\u00020\tH\u00c6\u0001J\u0013\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010%\u001a\u00020\tH\u00d6\u0001J\t\u0010&\u001a\u00020\'H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\f\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u000b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u0011\u0010\n\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0011R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0014\u00a8\u0006("}, d2 = {"Lcom/symbolsense/ai/SymbolDetector$PreparedInput;", "", "buffer", "Ljava/nio/ByteBuffer;", "scale", "", "padX", "padY", "resizedWidth", "", "resizedHeight", "originalWidth", "originalHeight", "(Ljava/nio/ByteBuffer;FFFIIII)V", "getBuffer", "()Ljava/nio/ByteBuffer;", "getOriginalHeight", "()I", "getOriginalWidth", "getPadX", "()F", "getPadY", "getResizedHeight", "getResizedWidth", "getScale", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
    static final class PreparedInput {
        @org.jetbrains.annotations.NotNull()
        private final java.nio.ByteBuffer buffer = null;
        private final float scale = 0.0F;
        private final float padX = 0.0F;
        private final float padY = 0.0F;
        private final int resizedWidth = 0;
        private final int resizedHeight = 0;
        private final int originalWidth = 0;
        private final int originalHeight = 0;
        
        public PreparedInput(@org.jetbrains.annotations.NotNull()
        java.nio.ByteBuffer buffer, float scale, float padX, float padY, int resizedWidth, int resizedHeight, int originalWidth, int originalHeight) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.nio.ByteBuffer getBuffer() {
            return null;
        }
        
        public final float getScale() {
            return 0.0F;
        }
        
        public final float getPadX() {
            return 0.0F;
        }
        
        public final float getPadY() {
            return 0.0F;
        }
        
        public final int getResizedWidth() {
            return 0;
        }
        
        public final int getResizedHeight() {
            return 0;
        }
        
        public final int getOriginalWidth() {
            return 0;
        }
        
        public final int getOriginalHeight() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.nio.ByteBuffer component1() {
            return null;
        }
        
        public final float component2() {
            return 0.0F;
        }
        
        public final float component3() {
            return 0.0F;
        }
        
        public final float component4() {
            return 0.0F;
        }
        
        public final int component5() {
            return 0;
        }
        
        public final int component6() {
            return 0;
        }
        
        public final int component7() {
            return 0;
        }
        
        public final int component8() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.symbolsense.ai.SymbolDetector.PreparedInput copy(@org.jetbrains.annotations.NotNull()
        java.nio.ByteBuffer buffer, float scale, float padX, float padY, int resizedWidth, int resizedHeight, int originalWidth, int originalHeight) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J;\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n\u00a8\u0006\u001c"}, d2 = {"Lcom/symbolsense/ai/SymbolDetector$RawBox;", "", "left", "", "top", "right", "bottom", "score", "(FFFFF)V", "getBottom", "()F", "getLeft", "getRight", "getScore", "getTop", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
    static final class RawBox {
        private final float left = 0.0F;
        private final float top = 0.0F;
        private final float right = 0.0F;
        private final float bottom = 0.0F;
        private final float score = 0.0F;
        
        public RawBox(float left, float top, float right, float bottom, float score) {
            super();
        }
        
        public final float getLeft() {
            return 0.0F;
        }
        
        public final float getTop() {
            return 0.0F;
        }
        
        public final float getRight() {
            return 0.0F;
        }
        
        public final float getBottom() {
            return 0.0F;
        }
        
        public final float getScore() {
            return 0.0F;
        }
        
        public final float component1() {
            return 0.0F;
        }
        
        public final float component2() {
            return 0.0F;
        }
        
        public final float component3() {
            return 0.0F;
        }
        
        public final float component4() {
            return 0.0F;
        }
        
        public final float component5() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.symbolsense.ai.SymbolDetector.RawBox copy(float left, float top, float right, float bottom, float score) {
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
}