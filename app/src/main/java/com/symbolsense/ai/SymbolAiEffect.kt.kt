package com.symbolsense.ai

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "SymbolSenseAI"

/**
 * V5.6 AI entry point.
 *
 * ProcessingScreen tidak memanggil classifier isolated langsung.
 * Semua scan masuk melalui FormulaRecognizer agar:
 *
 * detector
 * -> scan quality guard
 * -> optional auto-crop/retry
 * -> isolated classifier per crop
 * -> structural recognizer
 * -> parser
 *
 * tetap satu pipeline.
 */
@Composable
fun SymbolAiEffect(
    imageUri: Uri?,
    onSuccess: (SymbolRecognitionResult) -> Unit,
    onError: (Throwable) -> Unit
) {
    val context =
        LocalContext.current

    val recognizer =
        remember {
            Log.d(
                TAG,
                "Creating FormulaRecognizer..."
            )

            FormulaRecognizer(
                context =
                    context.applicationContext
            )
        }

    DisposableEffect(
        recognizer
    ) {
        onDispose {
            Log.d(
                TAG,
                "Closing FormulaRecognizer"
            )

            recognizer.close()
        }
    }

    LaunchedEffect(
        imageUri
    ) {
        if (
            imageUri == null
        ) {
            val error =
                IllegalStateException(
                    "Tidak ada gambar yang dikirim ke AI."
                )

            Log.e(
                TAG,
                error.message
                    ?: "Image URI null"
            )

            onError(
                error
            )

            return@LaunchedEffect
        }

        try {
            Log.d(
                TAG,
                "Starting formula recognition: $imageUri"
            )

            val result =
                withContext(
                    Dispatchers.IO
                ) {
                    recognizer.recognize(
                        uri = imageUri,
                        topK = 5
                    )
                }

            Log.d(
                TAG,
                "Formula recognition SUCCESS"
            )

            Log.d(
                TAG,
                "display=${result.structuredDisplay}"
            )

            Log.d(
                TAG,
                "latex=${result.structuredLatex}"
            )

            Log.d(
                TAG,
                "symbols=${result.symbols.size}"
            )

            onSuccess(
                result
            )

        } catch (
            throwable: Throwable
        ) {
            Log.e(
                TAG,
                "Formula recognition FAILED: ${throwable.message}",
                throwable
            )

            onError(
                throwable
            )
        }
    }
}

@Composable
fun SymbolAiBitmapEffect(
    bitmap: Bitmap?,
    onSuccess: (SymbolRecognitionResult) -> Unit,
    onError: (Throwable) -> Unit
) {
    val context =
        LocalContext.current

    val recognizer =
        remember {
            FormulaRecognizer(
                context =
                    context.applicationContext
            )
        }

    DisposableEffect(
        recognizer
    ) {
        onDispose {
            recognizer.close()
        }
    }

    LaunchedEffect(
        bitmap
    ) {
        if (
            bitmap == null
        ) {
            onError(
                IllegalStateException(
                    "Bitmap NULL."
                )
            )

            return@LaunchedEffect
        }

        try {
            val result =
                withContext(
                    Dispatchers.Default
                ) {
                    recognizer.recognize(
                        bitmap = bitmap,
                        topK = 5
                    )
                }

            onSuccess(
                result
            )

        } catch (
            throwable: Throwable
        ) {
            Log.e(
                TAG,
                "Bitmap formula recognition FAILED",
                throwable
            )

            onError(
                throwable
            )
        }
    }
}
