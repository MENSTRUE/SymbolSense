package com.symbolsense.ui.screens.symboldetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.symbolsense.data.model.SymbolEntry
import com.symbolsense.ui.components.AppTopBar
import com.symbolsense.ui.theme.CyanAccent
import com.symbolsense.ui.theme.IndigoPrimary
import androidx.compose.foundation.layout.Row
/**
 * Screen 14/15 — Symbol Detail.
 *
 * [onSpeak] dipanggil saat tombol speaker ditekan — hubungkan ke
 * Android TextToSpeech untuk membacakan [SymbolEntry.description] (fitur aksesibilitas).
 */
@Composable
fun SymbolDetailScreen(
    entry: SymbolEntry,
    onBack: () -> Unit,
    onSpeak: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(title = entry.name, onBack = onBack)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.linearGradient(listOf(IndigoPrimary, CyanAccent)),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(entry.glyph, color = Color.White, fontSize = 64.sp)
                }

                Spacer(Modifier.size(20.dp))

                SectionTitle("Deskripsi")
                Text(entry.description, style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.size(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    SectionTitle("Notasi", modifier = Modifier.weight(1f))
                    IconButton(onClick = onSpeak) {
                        Icon(Icons.Filled.VolumeUp, contentDescription = "Bacakan deskripsi")
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "LaTeX: ${entry.notationLatex}",
                            color = MaterialTheme.colorScheme.surface,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Unicode: ${entry.notationUnicode}",
                            color = MaterialTheme.colorScheme.surface,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "ASCII aprox: ${entry.notationAscii}",
                            color = MaterialTheme.colorScheme.surface,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(Modifier.size(20.dp))

                SectionTitle("Contoh Penggunaan")
                Spacer(Modifier.size(8.dp))
            }

            items(entry.usageExamples) { example ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(example.expression, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            example.caption,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(Modifier.size(16.dp)) }
        }
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = modifier)
}
