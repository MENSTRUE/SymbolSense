package com.symbolsense.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.symbolsense.data.local.dao.ScanDao
import com.symbolsense.data.local.entity.DetectedSymbolEntity
import com.symbolsense.data.local.entity.ScanEntity

@Database(
    entities = [ScanEntity::class, DetectedSymbolEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SymbolSenseDatabase : RoomDatabase() {

    abstract fun scanDao(): ScanDao

    companion object {
        @Volatile
        private var INSTANCE: SymbolSenseDatabase? = null

        fun getInstance(context: Context): SymbolSenseDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SymbolSenseDatabase::class.java,
                    "symbolsense.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
