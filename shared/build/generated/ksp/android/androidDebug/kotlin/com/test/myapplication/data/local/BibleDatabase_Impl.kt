package com.test.myapplication.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class BibleDatabase_Impl : BibleDatabase() {
  private val _bibleDao: Lazy<BibleDao> = lazy {
    BibleDao_Impl(this)
  }


  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2,
        "dc9476b6b4f65e8a12179dce08eeb183", "23fa700ea4cd6e5c6b3a150dc3fe03a0") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `books` (`id` TEXT NOT NULL, `bibleId` TEXT NOT NULL, `name` TEXT NOT NULL, `nameLong` TEXT NOT NULL, `abbreviation` TEXT NOT NULL, PRIMARY KEY(`id`, `bibleId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `chapters` (`id` TEXT NOT NULL, `bibleId` TEXT NOT NULL, `number` TEXT NOT NULL, `bookId` TEXT NOT NULL, PRIMARY KEY(`id`, `bibleId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `verses` (`id` TEXT NOT NULL, `bibleId` TEXT NOT NULL, `bookId` TEXT NOT NULL, `chapterId` TEXT NOT NULL, `verse` INTEGER NOT NULL, `text` TEXT NOT NULL, `reference` TEXT NOT NULL, PRIMARY KEY(`id`, `bibleId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'dc9476b6b4f65e8a12179dce08eeb183')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `books`")
        connection.execSQL("DROP TABLE IF EXISTS `chapters`")
        connection.execSQL("DROP TABLE IF EXISTS `verses`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsBooks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBooks.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("bibleId", TableInfo.Column("bibleId", "TEXT", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("nameLong", TableInfo.Column("nameLong", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBooks.put("abbreviation", TableInfo.Column("abbreviation", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBooks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBooks: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBooks: TableInfo = TableInfo("books", _columnsBooks, _foreignKeysBooks,
            _indicesBooks)
        val _existingBooks: TableInfo = read(connection, "books")
        if (!_infoBooks.equals(_existingBooks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |books(com.test.myapplication.data.local.BibleBookEntity).
              | Expected:
              |""".trimMargin() + _infoBooks + """
              |
              | Found:
              |""".trimMargin() + _existingBooks)
        }
        val _columnsChapters: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsChapters.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChapters.put("bibleId", TableInfo.Column("bibleId", "TEXT", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChapters.put("number", TableInfo.Column("number", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChapters.put("bookId", TableInfo.Column("bookId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysChapters: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesChapters: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoChapters: TableInfo = TableInfo("chapters", _columnsChapters, _foreignKeysChapters,
            _indicesChapters)
        val _existingChapters: TableInfo = read(connection, "chapters")
        if (!_infoChapters.equals(_existingChapters)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |chapters(com.test.myapplication.data.local.BibleChapterEntity).
              | Expected:
              |""".trimMargin() + _infoChapters + """
              |
              | Found:
              |""".trimMargin() + _existingChapters)
        }
        val _columnsVerses: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsVerses.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVerses.put("bibleId", TableInfo.Column("bibleId", "TEXT", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVerses.put("bookId", TableInfo.Column("bookId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVerses.put("chapterId", TableInfo.Column("chapterId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVerses.put("verse", TableInfo.Column("verse", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVerses.put("text", TableInfo.Column("text", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVerses.put("reference", TableInfo.Column("reference", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysVerses: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesVerses: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoVerses: TableInfo = TableInfo("verses", _columnsVerses, _foreignKeysVerses,
            _indicesVerses)
        val _existingVerses: TableInfo = read(connection, "verses")
        if (!_infoVerses.equals(_existingVerses)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |verses(com.test.myapplication.data.local.BibleVerseEntity).
              | Expected:
              |""".trimMargin() + _infoVerses + """
              |
              | Found:
              |""".trimMargin() + _existingVerses)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "books", "chapters", "verses")
  }

  public override fun clearAllTables() {
    super.performClear(false, "books", "chapters", "verses")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(BibleDao::class, BibleDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun bibleDao(): BibleDao = _bibleDao.value
}
