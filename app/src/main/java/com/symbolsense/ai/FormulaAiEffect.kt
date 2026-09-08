package com.symbolsense.ai

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val FORMULA_TAG = "SymbolSenseFormulaAI"

/**
 * Compose bridge for the full V1 formula pipeline.
 *
 * image -> detector -> many crops -> SymbolClassifier -> linear parser
 */
@Composable
fun FormulaAiEffect(
    imageUri: Uri?,
    onSuccess: (SymbolRecognitionResult) -> Unit,
    onError: (Throwable) -> Unit
) {
    val context = LocalContext.current

    val recognizer = remember {
        FormulaRecognizer(
            context = context.applicationContext
        )
    }

    DisposableEffect(recognizer) {
        onDispose {
            Log.d(FORMULA_TAG, "Closing FormulaRecognizer")
            recognizer.close()
        }
    }

    LaunchedEffect(imageUri) {
        if (imageUri == null) {
            onError(
                IllegalStateException(
                    "Image URI NULL. Tidak ada gambar yang dikirim ke formula pipeline."
                )
            )
            return@LaunchedEffect
        }

        try {
            val result = withContext(Dispatchers.IO) {
                recognizer.recognize(
                    uri = imageUri,
                    topK = 3
                )
            }

            Log.d(FORMULA_TAG, "Inference SUCCESS")
            Log.d(FORMULA_TAG, "Mode       = ${result.mode}")
            Log.d(FORMULA_TAG, "Symbols    = ${result.symbols.size}")
            Log.d(FORMULA_TAG, "Expression = ${result.structuredDisplay}")
            Log.d(FORMULA_TAG, "LaTeX      = ${result.structuredLatex}")
            Log.d(FORMULA_TAG, "Total ms   = ${result.inferenceTimeMs}")

            onSuccess(result)
        } catch (throwable: Throwable) {
            Log.e(
                FORMULA_TAG,
                "Formula inference FAILED: ${throwable.message}",
                throwable
            )
            onError(throwable)
        }
    }
}
