package com.symbolsense.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import com.symbolsense.data.local.entity.DetectedSymbolEntity;
import com.symbolsense.data.local.entity.ScanEntity;
import com.symbolsense.data.local.entity.ScanWithSymbols;
import kotlinx.coroutines.flow.Flow;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f0\u000bH\'J\u0018\u0010\u000e\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\u000b2\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u0016\u0010\u000f\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u001c\u0010\u0013\u001a\u00020\u00032\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\fH\u00a7@\u00a2\u0006\u0002\u0010\u0016\u00a8\u0006\u0017"}, d2 = {"Lcom/symbolsense/data/local/dao/ScanDao;", "", "clearAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteScanById", "scanId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteSymbolsForScan", "observeAll", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/symbolsense/data/local/entity/ScanWithSymbols;", "observeById", "upsertScan", "scan", "Lcom/symbolsense/data/local/entity/ScanEntity;", "(Lcom/symbolsense/data/local/entity/ScanEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "upsertSymbols", "symbols", "Lcom/symbolsense/data/local/entity/DetectedSymbolEntity;", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface ScanDao {
    
    @androidx.room.Transaction()
    @androidx.room.Query(value = "SELECT * FROM scan_history ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.symbolsense.data.local.entity.ScanWithSymbols>> observeAll();
    
    @androidx.room.Transaction()
    @androidx.room.Query(value = "SELECT * FROM scan_history WHERE id = :scanId LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.symbolsense.data.local.entity.ScanWithSymbols> observeById(@org.jetbrains.annotations.NotNull()
    java.lang.String scanId);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object upsertScan(@org.jetbrains.annotations.NotNull()
    com.symbolsense.data.local.entity.ScanEntity scan, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object upsertSymbols(@org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.data.local.entity.DetectedSymbolEntity> symbols, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM detected_symbol WHERE scanId = :scanId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteSymbolsForScan(@org.jetbrains.annotations.NotNull()
    java.lang.String scanId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM scan_history WHERE id = :scanId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteScanById(@org.jetbrains.annotations.NotNull()
    java.lang.String scanId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM scan_history")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}