package com.symbolsense.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ScanWithSymbols(
    @Embedded val scan: ScanEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "scanId"
    )
    val symbols: List<DetectedSymbolEntity>
)
