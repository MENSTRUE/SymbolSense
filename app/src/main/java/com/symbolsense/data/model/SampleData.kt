package com.symbolsense.data.model

/**
 * Data dummy untuk preview Compose & pengembangan UI sebelum
 * data/ml dan data/local (Room) terhubung.
 */
object SampleData {

    val detectedSymbols = listOf(
        DetectedSymbol("1", "Integral", "∫", 0.97f, RelativeBoundingBox(0.05f, 0.05f, 0.20f, 0.22f)),
        DetectedSymbol("2", "Eksponen", "x²", 0.93f, RelativeBoundingBox(0.28f, 0.05f, 0.45f, 0.22f)),
        DetectedSymbol("3", "Diferensial", "dx", 0.88f, RelativeBoundingBox(0.55f, 0.05f, 0.75f, 0.22f)),
        DetectedSymbol("4", "Eksponensial", "e^x", 0.91f, RelativeBoundingBox(0.05f, 0.35f, 0.30f, 0.52f)),
        DetectedSymbol("5", "Akar kuadrat", "√(a²+b²)", 0.62f, RelativeBoundingBox(0.32f, 0.55f, 0.55f, 0.75f)),
        DetectedSymbol("6", "Kurang dari atau sama dengan", "≤", 0.58f, RelativeBoundingBox(0.58f, 0.55f, 0.70f, 0.75f))
    )

    val mathResult = ScanResult(
        id = "draft_math",
        domain = SymbolDomain.MATH,
        timestampLabel = "Hari ini, 14:32",
        rawPreviewText = "∫₀^∞ e^{-x²} dx = √π/2 · ...",
        structuredOutput = "∫ x² dx = x³/3 + C\ne^x = Σ xⁿ/n!\n√(a²+b²) ≤ a + b",
        latexOrCode = "\\int x^2 \\, dx = \\frac{x^3}{3} + C",
        detectedSymbols = detectedSymbols
    )

    // Riwayat tidak lagi memakai data dummy.
    // Data riwayat sekarang berasal dari Room Database.

