package com.symbolsense.ai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00b4\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0015\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0016\u0018\u0000 _2\u00020\u0001:\t_`abcdefgB!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0007J(\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u000b2\u0006\u0010\u001c\u001a\u00020\u000bH\u0002J(\u0010\u001d\u001a\u00020\u00192\u0006\u0010\u001e\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u000b2\u0006\u0010\u001f\u001a\u00020\u000bH\u0002J\u001c\u0010 \u001a\u000e\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\"0!2\u0006\u0010#\u001a\u00020$H\u0002J \u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020(2\u0006\u0010)\u001a\u00020(2\u0006\u0010*\u001a\u00020+H\u0002J \u0010,\u001a\u00020\"2\u0006\u0010-\u001a\u00020\"2\u0006\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u000bH\u0002J\b\u0010.\u001a\u00020/H\u0016J&\u00100\u001a\b\u0012\u0004\u0012\u000202012\u0006\u0010-\u001a\u00020\"2\u0006\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u000bH\u0002J \u00103\u001a\u0002042\u0006\u00105\u001a\u0002062\u0006\u00107\u001a\u0002062\u0006\u00108\u001a\u00020+H\u0002J\u000e\u00109\u001a\u00020:2\u0006\u0010#\u001a\u00020$J\b\u0010;\u001a\u00020(H\u0002J\u0010\u0010<\u001a\u00020=2\u0006\u0010>\u001a\u00020$H\u0002J0\u0010?\u001a\u00020&2\u0006\u0010@\u001a\u00020(2\u0006\u0010A\u001a\u00020(2\u0006\u0010B\u001a\u00020(2\u0006\u0010C\u001a\u00020(2\u0006\u0010-\u001a\u00020\"H\u0002J\u0010\u0010D\u001a\u00020=2\u0006\u0010>\u001a\u00020$H\u0002J\u0018\u0010E\u001a\u00020\u000b2\u0006\u0010F\u001a\u00020\u000b2\u0006\u0010G\u001a\u00020\u000bH\u0002J\u0018\u0010H\u001a\u00020(2\u0006\u0010I\u001a\u00020J2\u0006\u0010K\u001a\u00020JH\u0002J(\u0010L\u001a\u00020&2\u0006\u00105\u001a\u0002062\u0006\u0010F\u001a\u00020\u000b2\u0006\u0010G\u001a\u00020\u000b2\u0006\u0010M\u001a\u00020(H\u0002J\b\u0010N\u001a\u00020\tH\u0002J\b\u0010O\u001a\u00020PH\u0002J\u001a\u0010Q\u001a\u0004\u0018\u00010R2\u0006\u0010S\u001a\u00020J2\u0006\u0010*\u001a\u00020+H\u0002J\u001c\u0010T\u001a\b\u0012\u0004\u0012\u00020J012\f\u0010U\u001a\b\u0012\u0004\u0012\u00020J01H\u0002J\u0018\u0010V\u001a\u00020\u000b2\u0006\u0010W\u001a\u00020\u00192\u0006\u0010X\u001a\u00020\u000bH\u0002J \u0010Y\u001a\u00020\u000b2\u0006\u0010W\u001a\u00020\u00192\u0006\u0010X\u001a\u00020\u000b2\u0006\u0010*\u001a\u00020(H\u0002J\u0010\u0010Z\u001a\u00020+2\u0006\u0010>\u001a\u00020$H\u0002J\u0018\u0010[\u001a\u0002062\u0006\u0010\\\u001a\u00020P2\u0006\u0010]\u001a\u00020\u000bH\u0002J \u0010^\u001a\u00020\u000b2\u0006\u0010F\u001a\u00020\u000b2\u0006\u0010G\u001a\u00020\u000b2\u0006\u0010\u001c\u001a\u00020\u000bH\u0002R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000e\u001a\n \u0010*\u0004\u0018\u00010\u000f0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0011\u001a\n \u0010*\u0004\u0018\u00010\u00120\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0013\u001a\n \u0010*\u0004\u0018\u00010\r0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006h"}, d2 = {"Lcom/symbolsense/ai/SymbolDetector;", "Ljava/io/Closeable;", "context", "Landroid/content/Context;", "modelFileName", "", "configFileName", "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)V", "config", "Lcom/symbolsense/ai/SymbolDetector$DetectorConfig;", "heatmapOutputIndex", "", "heatmapOutputType", "Lorg/tensorflow/lite/DataType;", "inputQuant", "Lorg/tensorflow/lite/Tensor$QuantizationParams;", "kotlin.jvm.PlatformType", "inputTensor", "Lorg/tensorflow/lite/Tensor;", "inputType", "interpreter", "Lorg/tensorflow/lite/Interpreter;", "sizeOutputIndex", "borderMedianChannel", "pixels", "", "w", "h", "channel", "boxBlur", "input", "radius", "canonicalizeRoi", "Lkotlin/Pair;", "", "bitmap", "Landroid/graphics/Bitmap;", "centerInsideValidImage", "", "cx", "", "cy", "p", "Lcom/symbolsense/ai/SymbolDetector$PreparedInput;", "cleanForegroundMask", "mask", "close", "", "connectedComponents", "", "Lcom/symbolsense/ai/SymbolDetector$Component;", "decode", "Lcom/symbolsense/ai/SymbolDetector$DecodeResult;", "heat", "", "size", "prepared", "detect", "Lcom/symbolsense/ai/SymbolDetectionResult;", "effectiveBaseThreshold", "estimateFormulaRoi", "Lcom/symbolsense/ai/SymbolDetector$FormulaRoi;", "source", "foregroundSupported", "left", "top", "right", "bottom", "fullRoi", "heatIndex", "gy", "gx", "iou", "a", "Lcom/symbolsense/ai/SymbolDetector$RawBox;", "b", "isLocalPeak", "score", "loadConfig", "loadModel", "Ljava/nio/ByteBuffer;", "mapBackToOriginal", "Lcom/symbolsense/ai/DetectorBox;", "box", "nonMaximumSuppression", "candidates", "otsuThreshold", "hist", "total", "percentileFromHistogram", "prepareInput", "readOutputFloat", "buffer", "outputIndex", "sizeIndex", "Companion", "Component", "DecodeResult", "DetectorConfig", "FormulaRoi", "IntArrayList", "Peak", "PreparedInput", "RawBox", "app_debug"})
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
    private static final int ANALYSIS_MAX_W = 720;
    private static final int ANALYSIS_MAX_H = 520;
    private static final int MODEL_MARGIN = 18;
    private static final float PEAK_SCAN_FLOOR = 0.14F;
    private static final int MIN_INK_PIXELS_IN_BOX = 5;
    private static final float MIN_INK_DENSITY = 0.012F;
    private static final float MAX_INK_DENSITY = 0.78F;
    private static final float MAX_REASONABLE_BOX_WIDTH_RATIO = 0.8F;
    private static final float MAX_REASONABLE_BOX_HEIGHT_RATIO = 0.8F;
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
     * 1) Find formula ROI on a medium-size image.
     * 2) Crop original bitmap to that ROI.
     * 3) Canonicalize ROI to black ink / white background.
     * 4) Letterbox canonical ROI to 384x256.
     */
    private final com.symbolsense.ai.SymbolDetector.PreparedInput prepareInput(android.graphics.Bitmap source) {
        return null;
    }
    
    /**
     * Formula ROI estimator. It intentionally uses global border color distance
     * first because screen moire has high LOCAL contrast but usually much lower
     * distance from the background color than the actual ink.
     */
    private final com.symbolsense.ai.SymbolDetector.FormulaRoi estimateFormulaRoi(android.graphics.Bitmap source) {
        return null;
    }
    
    /**
     * Canonical ROI: near-binary dark ink on a white background.
     * This deliberately suppresses screen texture before the detector.
     */
    private final kotlin.Pair<int[], byte[]> canonicalizeRoi(android.graphics.Bitmap bitmap) {
        return null;
    }
    
    private final byte[] cleanForegroundMask(byte[] mask, int w, int h) {
        return null;
    }
    
    private final com.symbolsense.ai.SymbolDetector.DecodeResult decode(float[] heat, float[] size, com.symbolsense.ai.SymbolDetector.PreparedInput prepared) {
        return null;
    }
    
    private final boolean foregroundSupported(float left, float top, float right, float bottom, byte[] mask) {
        return false;
    }
    
    private final float effectiveBaseThreshold() {
        return 0.0F;
    }
    
    private final boolean centerInsideValidImage(float cx, float cy, com.symbolsense.ai.SymbolDetector.PreparedInput p) {
        return false;
    }
    
    private final com.symbolsense.ai.DetectorBox mapBackToOriginal(com.symbolsense.ai.SymbolDetector.RawBox box, com.symbolsense.ai.SymbolDetector.PreparedInput p) {
        return null;
    }
    
    private final java.util.List<com.symbolsense.ai.SymbolDetector.RawBox> nonMaximumSuppression(java.util.List<com.symbolsense.ai.SymbolDetector.RawBox> candidates) {
        return null;
    }
    
    private final float iou(com.symbolsense.ai.SymbolDetector.RawBox a, com.symbolsense.ai.SymbolDetector.RawBox b) {
        return 0.0F;
    }
    
    private final boolean isLocalPeak(float[] heat, int gy, int gx, float score) {
        return false;
    }
    
    private final int heatIndex(int gy, int gx) {
        return 0;
    }
    
    private final int sizeIndex(int gy, int gx, int channel) {
        return 0;
    }
    
    private final float[] readOutputFloat(java.nio.ByteBuffer buffer, int outputIndex) {
        return null;
    }
    
    private final com.symbolsense.ai.SymbolDetector.DetectorConfig loadConfig() {
        return null;
    }
    
    private final java.nio.ByteBuffer loadModel() {
        return null;
    }
    
    private final int borderMedianChannel(int[] pixels, int w, int h, int channel) {
        return 0;
    }
    
    private final int otsuThreshold(int[] hist, int total) {
        return 0;
    }
    
    private final int percentileFromHistogram(int[] hist, int total, float p) {
        return 0;
    }
    
    private final int[] boxBlur(int[] input, int w, int h, int radius) {
        return null;
    }
    
    private final java.util.List<com.symbolsense.ai.SymbolDetector.Component> connectedComponents(byte[] mask, int w, int h) {
        return null;
    }
    
    private final com.symbolsense.ai.SymbolDetector.FormulaRoi fullRoi(android.graphics.Bitmap source) {
        return null;
    }
    
    @java.lang.Override()
    public void close() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\t\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\rX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\rX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\rX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/symbolsense/ai/SymbolDetector$Companion;", "", "()V", "ANALYSIS_MAX_H", "", "ANALYSIS_MAX_W", "CONFIG_FILE_NAME", "", "GRID_H", "GRID_W", "INPUT_H", "INPUT_W", "MAX_INK_DENSITY", "", "MAX_REASONABLE_BOX_HEIGHT_RATIO", "MAX_REASONABLE_BOX_WIDTH_RATIO", "MIN_INK_DENSITY", "MIN_INK_PIXELS_IN_BOX", "MODEL_FILE_NAME", "MODEL_MARGIN", "PEAK_SCAN_FLOOR", "STRIDE", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0018\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\tH\u00c6\u0003JE\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\tH\u00c6\u0001J\u0013\u0010\u001e\u001a\u00020\t2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010 \u001a\u00020\u0003H\u00d6\u0001J\t\u0010!\u001a\u00020\"H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u000e\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\fR\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u0016\u0010\f\u00a8\u0006#"}, d2 = {"Lcom/symbolsense/ai/SymbolDetector$Component;", "", "area", "", "left", "top", "right", "bottom", "touchesBorder", "", "(IIIIIZ)V", "getArea", "()I", "getBottom", "height", "getHeight", "getLeft", "getRight", "getTop", "getTouchesBorder", "()Z", "width", "getWidth", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "toString", "", "app_debug"})
    static final class Component {
        private final int area = 0;
        private final int left = 0;
        private final int top = 0;
        private final int right = 0;
        private final int bottom = 0;
        private final boolean touchesBorder = false;
        
        public Component(int area, int left, int top, int right, int bottom, boolean touchesBorder) {
            super();
        }
        
        public final int getArea() {
            return 0;
        }
        
        public final int getLeft() {
            return 0;
        }
        
        public final int getTop() {
            return 0;
        }
        
        public final int getRight() {
            return 0;
        }
        
        public final int getBottom() {
            return 0;
        }
        
        public final boolean getTouchesBorder() {
            return false;
        }
        
        public final int getWidth() {
            return 0;
        }
        
        public final int getHeight() {
            return 0;
        }
        
        public final int component1() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final int component3() {
            return 0;
        }
        
        public final int component4() {
            return 0;
        }
        
        public final int component5() {
            return 0;
        }
        
        public final boolean component6() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.symbolsense.ai.SymbolDetector.Component copy(int area, int left, int top, int right, int bottom, boolean touchesBorder) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0007J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\tR\u0011\u0010\u000f\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\t\u00a8\u0006\u001c"}, d2 = {"Lcom/symbolsense/ai/SymbolDetector$FormulaRoi;", "", "left", "", "top", "right", "bottom", "(IIII)V", "getBottom", "()I", "height", "getHeight", "getLeft", "getRight", "getTop", "width", "getWidth", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
    static final class FormulaRoi {
        private final int left = 0;
        private final int top = 0;
        private final int right = 0;
        private final int bottom = 0;
        
        public FormulaRoi(int left, int top, int right, int bottom) {
            super();
        }
        
        public final int getLeft() {
            return 0;
        }
        
        public final int getTop() {
            return 0;
        }
        
        public final int getRight() {
            return 0;
        }
        
        public final int getBottom() {
            return 0;
        }
        
        public final int getWidth() {
            return 0;
        }
        
        public final int getHeight() {
            return 0;
        }
        
        public final int component1() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final int component3() {
            return 0;
        }
        
        public final int component4() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.symbolsense.ai.SymbolDetector.FormulaRoi copy(int left, int top, int right, int bottom) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0015\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u0003J\u0011\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0003H\u0086\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001e\u0010\b\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\u0003@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0010"}, d2 = {"Lcom/symbolsense/ai/SymbolDetector$IntArrayList;", "", "initialCapacity", "", "(I)V", "data", "", "<set-?>", "size", "getSize", "()I", "add", "", "value", "get", "index", "app_debug"})
    static final class IntArrayList {
        @org.jetbrains.annotations.NotNull()
        private int[] data;
        private int size = 0;
        
        public IntArrayList(int initialCapacity) {
            super();
        }
        
        public final int getSize() {
            return 0;
        }
        
        public final void add(int value) {
        }
        
        public final int get(int index) {
            return 0;
        }
        
        public IntArrayList() {
            super();
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u001e\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001BU\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\u000b\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u000b\u0012\u0006\u0010\u0010\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\u0011J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\"\u001a\u00020\u000bH\u00c6\u0003J\t\u0010#\u001a\u00020\u0005H\u00c6\u0003J\t\u0010$\u001a\u00020\u0007H\u00c6\u0003J\t\u0010%\u001a\u00020\u0007H\u00c6\u0003J\t\u0010&\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\'\u001a\u00020\u000bH\u00c6\u0003J\t\u0010(\u001a\u00020\u000bH\u00c6\u0003J\t\u0010)\u001a\u00020\u000eH\u00c6\u0003J\t\u0010*\u001a\u00020\u000bH\u00c6\u0003Jm\u0010+\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000b2\b\b\u0002\u0010\u0010\u001a\u00020\u000bH\u00c6\u0001J\u0013\u0010,\u001a\u00020-2\b\u0010.\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010/\u001a\u00020\u000bH\u00d6\u0001J\t\u00100\u001a\u000201H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0010\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u000f\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001aR\u0011\u0010\f\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0017R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0017R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001a\u00a8\u00062"}, d2 = {"Lcom/symbolsense/ai/SymbolDetector$PreparedInput;", "", "buffer", "Ljava/nio/ByteBuffer;", "inkMask", "", "scale", "", "padX", "padY", "resizedWidth", "", "resizedHeight", "roi", "Lcom/symbolsense/ai/SymbolDetector$FormulaRoi;", "originalWidth", "originalHeight", "(Ljava/nio/ByteBuffer;[BFFFIILcom/symbolsense/ai/SymbolDetector$FormulaRoi;II)V", "getBuffer", "()Ljava/nio/ByteBuffer;", "getInkMask", "()[B", "getOriginalHeight", "()I", "getOriginalWidth", "getPadX", "()F", "getPadY", "getResizedHeight", "getResizedWidth", "getRoi", "()Lcom/symbolsense/ai/SymbolDetector$FormulaRoi;", "getScale", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
    static final class PreparedInput {
        @org.jetbrains.annotations.NotNull()
        private final java.nio.ByteBuffer buffer = null;
        @org.jetbrains.annotations.NotNull()
        private final byte[] inkMask = null;
        private final float scale = 0.0F;
        private final float padX = 0.0F;
        private final float padY = 0.0F;
        private final int resizedWidth = 0;
        private final int resizedHeight = 0;
        @org.jetbrains.annotations.NotNull()
        private final com.symbolsense.ai.SymbolDetector.FormulaRoi roi = null;
        private final int originalWidth = 0;
        private final int originalHeight = 0;
        
        public PreparedInput(@org.jetbrains.annotations.NotNull()
        java.nio.ByteBuffer buffer, @org.jetbrains.annotations.NotNull()
        byte[] inkMask, float scale, float padX, float padY, int resizedWidth, int resizedHeight, @org.jetbrains.annotations.NotNull()
        com.symbolsense.ai.SymbolDetector.FormulaRoi roi, int originalWidth, int originalHeight) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.nio.ByteBuffer getBuffer() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] getInkMask() {
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
        
        @org.jetbrains.annotations.NotNull()
        public final com.symbolsense.ai.SymbolDetector.FormulaRoi getRoi() {
            return null;
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
        
        public final int component10() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final byte[] component2() {
            return null;
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
        
        public final int component6() {
            return 0;
        }
        
        public final int component7() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.symbolsense.ai.SymbolDetector.FormulaRoi component8() {
            return null;
        }
        
        public final int component9() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.symbolsense.ai.SymbolDetector.PreparedInput copy(@org.jetbrains.annotations.NotNull()
        java.nio.ByteBuffer buffer, @org.jetbrains.annotations.NotNull()
        byte[] inkMask, float scale, float padX, float padY, int resizedWidth, int resizedHeight, @org.jetbrains.annotations.NotNull()
        com.symbolsense.ai.SymbolDetector.FormulaRoi roi, int originalWidth, int originalHeight) {
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