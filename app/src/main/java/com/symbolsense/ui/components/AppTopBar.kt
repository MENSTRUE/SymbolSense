package com.symbolsense.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class TrailingAction { SETTINGS, SEARCH, SHARE }

/**
 * Top app bar standar dipakai di hampir semua screen.
 * - [onBack] null -> tombol back disembunyikan (misal di Home)
 * - [titleBadge] dipakai untuk badge domain kecil di sebelah judul (Screen 9 - Result Editor)
 * - [trailingIcon] aksi kanan (settings/search/share)
 * - [onTrailingClick] callback aksi kanan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    trailingIcon: TrailingAction? = null,
    onTrailingClick: (() -> Unit)? = null,
    titleBadge: String? = null,
    titleBadgeColor: Color = MaterialTheme.colorScheme.primary,
    titleBadgeBg: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
) {
    TopAppBar(
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, style = MaterialTheme.typography.titleLarge)
                    if (titleBadge != null) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = titleBadgeBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = titleBadge,
                                style = MaterialTheme.typography.labelMedium,
                                color = titleBadgeColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                }
            }
        },
        actions = {
            if (trailingIcon != null) {
                IconButton(onClick = { onTrailingClick?.invoke() }) {
                    when (trailingIcon) {
                        TrailingAction.SETTINGS -> Icon(Icons.Filled.Settings, contentDescription = "Setelan")
                        TrailingAction.SEARCH -> Icon(Icons.Filled.Search, contentDescription = "Cari")
                        TrailingAction.SHARE -> Icon(Icons.Filled.Share, contentDescription = "Bagikan")
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}
