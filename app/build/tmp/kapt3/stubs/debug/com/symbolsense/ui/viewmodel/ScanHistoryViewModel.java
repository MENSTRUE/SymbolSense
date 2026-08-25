package com.symbolsense.ui.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import com.symbolsense.data.local.SymbolSenseDatabase;
import com.symbolsense.data.model.ScanResult;
import com.symbolsense.data.repository.ScanRepository;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\r\u001a\u00020\u000eJ \u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u00112\u0010\b\u0002\u0010\u0012\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\u0013J&\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\b2\u0016\b\u0002\u0010\u0016\u001a\u0010\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u000e\u0018\u00010\u0017R\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/symbolsense/ui/viewmodel/ScanHistoryViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "history", "Lkotlinx/coroutines/flow/StateFlow;", "", "Lcom/symbolsense/data/model/ScanResult;", "getHistory", "()Lkotlinx/coroutines/flow/StateFlow;", "repository", "Lcom/symbolsense/data/repository/ScanRepository;", "clearHistory", "", "deleteScan", "scanId", "", "onDeleted", "Lkotlin/Function0;", "saveScan", "result", "onSaved", "Lkotlin/Function1;", "app_debug"})
public final class ScanHistoryViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.symbolsense.data.repository.ScanRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.symbolsense.data.model.ScanResult>> history = null;
    
    public ScanHistoryViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.symbolsense.data.model.ScanResult>> getHistory() {
        return null;
    }
    
    public final void saveScan(@org.jetbrains.annotations.NotNull()
    com.symbolsense.data.model.ScanResult result, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSaved) {
    }
    
    public final void deleteScan(@org.jetbrains.annotations.NotNull()
    java.lang.String scanId, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDeleted) {
    }
    
    public final void clearHistory() {
    }
}