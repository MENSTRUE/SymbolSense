package com.symbolsense.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanEntity(
    @PrimaryKey val id: String,
    val domain: String,
    val rawPreviewText: String,
    val structuredOutput: String,
    val codeOutput: String,
    val imageUri: String?,
    val averageConfidence: Float,
    val needsReview: Boolean,
    val createdAt: Long
)
