# SymbolSense

**SymbolSense** is an Android application prototype designed to recognize and organize symbols from images captured with the camera or selected from the gallery.

The application is currently focused on building a complete mobile workflow first, before integrating the final AI recognition model. The current MVP already provides the main user interface, image capture flow, crop interaction, local history storage, and supporting screens.

> **Current status:** UI/UX MVP is complete. The next development phase is AI-based symbol recognition, starting with mathematical symbols.

---

## Overview

SymbolSense is intended to help users identify symbols found in documents, notes, formulas, diagrams, or other visual sources.

The planned workflow is:

```text
Camera / Gallery
       ↓
Image Crop
       ↓
Image Preprocessing
       ↓
AI Symbol Recognition
       ↓
Symbol + Confidence
       ↓
Structured Result / LaTeX
       ↓
Save to History
       ↓
Export
```

The first AI implementation will focus on **mathematical symbols** before expanding to other domains.

---

## Current Features

### UI / UX

The MVP interface is already implemented, including:

- Splash screen
- First-run onboarding
- Home screen
- Bottom navigation
- Scan screen
- Camera interface
- Gallery picker
- Image crop screen
- Detection result screen
- Result preview
- History
- History detail
- Symbol library
- Symbol detail
- Settings

The onboarding is displayed only when the application is opened for the first time after installation or after application data is cleared.

---

## Implemented Functionality

### CameraX

SymbolSense already uses **CameraX** for real camera preview and image capture.

Users can:

- Open the camera
- Capture a real image
- Select an image from the gallery
- Continue the selected image to the crop screen

### Image Crop

The crop interface is interactive and supports:

- Dragging crop corners
- Moving crop edges
- Moving the crop area
- Resetting the crop
- Applying the selected crop
- Basic contrast enhancement

The cropped image is forwarded to the next processing stage.

### Room Database

Scan history is stored locally using **Room Database**.

Current persistence flow:

```text
Result
  ↓
Save
  ↓
Room Database
  ↓
History
  ↓
History Detail
```

Saved history remains available after the application is closed and reopened.

---

## AI Development Status

AI recognition is **not yet implemented in the current version**.

The next development phase will begin with mathematical symbol recognition.

Planned AI pipeline:

```text
Cropped Image
      ↓
Grayscale / Thresholding
      ↓
Symbol Segmentation
      ↓
CNN Classification
      ↓
Predicted Symbol
      ↓
Confidence Score
      ↓
LaTeX Conversion
```

The initial model is planned as a lightweight CNN that can later be converted to **TensorFlow Lite** for on-device inference.

Example target classes may include:

```text
0 1 2 3 4 5 6 7 8 9
+ - × ÷ =
< > ≤ ≥
( ) [ ]
x y
√ ∫
π
d
.
```

The exact class set may change during dataset preparation and model evaluation.

---

## Planned AI Workflow

Training will be performed outside the Android application, for example using Kaggle.

```text
Dataset
   ↓
Training CNN
   ↓
Evaluation
   ↓
model.keras
   ↓
TensorFlow Lite Conversion
   ↓
symbol_math.tflite
   ↓
Android Assets
   ↓
On-device Inference
```

The Android application will perform inference only.

---

## Planned Domains

SymbolSense is designed so recognition can later be expanded into several domains.

### Mathematics

Initial development priority.

Examples:

- Integral
- Square root
- Exponents
- Operators
- Inequality symbols
- Variables
- Numbers

### Chemistry

Planned future expansion for chemistry-related notation and symbols.

### Electronics

Planned future expansion for electronic and circuit-related symbols.

These domains are part of the application concept, but their AI recognition models are not yet implemented.

---

## Technology Stack

- **Kotlin**
- **Jetpack Compose**
- **Material 3**
- **Navigation Compose**
- **CameraX**
- **Room Database**
- **Coroutines**
- **Coil**
- **TensorFlow Lite** — prepared for the upcoming AI phase

---

## Project Status

| Component | Status |
|---|---|
| UI / UX MVP | ✅ Complete |
| First-run onboarding | ✅ Complete |
| Navigation | ✅ Complete |
| CameraX | ✅ Complete |
| Gallery picker | ✅ Complete |
| Interactive crop | ✅ Complete |
| Room persistence | ✅ Complete |
| Scan history | ✅ Complete |
| Symbol library UI | ✅ Complete |
| Mathematical AI model | 🚧 Next phase |
| Real symbol confidence | 🚧 Planned |
| Automatic LaTeX generation | 🚧 Planned |
| Chemistry recognition | 📌 Future |
| Electronics recognition | 📌 Future |
| Final export workflow | 📌 Future refinement |

---

## Next Development Phase

The UI is currently considered **frozen for the MVP**, except for small fixes that may be required during integration.

The main development focus now moves to:

1. Mathematical symbol dataset preparation
2. CNN model training
3. Model evaluation
4. TensorFlow Lite conversion
5. Android inference integration
6. Replacing dummy detection results with real model predictions
7. Generating confidence scores from model output
8. Converting recognized mathematical expressions into structured text / LaTeX

---

## Development Principle

SymbolSense is being developed incrementally.

The project intentionally separates interface development from AI development so each stage can be tested independently:

```text
UI / UX
   ✅
Room
   ✅
Camera
   ✅
Crop
   ✅
AI
   🚧
```

This prevents unfinished AI functionality from being presented as if it were already working.

---

## Notes

SymbolSense is currently a **work in progress** and should be treated as an MVP/prototype.

Screens that display recognition results currently serve as the interface for the upcoming AI pipeline. They should not yet be interpreted as evidence of a completed recognition model.

---

## Roadmap

```text
Phase 1 — UI / UX                         ✅
Phase 2 — Local persistence with Room    ✅
Phase 3 — CameraX and image capture      ✅
Phase 4 — Interactive image crop         ✅
Phase 5 — Mathematical AI recognition    🚧
Phase 6 — TFLite Android integration     ⏳
Phase 7 — Real result + confidence       ⏳
Phase 8 — LaTeX reconstruction           ⏳
Phase 9 — Additional symbol domains      ⏳
Phase 10 — Export and final refinement   ⏳
```

---

## License

A license has not yet been defined for this project.

