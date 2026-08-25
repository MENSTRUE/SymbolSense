package com.symbolsense.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.symbolsense.data.local.entity.DetectedSymbolEntity
import com.symbolsense.data.local.entity.ScanEntity
import com.symbolsense.data.local.entity.ScanWithSymbols
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {

    @Transaction
    @Query("SELECT * FROM scan_history ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ScanWithSymbols>>

    @Transaction
    @Query("SELECT * FROM scan_history WHERE id = :scanId LIMIT 1")
    fun observeById(scanId: String): Flow<ScanWithSymbols?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertScan(scan: ScanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSymbols(symbols: List<DetectedSymbolEntity>)

    @Query("DELETE FROM detected_symbol WHERE scanId = :scanId")
    suspend fun deleteSymbolsForScan(scanId: String)

    @Query("DELETE FROM scan_history WHERE id = :scanId")
    suspend fun deleteScanById(scanId: String)

    @Query("DELETE FROM scan_history")
    suspend fun clearAll()
}
