package com.symbolsense.ui.components;

import android.app.Activity;
import androidx.core.view.WindowCompat;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import com.symbolsense.data.model.SymbolDomain;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000H\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007\u001a$\u0010\u000b\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0007\u001aI\u0010\u0012\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\r2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\b0\u00152\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0016\u001a\u00020\u00172\u0015\b\u0002\u0010\u0018\u001a\u000f\u0012\u0004\u0012\u00020\b\u0018\u00010\u0015\u00a2\u0006\u0002\b\u0019H\u0007\u001a\u0012\u0010\u001a\u001a\u00020\b2\b\b\u0002\u0010\u001b\u001a\u00020\u0011H\u0007\u001a(\u0010\u001c\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\r2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\b0\u00152\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0007\u001a\u001a\u0010\u001d\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0007\u001a\u0010\u0010\u001e\u001a\u00020\b2\u0006\u0010\u001f\u001a\u00020\u0017H\u0007\u001a\u000e\u0010 \u001a\u00020\r2\u0006\u0010!\u001a\u00020\"\u001a\u000e\u0010#\u001a\u00020\r2\u0006\u0010!\u001a\u00020\"\"\u0013\u0010\u0000\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0002\u0010\u0003\"\u0013\u0010\u0005\u001a\u00020\u0001\u00a2\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0006\u0010\u0003\u00a8\u0006$"}, d2 = {"PagePadding", "Landroidx/compose/ui/unit/Dp;", "getPagePadding", "()F", "F", "StandardRadius", "getStandardRadius", "ConfidenceText", "", "value", "", "GlyphTile", "glyph", "", "modifier", "Landroidx/compose/ui/Modifier;", "size", "", "PrimaryActionButton", "text", "onClick", "Lkotlin/Function0;", "enabled", "", "leading", "Landroidx/compose/runtime/Composable;", "SDivider", "indent", "SecondaryActionButton", "SectionLabel", "SystemBarsForScreen", "darkIcons", "domainCodeLabel", "domain", "Lcom/symbolsense/data/model/SymbolDomain;", "domainGlyph", "app_debug"})
public final class DesignSystemKt {
    private static final float PagePadding = 0.0F;
    private static final float StandardRadius = 0.0F;
    
    public static final float getPagePadding() {
        return 0.0F;
    }
    
    public static final float getStandardRadius() {
        return 0.0F;
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SDivider(int indent) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SectionLabel(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void PrimaryActionButton(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, boolean enabled, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> leading) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SecondaryActionButton(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void GlyphTile(@org.jetbrains.annotations.NotNull()
    java.lang.String glyph, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier, int size) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ConfidenceText(float value) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String domainCodeLabel(@org.jetbrains.annotations.NotNull()
    com.symbolsense.data.model.SymbolDomain domain) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String domainGlyph(@org.jetbrains.annotations.NotNull()
    com.symbolsense.data.model.SymbolDomain domain) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SystemBarsForScreen(boolean darkIcons) {
    }
}