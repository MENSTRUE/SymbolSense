package com.symbolsense.data.repository

import androidx.room.withTransaction
import com.symbolsense.data.local.SymbolSenseDatabase
import com.symbolsense.data.local.entity.DetectedSymbolEntity
import com.symbolsense.data.local.entity.ScanEntity
import com.symbolsense.data.local.entity.ScanWithSymbols
import com.symbolsense.data.local.formatTimestampLabel
import com.symbolsense.data.model.DetectedSymbol
import com.symbolsense.data.model.RelativeBoundingBox
import com.symbolsense.data.model.ScanResult
import com.symbolsense.data.model.SymbolDomain
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScanRepository(
    private val database: SymbolSenseDatabase
) {
    private val scanDao = database.scanDao()

    fun observeHistory(): Flow<List<ScanResult>> {
        return scanDao.observeAll().map { rows -> rows.map { it.toModel() } }
    }

    fun observeScan(scanId: String): Flow<ScanResult?> {
        return scanDao.observeById(scanId).map { it?.toModel() }
    }

    /**
     * Menyimpan hasil scan ke Room.
     * Draft dari pipeline UI diberi UUID baru agar scan berulang tidak menimpa riwayat lama.
     */
    suspend fun saveScan(result: ScanResult): String {
        val storedId = if (result.id.startsWith("draft_")) {
            UUID.randomUUID().toString()
        } else {
            result.id
        }

        val createdAt = if (result.id.startsWith("draft_")) {
            System.currentTimeMillis()
        } else {
            result.createdAtMillis
        }

        val confidences = result.detectedSymbols.map { it.confidence }
        val averageConfidence = if (confidences.isEmpty()) 0f else confidences.average().toFloat()
        val needsReview = result.detectedSymbols.any { it.confidence < 0.70f }

        val scanEntity = ScanEntity(
            id = storedId,
            domain = result.domain.name,
            rawPreviewText = result.rawPreviewText,
            structuredOutput = result.structuredOutput,
            codeOutput = result.latexOrCode,
            imageUri = result.imageUri,
            averageConfidence = averageConfidence,
            needsReview = needsReview,
            createdAt = createdAt
        )

        val symbolEntities = result.detectedSymbols.mapIndexed { index, symbol ->
            DetectedSymbolEntity(
                id = "$storedId:${symbol.id}:$index",
                scanId = storedId,
                sourceSymbolId = symbol.id,
                label = symbol.label,
                displayGlyph = symbol.displayGlyph,
                confidence = symbol.confidence,
                boxLeft = symbol.boundingBox.left,
                boxTop = symbol.boundingBox.top,
                boxRight = symbol.boundingBox.right,
                boxBottom = symbol.boundingBox.bottom
            )
        }

        database.withTransaction {
            scanDao.upsertScan(scanEntity)
            scanDao.deleteSymbolsForScan(storedId)
            if (symbolEntities.isNotEmpty()) {
                scanDao.upsertSymbols(symbolEntities)
            }
        }

        return storedId
    }

    suspend fun deleteScan(scanId: String) {
        scanDao.deleteScanById(scanId)
    }

    suspend fun clearHistory() {
        scanDao.clearAll()
    }
}

private fun ScanWithSymbols.toModel(): ScanResult {
    val safeDomain = runCatching { SymbolDomain.valueOf(scan.domain) }
        .getOrDefault(SymbolDomain.GENERAL)

    return ScanResult(
        id = scan.id,
        domain = safeDomain,
        timestampLabel = formatTimestampLabel(scan.createdAt),
        rawPreviewText = scan.rawPreviewText,
        structuredOutput = scan.structuredOutput,
        latexOrCode = scan.codeOutput,
        detectedSymbols = symbols
            .sortedBy { it.id }
            .map { symbol ->
                DetectedSymbol(
                    id = symbol.sourceSymbolId,
                    label = symbol.label,
                    displayGlyph = symbol.displayGlyph,
                    confidence = symbol.confidence,
                    boundingBox = RelativeBoundingBox(
                        left = symbol.boxLeft,
                        top = symbol.boxTop,
                        right = symbol.boxRight,
                        bottom = symbol.boxBottom
                    )
                )
            },
        createdAtMillis = scan.createdAt,
        imageUri = scan.imageUri
    )
}
