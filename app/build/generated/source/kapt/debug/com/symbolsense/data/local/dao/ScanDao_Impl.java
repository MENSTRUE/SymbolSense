package com.symbolsense.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.symbolsense.data.local.entity.DetectedSymbolEntity;
import com.symbolsense.data.local.entity.ScanEntity;
import com.symbolsense.data.local.entity.ScanWithSymbols;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ScanDao_Impl implements ScanDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ScanEntity> __insertionAdapterOfScanEntity;

  private final EntityInsertionAdapter<DetectedSymbolEntity> __insertionAdapterOfDetectedSymbolEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSymbolsForScan;

  private final SharedSQLiteStatement __preparedStmtOfDeleteScanById;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public ScanDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfScanEntity = new EntityInsertionAdapter<ScanEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `scan_history` (`id`,`domain`,`rawPreviewText`,`structuredOutput`,`codeOutput`,`imageUri`,`averageConfidence`,`needsReview`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScanEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getDomain() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDomain());
        }
        if (entity.getRawPreviewText() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getRawPreviewText());
        }
        if (entity.getStructuredOutput() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getStructuredOutput());
        }
        if (entity.getCodeOutput() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCodeOutput());
        }
        if (entity.getImageUri() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getImageUri());
        }
        statement.bindDouble(7, entity.getAverageConfidence());
        final int _tmp = entity.getNeedsReview() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfDetectedSymbolEntity = new EntityInsertionAdapter<DetectedSymbolEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `detected_symbol` (`id`,`scanId`,`sourceSymbolId`,`label`,`displayGlyph`,`confidence`,`boxLeft`,`boxTop`,`boxRight`,`boxBottom`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DetectedSymbolEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getScanId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getScanId());
        }
        if (entity.getSourceSymbolId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getSourceSymbolId());
        }
        if (entity.getLabel() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getLabel());
        }
        if (entity.getDisplayGlyph() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDisplayGlyph());
        }
        statement.bindDouble(6, entity.getConfidence());
        statement.bindDouble(7, entity.getBoxLeft());
        statement.bindDouble(8, entity.getBoxTop());
        statement.bindDouble(9, entity.getBoxRight());
        statement.bindDouble(10, entity.getBoxBottom());
      }
    };
    this.__preparedStmtOfDeleteSymbolsForScan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM detected_symbol WHERE scanId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteScanById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM scan_history WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM scan_history";
        return _query;
      }
    };
  }

  @Override
  public Object upsertScan(final ScanEntity scan, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfScanEntity.insert(scan);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertSymbols(final List<DetectedSymbolEntity> symbols,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDetectedSymbolEntity.insert(symbols);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSymbolsForScan(final String scanId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSymbolsForScan.acquire();
        int _argIndex = 1;
        if (scanId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, scanId);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteSymbolsForScan.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteScanById(final String scanId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteScanById.acquire();
        int _argIndex = 1;
        if (scanId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, scanId);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteScanById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ScanWithSymbols>> observeAll() {
    final String _sql = "SELECT * FROM scan_history ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"detected_symbol",
        "scan_history"}, new Callable<List<ScanWithSymbols>>() {
      @Override
      @NonNull
      public List<ScanWithSymbols> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfDomain = CursorUtil.getColumnIndexOrThrow(_cursor, "domain");
            final int _cursorIndexOfRawPreviewText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawPreviewText");
            final int _cursorIndexOfStructuredOutput = CursorUtil.getColumnIndexOrThrow(_cursor, "structuredOutput");
            final int _cursorIndexOfCodeOutput = CursorUtil.getColumnIndexOrThrow(_cursor, "codeOutput");
            final int _cursorIndexOfImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUri");
            final int _cursorIndexOfAverageConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "averageConfidence");
            final int _cursorIndexOfNeedsReview = CursorUtil.getColumnIndexOrThrow(_cursor, "needsReview");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final ArrayMap<String, ArrayList<DetectedSymbolEntity>> _collectionSymbols = new ArrayMap<String, ArrayList<DetectedSymbolEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getString(_cursorIndexOfId);
              }
              if (_tmpKey != null) {
                if (!_collectionSymbols.containsKey(_tmpKey)) {
                  _collectionSymbols.put(_tmpKey, new ArrayList<DetectedSymbolEntity>());
                }
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipdetectedSymbolAscomSymbolsenseDataLocalEntityDetectedSymbolEntity(_collectionSymbols);
            final List<ScanWithSymbols> _result = new ArrayList<ScanWithSymbols>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final ScanWithSymbols _item;
              final ScanEntity _tmpScan;
              final String _tmpId;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmpId = null;
              } else {
                _tmpId = _cursor.getString(_cursorIndexOfId);
              }
              final String _tmpDomain;
              if (_cursor.isNull(_cursorIndexOfDomain)) {
                _tmpDomain = null;
              } else {
                _tmpDomain = _cursor.getString(_cursorIndexOfDomain);
              }
              final String _tmpRawPreviewText;
              if (_cursor.isNull(_cursorIndexOfRawPreviewText)) {
                _tmpRawPreviewText = null;
              } else {
                _tmpRawPreviewText = _cursor.getString(_cursorIndexOfRawPreviewText);
              }
              final String _tmpStructuredOutput;
              if (_cursor.isNull(_cursorIndexOfStructuredOutput)) {
                _tmpStructuredOutput = null;
              } else {
                _tmpStructuredOutput = _cursor.getString(_cursorIndexOfStructuredOutput);
              }
              final String _tmpCodeOutput;
              if (_cursor.isNull(_cursorIndexOfCodeOutput)) {
                _tmpCodeOutput = null;
              } else {
                _tmpCodeOutput = _cursor.getString(_cursorIndexOfCodeOutput);
              }
              final String _tmpImageUri;
              if (_cursor.isNull(_cursorIndexOfImageUri)) {
                _tmpImageUri = null;
              } else {
                _tmpImageUri = _cursor.getString(_cursorIndexOfImageUri);
              }
              final float _tmpAverageConfidence;
              _tmpAverageConfidence = _cursor.getFloat(_cursorIndexOfAverageConfidence);
              final boolean _tmpNeedsReview;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfNeedsReview);
              _tmpNeedsReview = _tmp != 0;
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              _tmpScan = new ScanEntity(_tmpId,_tmpDomain,_tmpRawPreviewText,_tmpStructuredOutput,_tmpCodeOutput,_tmpImageUri,_tmpAverageConfidence,_tmpNeedsReview,_tmpCreatedAt);
              final ArrayList<DetectedSymbolEntity> _tmpSymbolsCollection;
              final String _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getString(_cursorIndexOfId);
              }
              if (_tmpKey_1 != null) {
                _tmpSymbolsCollection = _collectionSymbols.get(_tmpKey_1);
              } else {
                _tmpSymbolsCollection = new ArrayList<DetectedSymbolEntity>();
              }
              _item = new ScanWithSymbols(_tmpScan,_tmpSymbolsCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<ScanWithSymbols> observeById(final String scanId) {
    final String _sql = "SELECT * FROM scan_history WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (scanId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, scanId);
    }
    return CoroutinesRoom.createFlow(__db, true, new String[] {"detected_symbol",
        "scan_history"}, new Callable<ScanWithSymbols>() {
      @Override
      @Nullable
      public ScanWithSymbols call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfDomain = CursorUtil.getColumnIndexOrThrow(_cursor, "domain");
            final int _cursorIndexOfRawPreviewText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawPreviewText");
            final int _cursorIndexOfStructuredOutput = CursorUtil.getColumnIndexOrThrow(_cursor, "structuredOutput");
            final int _cursorIndexOfCodeOutput = CursorUtil.getColumnIndexOrThrow(_cursor, "codeOutput");
            final int _cursorIndexOfImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUri");
            final int _cursorIndexOfAverageConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "averageConfidence");
            final int _cursorIndexOfNeedsReview = CursorUtil.getColumnIndexOrThrow(_cursor, "needsReview");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final ArrayMap<String, ArrayList<DetectedSymbolEntity>> _collectionSymbols = new ArrayMap<String, ArrayList<DetectedSymbolEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmpKey = null;
              } else {
                _tmpKey = _cursor.getString(_cursorIndexOfId);
              }
              if (_tmpKey != null) {
                if (!_collectionSymbols.containsKey(_tmpKey)) {
                  _collectionSymbols.put(_tmpKey, new ArrayList<DetectedSymbolEntity>());
                }
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipdetectedSymbolAscomSymbolsenseDataLocalEntityDetectedSymbolEntity(_collectionSymbols);
            final ScanWithSymbols _result;
            if (_cursor.moveToFirst()) {
              final ScanEntity _tmpScan;
              final String _tmpId;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmpId = null;
              } else {
                _tmpId = _cursor.getString(_cursorIndexOfId);
              }
              final String _tmpDomain;
              if (_cursor.isNull(_cursorIndexOfDomain)) {
                _tmpDomain = null;
              } else {
                _tmpDomain = _cursor.getString(_cursorIndexOfDomain);
              }
              final String _tmpRawPreviewText;
              if (_cursor.isNull(_cursorIndexOfRawPreviewText)) {
                _tmpRawPreviewText = null;
              } else {
                _tmpRawPreviewText = _cursor.getString(_cursorIndexOfRawPreviewText);
              }
              final String _tmpStructuredOutput;
              if (_cursor.isNull(_cursorIndexOfStructuredOutput)) {
                _tmpStructuredOutput = null;
              } else {
                _tmpStructuredOutput = _cursor.getString(_cursorIndexOfStructuredOutput);
              }
              final String _tmpCodeOutput;
              if (_cursor.isNull(_cursorIndexOfCodeOutput)) {
                _tmpCodeOutput = null;
              } else {
                _tmpCodeOutput = _cursor.getString(_cursorIndexOfCodeOutput);
              }
              final String _tmpImageUri;
              if (_cursor.isNull(_cursorIndexOfImageUri)) {
                _tmpImageUri = null;
              } else {
                _tmpImageUri = _cursor.getString(_cursorIndexOfImageUri);
              }
              final float _tmpAverageConfidence;
              _tmpAverageConfidence = _cursor.getFloat(_cursorIndexOfAverageConfidence);
              final boolean _tmpNeedsReview;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfNeedsReview);
              _tmpNeedsReview = _tmp != 0;
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              _tmpScan = new ScanEntity(_tmpId,_tmpDomain,_tmpRawPreviewText,_tmpStructuredOutput,_tmpCodeOutput,_tmpImageUri,_tmpAverageConfidence,_tmpNeedsReview,_tmpCreatedAt);
              final ArrayList<DetectedSymbolEntity> _tmpSymbolsCollection;
              final String _tmpKey_1;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmpKey_1 = null;
              } else {
                _tmpKey_1 = _cursor.getString(_cursorIndexOfId);
              }
              if (_tmpKey_1 != null) {
                _tmpSymbolsCollection = _collectionSymbols.get(_tmpKey_1);
              } else {
                _tmpSymbolsCollection = new ArrayList<DetectedSymbolEntity>();
              }
              _result = new ScanWithSymbols(_tmpScan,_tmpSymbolsCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipdetectedSymbolAscomSymbolsenseDataLocalEntityDetectedSymbolEntity(
      @NonNull final ArrayMap<String, ArrayList<DetectedSymbolEntity>> _map) {
    final Set<String> __mapKeySet = _map.keySet();
    if (__mapKeySet.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchArrayMap(_map, true, (map) -> {
        __fetchRelationshipdetectedSymbolAscomSymbolsenseDataLocalEntityDetectedSymbolEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`scanId`,`sourceSymbolId`,`label`,`displayGlyph`,`confidence`,`boxLeft`,`boxTop`,`boxRight`,`boxBottom` FROM `detected_symbol` WHERE `scanId` IN (");
    final int _inputSize = __mapKeySet == null ? 1 : __mapKeySet.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    if (__mapKeySet == null) {
      _stmt.bindNull(_argIndex);
    } else {
      for (String _item : __mapKeySet) {
        if (_item == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, _item);
        }
        _argIndex++;
      }
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "scanId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfScanId = 1;
      final int _cursorIndexOfSourceSymbolId = 2;
      final int _cursorIndexOfLabel = 3;
      final int _cursorIndexOfDisplayGlyph = 4;
      final int _cursorIndexOfConfidence = 5;
      final int _cursorIndexOfBoxLeft = 6;
      final int _cursorIndexOfBoxTop = 7;
      final int _cursorIndexOfBoxRight = 8;
      final int _cursorIndexOfBoxBottom = 9;
      while (_cursor.moveToNext()) {
        final String _tmpKey;
        if (_cursor.isNull(_itemKeyIndex)) {
          _tmpKey = null;
        } else {
          _tmpKey = _cursor.getString(_itemKeyIndex);
        }
        if (_tmpKey != null) {
          final ArrayList<DetectedSymbolEntity> _tmpRelation = _map.get(_tmpKey);
          if (_tmpRelation != null) {
            final DetectedSymbolEntity _item_1;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpScanId;
            if (_cursor.isNull(_cursorIndexOfScanId)) {
              _tmpScanId = null;
            } else {
              _tmpScanId = _cursor.getString(_cursorIndexOfScanId);
            }
            final String _tmpSourceSymbolId;
            if (_cursor.isNull(_cursorIndexOfSourceSymbolId)) {
              _tmpSourceSymbolId = null;
            } else {
              _tmpSourceSymbolId = _cursor.getString(_cursorIndexOfSourceSymbolId);
            }
            final String _tmpLabel;
            if (_cursor.isNull(_cursorIndexOfLabel)) {
              _tmpLabel = null;
            } else {
              _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            }
            final String _tmpDisplayGlyph;
            if (_cursor.isNull(_cursorIndexOfDisplayGlyph)) {
              _tmpDisplayGlyph = null;
            } else {
              _tmpDisplayGlyph = _cursor.getString(_cursorIndexOfDisplayGlyph);
            }
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final float _tmpBoxLeft;
            _tmpBoxLeft = _cursor.getFloat(_cursorIndexOfBoxLeft);
            final float _tmpBoxTop;
            _tmpBoxTop = _cursor.getFloat(_cursorIndexOfBoxTop);
            final float _tmpBoxRight;
            _tmpBoxRight = _cursor.getFloat(_cursorIndexOfBoxRight);
            final float _tmpBoxBottom;
            _tmpBoxBottom = _cursor.getFloat(_cursorIndexOfBoxBottom);
            _item_1 = new DetectedSymbolEntity(_tmpId,_tmpScanId,_tmpSourceSymbolId,_tmpLabel,_tmpDisplayGlyph,_tmpConfidence,_tmpBoxLeft,_tmpBoxTop,_tmpBoxRight,_tmpBoxBottom);
            _tmpRelation.add(_item_1);
          }
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
