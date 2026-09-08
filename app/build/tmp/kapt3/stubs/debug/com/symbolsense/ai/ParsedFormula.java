package com.symbolsense.ai;

/**
 * Spatial parser V1.
 *
 * Scope deliberately kept conservative:
 * - sorts detected symbols into visual rows
 * - sorts each row left -> right
 * - creates readable display text
 * - creates valid-enough linear LaTeX for supported 32 classes
 *
 * It is NOT yet a full 2D math parser for fractions, roots with radicands,
 * superscript/subscript trees, integral limits, or matrices.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\bJ\u000f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0006H\u00c6\u0003J-\u0010\u0011\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0006H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u0018"}, d2 = {"Lcom/symbolsense/ai/ParsedFormula;", "", "orderedSymbols", "", "Lcom/symbolsense/ai/RecognizedSymbol;", "display", "", "latex", "(Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)V", "getDisplay", "()Ljava/lang/String;", "getLatex", "getOrderedSymbols", "()Ljava/util/List;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class ParsedFormula {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.symbolsense.ai.RecognizedSymbol> orderedSymbols = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String display = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String latex = null;
    
    public ParsedFormula(@org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.ai.RecognizedSymbol> orderedSymbols, @org.jetbrains.annotations.NotNull()
    java.lang.String display, @org.jetbrains.annotations.NotNull()
    java.lang.String latex) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.symbolsense.ai.RecognizedSymbol> getOrderedSymbols() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplay() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLatex() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.symbolsense.ai.RecognizedSymbol> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.ai.ParsedFormula copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.ai.RecognizedSymbol> orderedSymbols, @org.jetbrains.annotations.NotNull()
    java.lang.String display, @org.jetbrains.annotations.NotNull()
    java.lang.String latex) {
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