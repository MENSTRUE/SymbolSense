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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0094\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0015\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0014\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u001b\n\u0002\u0010\u0005\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\u0018\u0000 X2\u00020\u0001:\u0003WXYB!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0007J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J \u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\u001cH\u0002J(\u0010 \u001a\u00020\u00182\u0006\u0010!\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\u001c2\u0006\u0010\"\u001a\u00020\u001cH\u0002J\u0018\u0010#\u001a\u00020\f2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\u001cH\u0002J \u0010\'\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\u001cH\u0002J\u0018\u0010(\u001a\u00020)2\u0006\u0010\u0019\u001a\u00020\u001a2\b\b\u0002\u0010*\u001a\u00020\u001cJ\u0018\u0010(\u001a\u00020)2\u0006\u0010+\u001a\u00020,2\b\b\u0002\u0010*\u001a\u00020\u001cJ\b\u0010-\u001a\u00020.H\u0016J\u0010\u0010/\u001a\u0002002\u0006\u00101\u001a\u00020\u0018H\u0002J\u0010\u00102\u001a\u0002032\u0006\u00104\u001a\u00020\u0018H\u0002J(\u00105\u001a\u00020\u00182\u0006\u00106\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\u001c2\u0006\u0010\"\u001a\u00020\u001cH\u0002J\"\u00107\u001a\u0004\u0018\u0001082\u0006\u00106\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\u001cH\u0002J(\u00109\u001a\u00020\u00182\u0006\u0010!\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\u001c2\u0006\u0010$\u001a\u00020%H\u0002J(\u0010:\u001a\u00020\u00182\u0006\u0010!\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\u001c2\u0006\u0010$\u001a\u00020%H\u0002J\u0018\u0010;\u001a\u00020\u001c2\u0006\u0010<\u001a\u00020\f2\u0006\u0010=\u001a\u00020\u001cH\u0002J \u0010>\u001a\u00020\u00182\u0006\u00106\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\u001cH\u0002J \u0010?\u001a\u00020\u00182\u0006\u0010@\u001a\u00020\u00182\u0006\u0010A\u001a\u00020\u001c2\u0006\u0010B\u001a\u00020\u001cH\u0002J\u0010\u0010C\u001a\u00020\u001a2\u0006\u0010+\u001a\u00020,H\u0002J\u000e\u0010D\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012H\u0002J\b\u0010E\u001a\u000200H\u0002J\u0010\u0010F\u001a\u00020\u001a2\u0006\u0010!\u001a\u00020\u001aH\u0002J\u0010\u0010G\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u0018H\u0002J\u0018\u0010H\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u00182\u0006\u0010I\u001a\u00020%H\u0002J\u0010\u0010J\u001a\u00020\u00182\u0006\u0010K\u001a\u00020\u001aH\u0002J0\u0010L\u001a\u00020\u00182\u0006\u0010!\u001a\u00020\u00182\u0006\u0010M\u001a\u00020\u001c2\u0006\u0010N\u001a\u00020\u001c2\u0006\u0010O\u001a\u00020\u001c2\u0006\u0010P\u001a\u00020\u001cH\u0002J0\u0010Q\u001a\u00020\u00182\u0006\u0010!\u001a\u00020\u00182\u0006\u0010M\u001a\u00020\u001c2\u0006\u0010N\u001a\u00020\u001c2\u0006\u0010O\u001a\u00020\u001c2\u0006\u0010P\u001a\u00020\u001cH\u0002J\u0010\u0010R\u001a\u00020\u001c2\u0006\u0010S\u001a\u00020TH\u0002J\f\u0010U\u001a\u00020V*\u00020\u001aH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\n \n*\u0004\u0018\u00010\f0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\r\u001a\n \n*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0014\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0015\u001a\n \n*\u0004\u0018\u00010\f0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0016\u001a\n \n*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006Z"}, d2 = {"Lcom/symbolsense/ai/SymbolClassifier;", "Ljava/io/Closeable;", "context", "Landroid/content/Context;", "modelFileName", "", "mappingFileName", "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)V", "inputQuantization", "Lorg/tensorflow/lite/Tensor$QuantizationParams;", "kotlin.jvm.PlatformType", "inputShape", "", "inputTensor", "Lorg/tensorflow/lite/Tensor;", "interpreter", "Lorg/tensorflow/lite/Interpreter;", "labels", "", "Lcom/symbolsense/ai/SymbolLabel;", "outputQuantization", "outputShape", "outputTensor", "bitmapToGray", "", "bitmap", "Landroid/graphics/Bitmap;", "borderMedian", "", "image", "width", "height", "boxBlur", "source", "radius", "boxesForGauss", "sigma", "", "n", "centerMedian", "classify", "Lcom/symbolsense/ai/SymbolRecognitionResult;", "topK", "uri", "Landroid/net/Uri;", "close", "", "createInputBuffer", "Ljava/nio/ByteBuffer;", "pixels", "dequantizeOutput", "", "output", "dilateSquare", "mask", "findBoundingBox", "Lcom/symbolsense/ai/SymbolClassifier$BBox;", "gaussian3x3", "gaussianBlurApprox", "histogramMedian", "histogram", "count", "keepGlyphComponents", "letterbox64", "crop", "cropWidth", "cropHeight", "loadBitmap", "loadLabels", "loadModel", "makeSoftwareAndBounded", "otsuThresholdFromInk", "percentileInk", "percentile", "preprocessCameraRobust", "sourceBitmap", "resizeGrayArea", "sourceWidth", "sourceHeight", "targetWidth", "targetHeight", "resizeGrayBilinear", "u8", "value", "", "isHardwareBitmap", "", "BBox", "Companion", "Component", "app_debug"})
public final class SymbolClassifier implements java.io.Closeable {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String modelFileName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String mappingFileName = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String MODEL_FILE_NAME = "symbolsense_model_int8.tflite";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String MAPPING_FILE_NAME = "class_mapping.json";
    private static final int INPUT_SIZE = 64;
    private static final int LETTERBOX_MARGIN = 6;
    private static final int MAX_PREPROCESS_SIDE = 1400;
    private static final float MIN_CONFIDENCE = 0.6F;
    
