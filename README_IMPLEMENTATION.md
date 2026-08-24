# SymbolSense — Android Implementation (Jetpack Compose Scaffold)

Implementasi UI lengkap untuk 15 screen SymbolSense sesuai mockup Figma, dibangun dengan
**Kotlin + Jetpack Compose + Material 3 + Navigation Compose**. Project ini adalah
**scaffold UI-first** — semua layar sudah jalan dengan data dummy (`SampleData.kt`),
siap dihubungkan ke ViewModel, Room, CameraX, dan TFLite.

---

## 1. Cara Membuka

1. Buka folder `SymbolSense/` di Android Studio (Hedgehog/Iguana atau lebih baru).
2. Biarkan Gradle sync otomatis (project sudah berisi `build.gradle.kts`, `settings.gradle.kts`).
3. Jalankan konfigurasi `app` ke emulator/device (minSdk 24, target 34).
4. Aplikasi langsung bisa di-navigate end-to-end: Splash → Onboarding → Home → Camera →
   Preview → Domain Confirm → Processing → Detection → Result Editor → Export, plus
   History, History Detail, Symbol Library, Symbol Detail, Settings.

---

## 2. Struktur Kode

```
app/src/main/java/com/symbolsense/
├── MainActivity.kt                 # Entry point, set SymbolSenseTheme + NavGraph
│
├── navigation/
│   ├── Screen.kt                   # Definisi semua route
│   └── NavGraph.kt                 # NavHost + wiring antar screen
│
├── ui/theme/
│   ├── Color.kt                    # Token warna (indigo/cyan/amber + domain colors)
│   ├── Type.kt                     # Typography (Space Grotesk + Inter, placeholder font sistem)
│   └── Theme.kt                    # ColorScheme light/dark, shapes
│
├── ui/components/                  # Komponen reusable lintas screen
│   ├── AppTopBar.kt                 # Top bar standar (back, judul, badge, aksi kanan)
│   ├── BottomNavBar.kt              # Bottom nav + FAB kamera mengambang
│   ├── DomainChips.kt               # DomainChip, DomainBadge, domainIcon()
│   ├── HistoryCards.kt              # HistoryCardCompact, HistoryCardFull, SymbolMiniCard
│   └── BoundingBoxOverlay.kt        # Overlay bounding box hasil deteksi
│
├── ui/screens/
│   ├── splash/SplashScreen.kt              # Screen 1
│   ├── onboarding/OnboardingScreen.kt      # Screen 2 (HorizontalPager 3 slide)
│   ├── home/HomeScreen.kt                  # Screen 3
│   ├── camera/CameraScreen.kt              # Screen 4 (placeholder live preview)
│   ├── preview/ImagePreviewScreen.kt       # Screen 5 (crop & adjust)
│   ├── domain/DomainConfirmationSheet.kt   # Screen 6 (ModalBottomSheet)
│   ├── processing/ProcessingScreen.kt      # Screen 7 (simulasi progress steps)
│   ├── detection/DetectionResultScreen.kt  # Screen 8
│   ├── editor/ResultEditorScreen.kt        # Screen 9 (tab Pratinjau / Kode)
│   ├── export/ExportBottomSheet.kt         # Screen 10 (ModalBottomSheet)
│   ├── history/HistoryScreen.kt            # Screen 11
│   ├── historydetail/HistoryDetailScreen.kt# Screen 12
│   ├── library/SymbolLibraryScreen.kt      # Screen 13
│   ├── symboldetail/SymbolDetailScreen.kt  # Screen 14
│   └── settings/SettingsScreen.kt          # Screen 15
│
└── data/model/
    ├── Models.kt                   # SymbolDomain, DetectedSymbol, ScanResult, SymbolEntry, dll
    └── SampleData.kt               # Data dummy untuk semua screen
```

---

## 3. Alur Navigasi (NavGraph.kt)

