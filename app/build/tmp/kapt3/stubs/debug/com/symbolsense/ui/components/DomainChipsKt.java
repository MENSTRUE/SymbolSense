package com.symbolsense.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.ui.Modifier;
import com.symbolsense.data.model.SymbolDomain;
import com.symbolsense.ui.theme.DomainColors;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000,\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0007\u001aB\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000b2\b\b\u0002\u0010\f\u001a\u00020\rH\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u000e\u0010\u000f\u001a\u000e\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u0002\u001a\u00020\u0003\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0011"}, d2 = {"DomainBadge", "", "domain", "Lcom/symbolsense/data/model/SymbolDomain;", "DomainChip", "label", "", "icon", "selected", "", "onClick", "Lkotlin/Function0;", "accentColor", "Landroidx/compose/ui/graphics/Color;", "DomainChip-xwkQ0AY", "(Ljava/lang/String;Ljava/lang/String;ZLkotlin/jvm/functions/Function0;J)V", "domainIcon", "app_debug"})
public final class DomainChipsKt {
    
    /**
     * Badge kecil berwarna sesuai domain, dipakai di History list & Result Editor.
     */
    @androidx.compose.runtime.Composable()
    public static final void DomainBadge(@org.jetbrains.annotations.NotNull()
    com.symbolsense.data.model.SymbolDomain domain) {
    }
    
    /**
     * Mengembalikan glyph ikon sederhana untuk tiap domain (dipakai pada chip & badge thumbnail).
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String domainIcon(@org.jetbrains.annotations.NotNull()
    com.symbolsense.data.model.SymbolDomain domain) {
        return null;
    }
}