    /**
     * DO NOT reorder this list.
     * It must stay identical to training + class_mapping.json.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> EXPECTED_ACTIVE_32 = null;
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
     * Classify a single isolated-symbol crop.
     */
    @kotlin.jvm.Synchronized()
    @org.jetbrains.annotations.NotNull()
    public final synchronized com.symbolsense.ai.SymbolRecognitionResult classify(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, int topK) {
        return null;
    }
    
    /**
     * Direct CameraX / Gallery / crop Uri entry point.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.SymbolRecognitionResult classify(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri, int topK) {
        return null;
    }
    
    /**
     * SymbolSense V4 camera-robust preprocessing.
     *
     * Mirrors Kaggle `prep_camera_np()` semantics:
     * 1. grayscale
     * 2. polarity detection
     * 3. illumination/background estimation
     * 4. illumination flattening
     * 5. light Gaussian blur
     * 6. Otsu foreground threshold
     * 7. glyph-relative connected-component cleanup
     * 8. tight bbox + slight padding
     * 9. preserve gray ink around foreground
     * 10. aspect-ratio letterbox to 64x64
     *
     * Output is canonical grayscale ByteArray:
     * white background = 255, dark symbol = near 0.
     */
    private final byte[] preprocessCameraRobust(android.graphics.Bitmap sourceBitmap) {
        return null;
    }
    
    /**
     * Ensures getPixels() is safe and bounds very large phone images.
     * V4 training preprocessing bounds max side to 1400.
     */
    private final android.graphics.Bitmap makeSoftwareAndBounded(android.graphics.Bitmap source) {
        return null;
    }
    
    private final boolean isHardwareBitmap(android.graphics.Bitmap $this$isHardwareBitmap) {
        return false;
    }
    
    /**
     * ARGB Bitmap -> uint8 grayscale, compositing transparent pixels over white.
     */
    private final byte[] bitmapToGray(android.graphics.Bitmap bitmap) {
        return null;
    }
    
    private final int borderMedian(byte[] image, int width, int height) {
        return 0;
    }
    
    private final int centerMedian(byte[] image, int width, int height) {
        return 0;
    }
    
    private final int histogramMedian(int[] histogram, int count) {
        return 0;
    }
    
    /**
     * Three box-blurs approximate a Gaussian efficiently in O(N), even for
     * the large sigma used by illumination background estimation.
     */
    private final byte[] gaussianBlurApprox(byte[] source, int width, int height, double sigma) {
        return null;
    }
    
    private final int[] boxesForGauss(double sigma, int n) {
        return null;
    }
    
