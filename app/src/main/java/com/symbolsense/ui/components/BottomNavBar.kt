package com.symbolsense.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class BottomNavTab(val label: String) {
    HOME("Home"),
    HISTORY("Riwayat"),
    LIBRARY("Pustaka"),
    SETTINGS("Setelan")
}

/**
 * Bottom nav yang SELALU muncul di Home, History, Library, Settings.
 * FAB kamera di tengah.
 */
@Composable
fun SymbolSenseBottomBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    onCameraClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            NavigationBarItem(
                selected = selectedTab == BottomNavTab.HOME,
                onClick = { onTabSelected(BottomNavTab.HOME) },
                icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                label = { Text("Home") },
                colors = navColors()
            )
            NavigationBarItem(
                selected = selectedTab == BottomNavTab.HISTORY,
                onClick = { onTabSelected(BottomNavTab.HISTORY) },
                icon = { Icon(Icons.Filled.Schedule, contentDescription = null) },
                label = { Text("Riwayat") },
                colors = navColors()
            )
            // Spacer slot kosong untuk FAB kamera
            NavigationBarItem(
                selected = false,
                onClick = {},
                enabled = false,
                icon = { Icon(Icons.Filled.PhotoCamera, contentDescription = null, tint = Color.Transparent) },
                label = { Text("") },
                colors = navColors()
            )
            NavigationBarItem(
                selected = selectedTab == BottomNavTab.LIBRARY,
                onClick = { onTabSelected(BottomNavTab.LIBRARY) },
                icon = { Icon(Icons.Filled.MenuBook, contentDescription = null) },
                label = { Text("Pustaka") },
                colors = navColors()
            )
            NavigationBarItem(
                selected = selectedTab == BottomNavTab.SETTINGS,
                onClick = { onTabSelected(BottomNavTab.SETTINGS) },
                icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                label = { Text("Setelan") },
                colors = navColors()
            )
        }

        // FAB kamera mengambang di tengah
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondary,
            shadowElevation = 6.dp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-24).dp)
                .size(56.dp),
            onClick = onCameraClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.PhotoCamera,
                    contentDescription = "Scan",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
)