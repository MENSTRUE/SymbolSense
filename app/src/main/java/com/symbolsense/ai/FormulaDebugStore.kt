package com.symbolsense.ai

import android.graphics.Bitmap

/**
 * Temporary in-memory debug snapshot for the latest formula recognition.
 *
 * This is intentionally NOT persisted and NOT part of the production model
 * contract. It exists only to diagnose detector-crop -> classifier problems.
 */
data class FormulaDebugCrop(
    val detectorIndex: Int,
    val rawCrop: Bitmap,
    val canonicalClassifierInput: Bitmap,
    val detectorConfidence: Float,
    val classifierTopK: List<SymbolPrediction>,
    val boundingBox: RecognitionBoundingBox,
    val rawWidth: Int,
    val rawHeight: Int
)

object FormulaDebugStore {
    @Volatile
    var latest: List<FormulaDebugCrop> = emptyList()
        private set

    fun clear() {
        latest = emptyList()
    }

    fun publish(items: List<FormulaDebugCrop>) {
        latest = items.toList()
    }
}
