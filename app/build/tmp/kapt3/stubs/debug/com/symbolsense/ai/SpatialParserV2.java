package com.symbolsense.ai;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010$\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u001aB\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\t\u001a\u00020\n2\n\u0010\u000b\u001a\u00060\fj\u0002`\r2\u0006\u0010\u000e\u001a\u00020\u0005H\u0002J\u0016\u0010\u000f\u001a\u00020\u00052\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002J\u0016\u0010\u0013\u001a\u00020\u00052\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002J\u0016\u0010\u0014\u001a\u00020\u00152\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00150\u0011H\u0002J\u0014\u0010\u0017\u001a\u00020\u00182\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011J\u0016\u0010\u0019\u001a\u00020\u00052\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lcom/symbolsense/ai/SpatialParserV2;", "", "()V", "invalidSuperscriptBases", "", "", "operatorNames", "superscriptDisplay", "", "appendSpaced", "", "out", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "token", "buildLinearDisplay", "symbols", "", "Lcom/symbolsense/ai/RecognizedSymbol;", "buildLinearLatex", "median", "", "values", "parse", "Lcom/symbolsense/ai/ParsedFormulaV2;", "renderSuperscriptDisplay", "Positioned", "app_debug"})
public final class SpatialParserV2 {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> operatorNames = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> invalidSuperscriptBases = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.lang.String> superscriptDisplay = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.symbolsense.ai.SpatialParserV2 INSTANCE = null;
    
    private SpatialParserV2() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.ParsedFormulaV2 parse(@org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.ai.RecognizedSymbol> symbols) {
        return null;
    }
    
    private final java.lang.String renderSuperscriptDisplay(java.util.List<com.symbolsense.ai.RecognizedSymbol> symbols) {
        return null;
    }
    
    private final void appendSpaced(java.lang.StringBuilder out, java.lang.String token) {
    }
    
    private final java.lang.String buildLinearDisplay(java.util.List<com.symbolsense.ai.RecognizedSymbol> symbols) {
        return null;
    }
    
    private final java.lang.String buildLinearLatex(java.util.List<com.symbolsense.ai.RecognizedSymbol> symbols) {
        return null;
    }
    
    private final float median(java.util.List<java.lang.Float> values) {
        return 0.0F;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0005H\u00c6\u0003J;\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001J\t\u0010\u001c\u001a\u00020\u001dH\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000b\u00a8\u0006\u001e"}, d2 = {"Lcom/symbolsense/ai/SpatialParserV2$Positioned;", "", "symbol", "Lcom/symbolsense/ai/RecognizedSymbol;", "centerX", "", "centerY", "width", "height", "(Lcom/symbolsense/ai/RecognizedSymbol;FFFF)V", "getCenterX", "()F", "getCenterY", "getHeight", "getSymbol", "()Lcom/symbolsense/ai/RecognizedSymbol;", "getWidth", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
    static final class Positioned {
        @org.jetbrains.annotations.NotNull()
        private final com.symbolsense.ai.RecognizedSymbol symbol = null;
        private final float centerX = 0.0F;
        private final float centerY = 0.0F;
        private final float width = 0.0F;
        private final float height = 0.0F;
        
        public Positioned(@org.jetbrains.annotations.NotNull()
        com.symbolsense.ai.RecognizedSymbol symbol, float centerX, float centerY, float width, float height) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.symbolsense.ai.RecognizedSymbol getSymbol() {
            return null;
        }
        
        public final float getCenterX() {
            return 0.0F;
        }
        
        public final float getCenterY() {
            return 0.0F;
        }
        
        public final float getWidth() {
            return 0.0F;
        }
        
        public final float getHeight() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.symbolsense.ai.RecognizedSymbol component1() {
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
        
        public final float component5() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.symbolsense.ai.SpatialParserV2.Positioned copy(@org.jetbrains.annotations.NotNull()
        com.symbolsense.ai.RecognizedSymbol symbol, float centerX, float centerY, float width, float height) {
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