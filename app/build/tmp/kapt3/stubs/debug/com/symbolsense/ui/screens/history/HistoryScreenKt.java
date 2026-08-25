package com.symbolsense.ui.screens.history;

import androidx.compose.material.icons.Icons;
import androidx.compose.material3.OutlinedTextFieldDefaults;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import com.symbolsense.data.model.ScanResult;
import com.symbolsense.data.model.SymbolDomain;
import com.symbolsense.ui.components.BottomNavTab;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0003H\u0003\u001a$\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\tH\u0003\u001aF\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00070\u000f2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u00010\t2\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\tH\u0007\u00a8\u0006\u0011"}, d2 = {"EmptyHistoryState", "", "hasAnyHistory", "", "hasQuery", "HistoryRow", "item", "Lcom/symbolsense/data/model/ScanResult;", "onOpenDetail", "Lkotlin/Function1;", "", "HistoryScreen", "selectedTab", "Lcom/symbolsense/ui/components/BottomNavTab;", "historyItems", "", "onTabSelected", "app_debug"})
public final class HistoryScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void HistoryScreen(@org.jetbrains.annotations.NotNull()
    com.symbolsense.ui.components.BottomNavTab selectedTab, @org.jetbrains.annotations.NotNull()
    java.util.List<com.symbolsense.data.model.ScanResult> historyItems, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.symbolsense.ui.components.BottomNavTab, kotlin.Unit> onTabSelected, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenDetail) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void EmptyHistoryState(boolean hasAnyHistory, boolean hasQuery) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void HistoryRow(com.symbolsense.data.model.ScanResult item, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenDetail) {
    }
}