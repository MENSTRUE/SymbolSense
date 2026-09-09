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

/**
 * SymbolSense multi-symbol detector runtime V4 — CROHME2019 Detector V2.
 *
 * Main deployment contract:
 * - Uses CROHME2019 Detector V2 FP16 (FLOAT32 I/O).
 * - Decoder mirrors Kaggle evaluation: calibrated threshold 0.35, 3x3 local peaks,
 *  size decode, NMS IoU 0.35, up to 128 detections.
 * - Keeps formula ROI extraction so a phone screenshot/photo can be cropped to the
 *  writing region, but DOES NOT binarize/canonicalize the detector input anymore.
 *  Detector V2 was trained with grayscale camera/screen backgrounds, polarity,
 *  moire, blur, lighting, JPEG and hard negatives; feeding raw grayscale is the
 *  closest Android match to that training domain.
 * - The exact-32 isolated SymbolClassifier remains a separate model.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\bJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J;\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010 \u001a\u00020!H\u00d6\u0001J\t\u0010\"\u001a\u00020#H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u000b\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\nR\u0011\u0010\r\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u000e\u0010\nR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\nR\u0011\u0010\u0010\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\nR\u0011\u0010\u0015\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u0016\u0010\n\u00a8\u0006$"}, d2 = {"Lcom/symbolsense/ai/DetectorBox;", "", "leftPx", "", "topPx", "rightPx", "bottomPx", "confidence", "(FFFFF)V", "getBottomPx", "()F", "centerX", "getCenterX", "centerY", "getCenterY", "getConfidence", "height", "getHeight", "getLeftPx", "getRightPx", "getTopPx", "width", "getWidth", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
public final class DetectorBox {
    private final float leftPx = 0.0F;
    private final float topPx = 0.0F;
    private final float rightPx = 0.0F;
    private final float bottomPx = 0.0F;
    private final float confidence = 0.0F;
    
    public DetectorBox(float leftPx, float topPx, float rightPx, float bottomPx, float confidence) {
        super();
    }
    
    public final float getLeftPx() {
        return 0.0F;
    }
    
    public final float getTopPx() {
        return 0.0F;
    }
    
    public final float getRightPx() {
        return 0.0F;
    }
    
    public final float getBottomPx() {
        return 0.0F;
    }
    
    public final float getConfidence() {
        return 0.0F;
    }
    
    public final float getWidth() {
        return 0.0F;
    }
    
    public final float getHeight() {
        return 0.0F;
    }
    
    public final float getCenterX() {
        return 0.0F;
    }
    
    public final float getCenterY() {
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
    public final com.symbolsense.ai.DetectorBox copy(float leftPx, float topPx, float rightPx, float bottomPx, float confidence) {
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