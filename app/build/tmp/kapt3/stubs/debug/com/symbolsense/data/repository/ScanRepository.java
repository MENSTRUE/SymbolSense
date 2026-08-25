package com.symbolsense.data.repository;

import com.symbolsense.data.local.SymbolSenseDatabase;
import com.symbolsense.data.local.entity.DetectedSymbolEntity;
import com.symbolsense.data.local.entity.ScanEntity;
import com.symbolsense.data.local.entity.ScanWithSymbols;
import com.symbolsense.data.model.DetectedSymbol;
import com.symbolsense.data.model.RelativeBoundingBox;
import com.symbolsense.data.model.ScanResult;
import com.symbolsense.data.model.SymbolDomain;
import java.util.UUID;
import kotlinx.coroutines.flow.Flow;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\rJ\u0012\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u00100\u000fJ\u0016\u0010\u0012\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\u000f2\u0006\u0010\u000b\u001a\u00020\fJ\u0016\u0010\u0013\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0015R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/symbolsense/data/repository/ScanRepository;", "", "database", "Lcom/symbolsense/data/local/SymbolSenseDatabase;", "(Lcom/symbolsense/data/local/SymbolSenseDatabase;)V", "scanDao", "Lcom/symbolsense/data/local/dao/ScanDao;", "clearHistory", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteScan", "scanId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observeHistory", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/symbolsense/data/model/ScanResult;", "observeScan", "saveScan", "result", "(Lcom/symbolsense/data/model/ScanResult;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class ScanRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.symbolsense.data.local.SymbolSenseDatabase database = null;
    @org.jetbrains.annotations.NotNull()
    private final com.symbolsense.data.local.dao.ScanDao scanDao = null;
    
    public ScanRepository(@org.jetbrains.annotations.NotNull()
    com.symbolsense.data.local.SymbolSenseDatabase database) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.symbolsense.data.model.ScanResult>> observeHistory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.symbolsense.data.model.ScanResult> observeScan(@org.jetbrains.annotations.NotNull()
    java.lang.String scanId) {
        return null;
    }
    
    /**
     * Menyimpan hasil scan ke Room.
     * Draft dari pipeline UI diberi UUID baru agar scan berulang tidak menimpa riwayat lama.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveScan(@org.jetbrains.annotations.NotNull()
    com.symbolsense.data.model.ScanResult result, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteScan(@org.jetbrains.annotations.NotNull()
    java.lang.String scanId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearHistory(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}