    val symbolLibrary = listOf(
        SymbolEntry(
            id = "integral", name = "Integral", glyph = "∫", domain = SymbolDomain.MATH,
            category = "Kalkulus",
            description = "Simbol integral (∫) digunakan dalam kalkulus untuk menunjukkan operasi integrasi. " +
                "Ditemukan oleh Leibniz pada abad ke-17, simbol ini berasal dari huruf \"S\" panjang (summa), " +
                "yang merepresentasikan penjumlahan kontinu dari elemen-elemen infinitesimal.",
            notationLatex = "\\int_{a}^{b} f(x)\\,dx",
            notationUnicode = "U+222B",
            notationAscii = "integral(f,a,b)",
            usageExamples = listOf(
                UsageExample("∫ x² dx = x³/3 + C", "Integral tak tentu monomial"),
                UsageExample("∫₀^1 2x dx = [x²]₀^1 = 1", "Integral tertentu")
            )
        ),
        SymbolEntry(
            id = "sigma", name = "Sigma", glyph = "Σ", domain = SymbolDomain.MATH,
            category = "Operator",
            description = "Sigma (Σ) adalah notasi penjumlahan yang digunakan untuk merepresentasikan " +
                "penjumlahan berurutan dari suatu barisan suku.",
            notationLatex = "\\sum_{i=1}^{n} a_i",
            notationUnicode = "U+03A3",
            notationAscii = "sum(a_i, i=1..n)",
            usageExamples = listOf(
                UsageExample("Σ i, i=1..n = n(n+1)/2", "Jumlah n bilangan asli pertama")
            )
        ),
        SymbolEntry(
            id = "partial", name = "Turunan parsial", glyph = "∂", domain = SymbolDomain.MATH,
            category = "Kalkulus",
            description = "Simbol turunan parsial (∂) digunakan untuk menyatakan turunan suatu fungsi " +
                "multivariabel terhadap salah satu variabelnya.",
            notationLatex = "\\frac{\\partial f}{\\partial x}",
            notationUnicode = "U+2202",
            notationAscii = "d/dx",
            usageExamples = listOf(UsageExample("∂f/∂x", "Turunan parsial terhadap x"))
        ),
        SymbolEntry(
            id = "nabla", name = "Nabla", glyph = "∇", domain = SymbolDomain.MATH,
            category = "Kalkulus",
            description = "Nabla (∇) adalah operator diferensial vektor, digunakan untuk gradien, " +
                "divergensi, dan curl dalam kalkulus vektor.",
            notationLatex = "\\nabla f",
            notationUnicode = "U+2207",
            notationAscii = "nabla",
            usageExamples = listOf(UsageExample("∇²φ = ρ/ε₀", "Persamaan Poisson"))
        ),
        SymbolEntry(
            id = "plusminus", name = "Plus-Minus", glyph = "±", domain = SymbolDomain.MATH,
            category = "Operator",
            description = "Simbol plus-minus (±) menunjukkan dua kemungkinan nilai, positif maupun negatif.",
            notationLatex = "\\pm",
            notationUnicode = "U+00B1",
            notationAscii = "+-",
            usageExamples = listOf(UsageExample("x = -b ± √(b²-4ac) / 2a", "Rumus kuadrat (ABC)"))
        ),
        SymbolEntry(
            id = "infinity", name = "Tak hingga", glyph = "∞", domain = SymbolDomain.MATH,
            category = "Kalkulus",
            description = "Simbol tak hingga (∞) menyatakan suatu nilai yang tidak terbatas atau bertumbuh " +
                "tanpa batas.",
            notationLatex = "\\infty",
            notationUnicode = "U+221E",
            notationAscii = "infinity",
            usageExamples = listOf(UsageExample("∫₀^∞ e^{-x} dx = 1", "Integral tak hingga"))
        ),
        SymbolEntry(
            id = "pi", name = "Pi", glyph = "π", domain = SymbolDomain.MATH,
            category = "Yunani",
            description = "Pi (π) adalah konstanta matematika yang merepresentasikan rasio keliling " +
                "lingkaran terhadap diameternya, kira-kira 3.14159.",
            notationLatex = "\\pi",
            notationUnicode = "U+03C0",
            notationAscii = "pi",
            usageExamples = listOf(UsageExample("Keliling = 2πr", "Keliling lingkaran"))
        ),
        SymbolEntry(
            id = "lambda", name = "Lambda", glyph = "λ", domain = SymbolDomain.MATH,
            category = "Yunani",
            description = "Lambda (λ) sering digunakan untuk menyatakan panjang gelombang, eigenvalue, " +
                "atau parameter dalam berbagai rumus matematika dan fisika.",
            notationLatex = "\\lambda",
            notationUnicode = "U+03BB",
            notationAscii = "lambda",
            usageExamples = listOf(UsageExample("Av = λv", "Persamaan eigenvalue"))
        ),
        SymbolEntry(
            id = "delta", name = "Delta", glyph = "Δ", domain = SymbolDomain.MATH,
            category = "Yunani",
            description = "Delta besar (Δ) menyatakan selisih atau perubahan suatu nilai antara dua kondisi.",
            notationLatex = "\\Delta",
            notationUnicode = "U+0394",
            notationAscii = "delta",
            usageExamples = listOf(UsageExample("Δx = x₂ - x₁", "Selisih posisi"))
        ),
        SymbolEntry(
            id = "sqrt", name = "Akar kuadrat", glyph = "√", domain = SymbolDomain.MATH,
            category = "Aljabar",
            description = "Simbol akar (√) menyatakan operasi akar kuadrat (atau akar pangkat-n dengan indeks).",
            notationLatex = "\\sqrt{x}",
            notationUnicode = "U+221A",
            notationAscii = "sqrt(x)",
            usageExamples = listOf(UsageExample("√(a²+b²) ≤ a + b", "Pertidaksamaan segitiga"))
        ),
        SymbolEntry(
            id = "forall", name = "Untuk semua", glyph = "∀", domain = SymbolDomain.MATH,
            category = "Aljabar",
            description = "Kuantor universal (∀) berarti \"untuk semua\" — digunakan dalam logika dan " +
                "pernyataan matematis formal.",
            notationLatex = "\\forall",
            notationUnicode = "U+2200",
            notationAscii = "forall",
            usageExamples = listOf(UsageExample("∀x ∈ ℝ, x² ≥ 0", "Pernyataan kuantifikasi universal"))
        ),
        SymbolEntry(
            id = "exists", name = "Eksistensial", glyph = "∃", domain = SymbolDomain.MATH,
            category = "Aljabar",
            description = "Kuantor eksistensial (∃) berarti \"terdapat\" — menyatakan adanya minimal satu " +
                "elemen yang memenuhi suatu kondisi.",
            notationLatex = "\\exists",
            notationUnicode = "U+2203",
            notationAscii = "exists",
            usageExamples = listOf(UsageExample("∃x ∈ ℕ, x > 100", "Pernyataan eksistensial"))
        )
    )
}