```
Splash --(timeout)--> Onboarding --(selesai)--> Home
Home --(FAB kamera)--> Camera --(capture/galeri)--> Preview
Preview --(Lanjutkan)--> [DomainConfirmationSheet overlay] --(Proses Sekarang)--> Processing
Processing --(otomatis selesai)--> Detection --(Lihat Hasil Terstruktur)--> Editor
Editor --(Export)--> [ExportBottomSheet overlay]
Home/BottomNav --> History --> HistoryDetail
Home/BottomNav --> Library --> SymbolDetail
TopBar settings icon --> Settings
```

`DomainConfirmationSheet` dan `ExportBottomSheet` **tidak** menjadi route NavHost terpisah —
ditampilkan sebagai `ModalBottomSheet` overlay di atas screen aktif (Preview & Editor),
dikontrol via local `remember { mutableStateOf(false) }`.

---

## 4. Yang Sudah Jalan vs TODO Integrasi

| Area | Status | Catatan |
|---|---|---|
| Seluruh 15 UI screen | ✅ Selesai | Sesuai mockup, pakai data dummy `SampleData` |
| Navigasi antar screen | ✅ Selesai | `NavGraph.kt` |
| Tema (warna, tipografi, shape) | ✅ Selesai | Bisa pasang font Space Grotesk/Inter — lihat `Type.kt` |
| Live camera preview (CameraX) | ⏳ TODO | Ganti `CameraPreviewPlaceholder()` di `CameraScreen.kt` dengan `AndroidView` + `PreviewView` |
| Crop interaktif (drag handle) | ⏳ TODO | `ImagePreviewScreen.kt` — handle saat ini statis/dekoratif |
| Inferensi TFLite (domain & symbol classifier) | ⏳ TODO | Buat `data/ml/TfliteSymbolClassifier.kt`, panggil dari `ProcessingScreen` |
| Layout reconstruction (LaTeX/SMILES/Netlist generator) | ⏳ TODO | `domain/postprocessing/` sesuai struktur di README utama |
| Riwayat persisten (Room) | ⏳ TODO | Ganti `SampleData.historyList` dengan Room DAO |
| Export PDF/DOCX & copy clipboard | ⏳ TODO | `ExportBottomSheet.onAction` |
| TTS aksesibilitas | ⏳ TODO | `SymbolDetailScreen.onSpeak` & toggle di `SettingsScreen` |
| Dark mode toggle nyata | ⏳ TODO | Saat ini `SymbolSenseTheme` ikut `isSystemInDarkTheme()`; hubungkan switch Settings ke DataStore lalu override |

---

## 5. Font Custom (opsional)

Untuk tampilan persis seperti Figma (Space Grotesk + Inter):

1. Download font dari Google Fonts, taruh di `app/src/main/res/font/`:
   - `space_grotesk_semibold.ttf`, `space_grotesk_bold.ttf`
   - `inter_regular.ttf`, `inter_medium.ttf`
2. Uncomment blok `FontFamily(...)` di `ui/theme/Type.kt` dan hapus fallback
   `FontFamily.SansSerif` / `FontFamily.Default`.

---

## 6. Langkah Lanjutan yang Disarankan

1. **CameraX dulu** — ganti placeholder di `CameraScreen` agar capture menghasilkan
   `Uri`/`Bitmap` nyata, lempar ke `ImagePreviewScreen` via argumen navigasi (atau
   shared ViewModel/SavedStateHandle).
2. **TFLite domain + symbol classifier** — mulai dari domain Matematika (dataset CROHME)
   sesuai roadmap di README utama project.
3. **Room untuk riwayat** — buat `ScanResultEntity`, `HistoryDao`, `AppDatabase`, lalu
   inject via Hilt ke `HistoryScreen` & `HomeScreen`.
4. **Hilt DI** — tambahkan `@HiltAndroidApp` Application class + `@AndroidEntryPoint`
   di `MainActivity`, plus modul untuk TFLite interpreter & Room.
