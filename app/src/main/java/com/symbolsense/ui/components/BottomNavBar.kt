package com.symbolsense.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.symbolsense.ui.theme.IndigoPrimary
import com.symbolsense.ui.theme.IndigoPrimaryContainer
import com.symbolsense.ui.theme.TextTertiaryLight

/** Bottom navigation V3: Beranda · Pindai · Pustaka · Riwayat. */
enum class BottomNavTab(val label: String) {
    HOME("Beranda"),
    SCAN("Pindai"),
    LIBRARY("Pustaka"),
    HISTORY("Riwayat")
}

@Composable
fun SymbolSenseBottomBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            Triple(BottomNavTab.HOME, Icons.Filled.Home, "Beranda"),
            Triple(BottomNavTab.SCAN, Icons.Filled.CenterFocusStrong, "Pindai"),
            Triple(BottomNavTab.LIBRARY, Icons.Filled.MenuBook, "Pustaka"),
            Triple(BottomNavTab.HISTORY, Icons.Filled.History, "Riwayat")
        )
        items.forEach { (tab, icon, label) ->
            val selected = selectedTab == tab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IndigoPrimary,
                    selectedTextColor = IndigoPrimary,
                    unselectedIconColor = TextTertiaryLight,
                    unselectedTextColor = TextTertiaryLight,
                    indicatorColor = if (selected) IndigoPrimaryContainer else Color.Transparent
                )
            )
        }
    }
}
