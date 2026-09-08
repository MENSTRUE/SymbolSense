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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/symbolsense/ai/RecognitionMode;", "", "(Ljava/lang/String;I)V", "ISOLATED", "MULTI_SYMBOL", "DETECTOR_FALLBACK", "app_debug"})
public enum RecognitionMode {
    /*public static final*/ ISOLATED /* = new ISOLATED() */,
    /*public static final*/ MULTI_SYMBOL /* = new MULTI_SYMBOL() */,
    /*public static final*/ DETECTOR_FALLBACK /* = new DETECTOR_FALLBACK() */;
    
    RecognitionMode() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.symbolsense.ai.RecognitionMode> getEntries() {
        return null;
    }
}