    private final byte[] boxBlur(byte[] source, int width, int height, int radius) {
        return null;
    }
    
    /**
     * 3x3 separable Gaussian equivalent in intent to cv2 GaussianBlur(3x3, 0.45).
     */
    private final byte[] gaussian3x3(byte[] source, int width, int height, double sigma) {
        return null;
    }
    
    private final int otsuThresholdFromInk(byte[] image) {
        return 0;
    }
    
    /**
     * V4 connected-component cleanup.
     * The cutoff is relative to the largest glyph component, NOT image area.
     * This avoids deleting small disconnected symbol strokes on high-res photos.
     */
    private final byte[] keepGlyphComponents(byte[] mask, int width, int height) {
        return null;
    }
    
    private final com.symbolsense.ai.SymbolClassifier.BBox findBoundingBox(byte[] mask, int width, int height) {
        return null;
    }
    
    private final int percentileInk(byte[] image, double percentile) {
        return 0;
    }
    
    /**
     * Square binary dilation using two O(N) sliding-window passes.
     */
    private final byte[] dilateSquare(byte[] mask, int width, int height, int radius) {
        return null;
    }
    
    private final byte[] letterbox64(byte[] crop, int cropWidth, int cropHeight) {
        return null;
    }
    
    /**
     * Approximation of cv2.INTER_AREA for downscaling.
     * Since target is at most 52x52, direct region averaging is inexpensive.
     */
    private final byte[] resizeGrayArea(byte[] source, int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {
        return null;
    }
    
    private final byte[] resizeGrayBilinear(byte[] source, int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {
        return null;
    }
    
    /**
     * Training model receives float pixel/255.0 and the exported model is UINT8.
     * Therefore we quantize canonical grayscale using the TFLite tensor parameters.
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
    
    private final int u8(byte value) {
        return 0;
    }
    
    @java.lang.Override()
    public void close() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0007J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\tR\u0011\u0010\u000f\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\t\u00a8\u0006\u001c"}, d2 = {"Lcom/symbolsense/ai/SymbolClassifier$BBox;", "", "left", "", "top", "right", "bottom", "(IIII)V", "getBottom", "()I", "height", "getHeight", "getLeft", "getRight", "getTop", "width", "getWidth", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
    static final class BBox {
        private final int left = 0;
        private final int top = 0;
        private final int right = 0;
        private final int bottom = 0;
        
        public BBox(int left, int top, int right, int bottom) {
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
        public final com.symbolsense.ai.SymbolClassifier.BBox copy(int left, int top, int right, int bottom) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/symbolsense/ai/SymbolClassifier$Companion;", "", "()V", "EXPECTED_ACTIVE_32", "", "", "INPUT_SIZE", "", "LETTERBOX_MARGIN", "MAPPING_FILE_NAME", "MAX_PREPROCESS_SIDE", "MIN_CONFIDENCE", "", "MODEL_FILE_NAME", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\n\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\nH\u00c6\u0003J\t\u0010\u001e\u001a\u00020\nH\u00c6\u0003JY\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\nH\u00c6\u0001J\u0013\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010#\u001a\u00020\u0003H\u00d6\u0001J\t\u0010$\u001a\u00020%H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u000b\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000eR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u000e\u00a8\u0006&"}, d2 = {"Lcom/symbolsense/ai/SymbolClassifier$Component;", "", "id", "", "area", "left", "top", "right", "bottom", "cx", "", "cy", "(IIIIIIDD)V", "getArea", "()I", "getBottom", "getCx", "()D", "getCy", "getId", "getLeft", "getRight", "getTop", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
    static final class Component {
        private final int id = 0;
        private final int area = 0;
        private final int left = 0;
        private final int top = 0;
        private final int right = 0;
        private final int bottom = 0;
        private final double cx = 0.0;
        private final double cy = 0.0;
        
        public Component(int id, int area, int left, int top, int right, int bottom, double cx, double cy) {
            super();
        }
        
        public final int getId() {
            return 0;
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
        
        public final double getCx() {
            return 0.0;
        }
        
        public final double getCy() {
            return 0.0;
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
        
        public final int component6() {
            return 0;
        }
        
        public final double component7() {
            return 0.0;
        }
        
        public final double component8() {
            return 0.0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.symbolsense.ai.SymbolClassifier.Component copy(int id, int area, int left, int top, int right, int bottom, double cx, double cy) {
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