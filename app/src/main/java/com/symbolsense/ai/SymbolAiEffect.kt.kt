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

@Composable
fun SymbolAiEffect(
    imageUri: Uri?,
    onSuccess: (SymbolRecognitionResult) -> Unit,
    onError: (Throwable) -> Unit
) {
    val context = LocalContext.current

    val classifier = remember {
        Log.d(TAG, "Creating SymbolClassifier...")

        SymbolClassifier(
            context = context.applicationContext
        )
    }

    DisposableEffect(classifier) {
        onDispose {
            Log.d(TAG, "Closing classifier")
            classifier.close()
        }
    }

    LaunchedEffect(imageUri) {

        Log.d(TAG, "LaunchedEffect imageUri = $imageUri")

        if (imageUri == null) {
            val error = IllegalStateException(
                "Image URI NULL. Tidak ada gambar yang dikirim ke AI."
            )

            Log.e(TAG, error.message ?: "URI null")
            onError(error)

            return@LaunchedEffect
        }

        try {
            Log.d(TAG, "Starting inference...")
            Log.d(TAG, "URI = $imageUri")

            val result = withContext(Dispatchers.IO) {
                classifier.classify(
                    uri = imageUri,
                    topK = 3
                )
            }

            Log.d(TAG, "Inference SUCCESS")
            Log.d(TAG, "Symbol = ${result.best.display}")
            Log.d(TAG, "Confidence = ${result.best.confidence}")
            Log.d(TAG, "Time = ${result.inferenceTimeMs} ms")

            onSuccess(result)

        } catch (throwable: Throwable) {

            Log.e(
                TAG,
                "Inference FAILED: ${throwable.message}",
                throwable
            )

            onError(throwable)
        }
    }
}

@Composable
fun SymbolAiBitmapEffect(
    bitmap: Bitmap?,
    onSuccess: (SymbolRecognitionResult) -> Unit,
    onError: (Throwable) -> Unit
) {
    val context = LocalContext.current

    val classifier = remember {
        SymbolClassifier(
            context = context.applicationContext
        )
    }

    DisposableEffect(classifier) {
        onDispose {
            classifier.close()
        }
    }

    LaunchedEffect(bitmap) {

        if (bitmap == null) {
            onError(
                IllegalStateException(
                    "Bitmap NULL."
                )
            )

            return@LaunchedEffect
        }

        try {
            val result = withContext(Dispatchers.Default) {
                classifier.classify(
                    bitmap = bitmap,
                    topK = 3
                )
            }

            onSuccess(result)

        } catch (throwable: Throwable) {

            Log.e(
                TAG,
                "Bitmap inference FAILED",
                throwable
            )

            onError(throwable)
        }
    }
}