package com.symbolsense.ui.screens.preview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.material.icons.Icons;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.drawscope.Stroke;
import androidx.compose.ui.layout.ContentScale;
import java.io.File;
import java.io.FileOutputStream;
import kotlinx.coroutines.Dispatchers;
import android.graphics.Canvas;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\f\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\f\u00a8\u0006\r"}, d2 = {"Lcom/symbolsense/ui/screens/preview/DragTarget;", "", "(Ljava/lang/String;I)V", "TOP_LEFT", "TOP_RIGHT", "BOTTOM_LEFT", "BOTTOM_RIGHT", "LEFT", "RIGHT", "TOP", "BOTTOM", "WHOLE", "NONE", "app_debug"})
enum DragTarget {
    /*public static final*/ TOP_LEFT /* = new TOP_LEFT() */,
    /*public static final*/ TOP_RIGHT /* = new TOP_RIGHT() */,
    /*public static final*/ BOTTOM_LEFT /* = new BOTTOM_LEFT() */,
    /*public static final*/ BOTTOM_RIGHT /* = new BOTTOM_RIGHT() */,
    /*public static final*/ LEFT /* = new LEFT() */,
    /*public static final*/ RIGHT /* = new RIGHT() */,
    /*public static final*/ TOP /* = new TOP() */,
    /*public static final*/ BOTTOM /* = new BOTTOM() */,
    /*public static final*/ WHOLE /* = new WHOLE() */,
    /*public static final*/ NONE /* = new NONE() */;
    
    DragTarget() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.symbolsense.ui.screens.preview.DragTarget> getEntries() {
        return null;
    }
}