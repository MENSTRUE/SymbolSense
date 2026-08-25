package com.symbolsense.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.symbolsense.ui.theme.BorderLight
import com.symbolsense.ui.theme.CyanAccent
import com.symbolsense.ui.theme.IndigoPrimary
import com.symbolsense.ui.theme.SurfaceRaisedLight
import com.symbolsense.ui.theme.SymbolMono
import com.symbolsense.ui.theme.TextSecondaryLight

private data class Slide(val title: String, val body: String, val type: Int)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val slides = listOf(
        Slide("Pindai simbol apa pun", "Arahkan kamera ke simbol matematika, kimia, atau elektronika, atau pilih gambar dari galeri.", 0),
        Slide("Lihat apa yang ditemukan", "Setiap simbol menampilkan nama, domain, dan tingkat kepercayaan. Deteksi yang meragukan ditandai untuk ditinjau.", 1),
        Slide("Edit dan ekspor", "Tinjau hasil terstruktur dalam LaTeX, SMILES, Netlist, atau teks biasa lalu simpan dan ekspor.", 2)
    )
    var index by remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.End) {
            Text("Lewati", modifier = Modifier.clickable(onClick = onFinish).padding(8.dp), style = MaterialTheme.typography.bodyMedium, color = TextSecondaryLight)
        }
        AnimatedContent(targetState = index, transitionSpec = { fadeIn() togetherWith fadeOut() }, modifier = Modifier.weight(1f), label = "onboarding") { i ->
            val slide = slides[i]
            Column(Modifier.fillMaxSize().padding(horizontal = 24.dp), verticalArrangement = Arrangement.Center) {
                OnboardingVisual(slide.type)
                Spacer(Modifier.height(30.dp))
                Text(slide.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(slide.body, style = MaterialTheme.typography.bodyMedium, color = TextSecondaryLight)
            }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                slides.indices.forEach { i -> Box(Modifier.size(width = if (i == index) 20.dp else 6.dp, height = 6.dp).background(if (i == index) IndigoPrimary else BorderLight, androidx.compose.foundation.shape.CircleShape).clickable { index = i }) }
            }
            Button(onClick = { if (index == slides.lastIndex) onFinish() else index++ }, shape = MaterialTheme.shapes.medium, colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary), elevation = ButtonDefaults.buttonElevation(0.dp)) {
                Text(if (index == slides.lastIndex) "Mulai" else "Lanjut")
            }
        }
    }
}

@Composable
private fun OnboardingVisual(type: Int) {
    Box(
        Modifier.fillMaxWidth().height(150.dp).background(SurfaceRaisedLight, MaterialTheme.shapes.medium).border(1.dp, BorderLight, MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        when (type) {
            0 -> {
                Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) { listOf("∫", "Σ", "√").forEach { Text(it, fontFamily = SymbolMono, style = MaterialTheme.typography.titleLarge) } }
                Canvas(Modifier.fillMaxSize().padding(12.dp)) {
                    val l = 18.dp.toPx(); val w = 2.dp.toPx()
                    fun c(x:Float,y:Float,sx:Float,sy:Float){ drawLine(CyanAccent,Offset(x,y),Offset(x+sx*l,y),w); drawLine(CyanAccent,Offset(x,y),Offset(x,y+sy*l),w) }
                    c(0f,0f,1f,1f); c(size.width,0f,-1f,1f); c(0f,size.height,1f,-1f); c(size.width,size.height,-1f,-1f)
                }
            }
            1 -> Canvas(Modifier.fillMaxSize().padding(20.dp)) {
                drawRect(CyanAccent, Offset(size.width*.08f,size.height*.18f), Size(size.width*.24f,size.height*.34f), style=Stroke(2.dp.toPx()))
                drawRect(CyanAccent, Offset(size.width*.40f,size.height*.26f), Size(size.width*.20f,size.height*.22f), style=Stroke(2.dp.toPx()))
                drawRect(com.symbolsense.ui.theme.AmberWarning, Offset(size.width*.67f,size.height*.32f), Size(size.width*.20f,size.height*.20f), style=Stroke(2.dp.toPx()))
            }
            else -> Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("∫ x² dx = x³/3 + C", fontFamily = SymbolMono, style = MaterialTheme.typography.titleMedium); Spacer(Modifier.size(14.dp)); Text("LaTeX  ·  PDF  ·  DOCX", style = MaterialTheme.typography.labelMedium, color = TextSecondaryLight) }
        }
    }
}
