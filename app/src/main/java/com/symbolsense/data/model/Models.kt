package com.symbolsense.data.model

/** Domain klasifikasi utama yang dikenali aplikasi. */
enum class SymbolDomain(val label: String) {
    AUTO("Otomatis"),
    MATH("Matematika"),
    CHEMISTRY("Kimia"),
    ELECTRONICS("Elektronika"),
    GENERAL("Umum")
}

/** Hasil deteksi satu simbol pada gambar (bounding box + label + confidence). */
data class DetectedSymbol(
    val id: String,
    val label: String,
    val displayGlyph: String,
    val confidence: Float,
    /** Posisi relatif bounding box (0f..1f) terhadap gambar, untuk overlay. */
    val boundingBox: RelativeBoundingBox
)

data class RelativeBoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

/**
 * Hasil akhir satu sesi scan.
 *
 * createdAtMillis dipakai Room agar riwayat dapat diurutkan secara benar dan
 * tetap konsisten setelah aplikasi ditutup/dibuka kembali.
 */
data class ScanResult(
    val id: String,
    val domain: SymbolDomain,
    val timestampLabel: String,
    val rawPreviewText: String,
    val structuredOutput: String,
    val latexOrCode: String,
    val detectedSymbols: List<DetectedSymbol>,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val imageUri: String? = null
)

/** Entri referensi simbol untuk Pustaka Simbol. */
data class SymbolEntry(
    val id: String,
    val name: String,
    val glyph: String,
    val domain: SymbolDomain,
    val category: String,
    val description: String,
    val notationLatex: String,
    val notationUnicode: String,
    val notationAscii: String,
    val usageExamples: List<UsageExample>
)

data class UsageExample(
    val expression: String,
    val caption: String
)
