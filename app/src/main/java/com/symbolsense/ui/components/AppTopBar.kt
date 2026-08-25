package com.symbolsense.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.ui.theme.BorderLight
import com.symbolsense.ui.theme.IndigoPrimary
import com.symbolsense.ui.theme.IndigoPrimaryContainer
import com.symbolsense.ui.theme.TextSecondaryLight

enum class TrailingAction { SETTINGS, SEARCH, SHARE }

@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    trailingIcon: TrailingAction? = null,
    onTrailingClick: (() -> Unit)? = null,
    titleBadge: String? = null,
    titleBadgeColor: Color = IndigoPrimary,
    titleBadgeBg: Color = IndigoPrimaryContainer
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxWidth().statusBarsPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                } else {
                    Spacer(Modifier.width(48.dp))
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        if (titleBadge != null) {
                            Spacer(Modifier.width(8.dp))
                            Surface(color = titleBadgeBg, shape = MaterialTheme.shapes.extraLarge) {
                                Text(
                                    titleBadge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = titleBadgeColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    if (subtitle != null) {
                        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                    }
                }

                if (trailingIcon != null) {
                    IconButton(onClick = { onTrailingClick?.invoke() }) {
                        when (trailingIcon) {
                            TrailingAction.SETTINGS -> Icon(Icons.Filled.Settings, contentDescription = "Pengaturan")
                            TrailingAction.SEARCH -> Icon(Icons.Filled.Search, contentDescription = "Cari")
                            TrailingAction.SHARE -> Icon(Icons.Filled.Share, contentDescription = "Bagikan")
                        }
                    }
                } else {
                    Spacer(Modifier.width(48.dp))
                }
            }
            HorizontalDivider(color = BorderLight)
        }
    }
}
