package com.symbolsense.data.model;

/**
 * Hasil akhir satu sesi scan.
 *
 * createdAtMillis dipakai Room agar riwayat dapat diurutkan secara benar dan
 * tetap konsisten setelah aplikasi ditutup/dibuka kembali.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BY\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0010J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0005H\u00c6\u0003J\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0003H\u00c6\u0003J\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010$\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u00c6\u0003J\t\u0010%\u001a\u00020\u000eH\u00c6\u0003J\u000b\u0010&\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003Jk\u0010\'\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010(\u001a\u00020)2\b\u0010*\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010+\u001a\u00020,H\u00d6\u0001J\t\u0010-\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0018R\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0018R\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0018R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0018R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0018\u00a8\u0006."}, d2 = {"Lcom/symbolsense/data/model/ScanResult;", "", "id", "", "domain", "Lcom/symbolsense/data/model/SymbolDomain;", "timestampLabel", "rawPreviewText", "structuredOutput", "latexOrCode", "detectedSymbols", "", "Lcom/symbolsense/data/model/DetectedSymbol;", "createdAtMillis", "", "imageUri", "(Ljava/lang/String;Lcom/symbolsense/data/model/SymbolDomain;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;JLjava/lang/String;)V", "getCreatedAtMillis", "()J", "getDetectedSymbols", "()Ljava/util/List;", "getDomain", "()Lcom/symbolsense/data/model/SymbolDomain;", "getId", "()Ljava/lang/String;", "getImageUri", "getLatexOrCode", "getRawPreviewText", "getStructuredOutput", "getTimestampLabel", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class ScanResult {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final com.symbolsense.data.model.SymbolDomain domain = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String timestampLabel = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String rawPreviewText = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String structuredOutput = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String latexOrCode = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.symbolsense.data.model.DetectedSymbol> detectedSymbols = null;
    private final long createdAtMillis = 0L;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String imageUri = null;
    
    public ScanResult(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    com.symbolsense.data.model.SymbolDomain domain, @org.jetbrains.annotations.NotNull()
    java.lang.String timestampLabel, @org.jetbrains.annotations.NotNull()
    java.lang.String rawPreviewText, @org.jetbrains.annotations.NotNull()
    java.lang.String structuredOutput, @org.jetbrains.annotations.NotNull()
    java.lang.String latexOrCode, @org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.data.model.DetectedSymbol> detectedSymbols, long createdAtMillis, @org.jetbrains.annotations.Nullable()
    java.lang.String imageUri) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.data.model.SymbolDomain getDomain() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTimestampLabel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRawPreviewText() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getStructuredOutput() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLatexOrCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.symbolsense.data.model.DetectedSymbol> getDetectedSymbols() {
        return null;
    }
    
    public final long getCreatedAtMillis() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getImageUri() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.data.model.SymbolDomain component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.symbolsense.data.model.DetectedSymbol> component7() {
        return null;
    }
    
    public final long component8() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.symbolsense.data.model.ScanResult copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    com.symbolsense.data.model.SymbolDomain domain, @org.jetbrains.annotations.NotNull()
    java.lang.String timestampLabel, @org.jetbrains.annotations.NotNull()
    java.lang.String rawPreviewText, @org.jetbrains.annotations.NotNull()
    java.lang.String structuredOutput, @org.jetbrains.annotations.NotNull()
    java.lang.String latexOrCode, @org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.data.model.DetectedSymbol> detectedSymbols, long createdAtMillis, @org.jetbrains.annotations.Nullable()
    java.lang.String imageUri) {
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