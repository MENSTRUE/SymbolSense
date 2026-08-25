package com.symbolsense.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.symbolsense.data.local.dao.ScanDao;
import com.symbolsense.data.local.dao.ScanDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SymbolSenseDatabase_Impl extends SymbolSenseDatabase {
  private volatile ScanDao _scanDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `scan_history` (`id` TEXT NOT NULL, `domain` TEXT NOT NULL, `rawPreviewText` TEXT NOT NULL, `structuredOutput` TEXT NOT NULL, `codeOutput` TEXT NOT NULL, `imageUri` TEXT, `averageConfidence` REAL NOT NULL, `needsReview` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `detected_symbol` (`id` TEXT NOT NULL, `scanId` TEXT NOT NULL, `sourceSymbolId` TEXT NOT NULL, `label` TEXT NOT NULL, `displayGlyph` TEXT NOT NULL, `confidence` REAL NOT NULL, `boxLeft` REAL NOT NULL, `boxTop` REAL NOT NULL, `boxRight` REAL NOT NULL, `boxBottom` REAL NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`scanId`) REFERENCES `scan_history`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_detected_symbol_scanId` ON `detected_symbol` (`scanId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '315cbbc95c1883a9f4c9c22f1417d3b1')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `scan_history`");
        db.execSQL("DROP TABLE IF EXISTS `detected_symbol`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsScanHistory = new HashMap<String, TableInfo.Column>(9);
        _columnsScanHistory.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanHistory.put("domain", new TableInfo.Column("domain", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanHistory.put("rawPreviewText", new TableInfo.Column("rawPreviewText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanHistory.put("structuredOutput", new TableInfo.Column("structuredOutput", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanHistory.put("codeOutput", new TableInfo.Column("codeOutput", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanHistory.put("imageUri", new TableInfo.Column("imageUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanHistory.put("averageConfidence", new TableInfo.Column("averageConfidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanHistory.put("needsReview", new TableInfo.Column("needsReview", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanHistory.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysScanHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesScanHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoScanHistory = new TableInfo("scan_history", _columnsScanHistory, _foreignKeysScanHistory, _indicesScanHistory);
        final TableInfo _existingScanHistory = TableInfo.read(db, "scan_history");
        if (!_infoScanHistory.equals(_existingScanHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "scan_history(com.symbolsense.data.local.entity.ScanEntity).\n"
                  + " Expected:\n" + _infoScanHistory + "\n"
                  + " Found:\n" + _existingScanHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsDetectedSymbol = new HashMap<String, TableInfo.Column>(10);
        _columnsDetectedSymbol.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDetectedSymbol.put("scanId", new TableInfo.Column("scanId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDetectedSymbol.put("sourceSymbolId", new TableInfo.Column("sourceSymbolId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDetectedSymbol.put("label", new TableInfo.Column("label", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDetectedSymbol.put("displayGlyph", new TableInfo.Column("displayGlyph", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDetectedSymbol.put("confidence", new TableInfo.Column("confidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDetectedSymbol.put("boxLeft", new TableInfo.Column("boxLeft", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDetectedSymbol.put("boxTop", new TableInfo.Column("boxTop", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDetectedSymbol.put("boxRight", new TableInfo.Column("boxRight", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDetectedSymbol.put("boxBottom", new TableInfo.Column("boxBottom", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDetectedSymbol = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysDetectedSymbol.add(new TableInfo.ForeignKey("scan_history", "CASCADE", "NO ACTION", Arrays.asList("scanId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesDetectedSymbol = new HashSet<TableInfo.Index>(1);
        _indicesDetectedSymbol.add(new TableInfo.Index("index_detected_symbol_scanId", false, Arrays.asList("scanId"), Arrays.asList("ASC")));
        final TableInfo _infoDetectedSymbol = new TableInfo("detected_symbol", _columnsDetectedSymbol, _foreignKeysDetectedSymbol, _indicesDetectedSymbol);
        final TableInfo _existingDetectedSymbol = TableInfo.read(db, "detected_symbol");
        if (!_infoDetectedSymbol.equals(_existingDetectedSymbol)) {
          return new RoomOpenHelper.ValidationResult(false, "detected_symbol(com.symbolsense.data.local.entity.DetectedSymbolEntity).\n"
                  + " Expected:\n" + _infoDetectedSymbol + "\n"
                  + " Found:\n" + _existingDetectedSymbol);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "315cbbc95c1883a9f4c9c22f1417d3b1", "d458d596e89d846ac994a10b3ed02f32");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "scan_history","detected_symbol");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `scan_history`");
      _db.execSQL("DELETE FROM `detected_symbol`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ScanDao.class, ScanDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ScanDao scanDao() {
    if (_scanDao != null) {
      return _scanDao;
    } else {
      synchronized(this) {
        if(_scanDao == null) {
          _scanDao = new ScanDao_Impl(this);
        }
        return _scanDao;
      }
    }
  }
}
