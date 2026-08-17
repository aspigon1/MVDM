package com.test.myapplication.`data`.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class BibleDao_Impl(
  __db: RoomDatabase,
) : BibleDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBibleBookEntity: EntityInsertAdapter<BibleBookEntity>

  private val __insertAdapterOfBibleChapterEntity: EntityInsertAdapter<BibleChapterEntity>

  private val __insertAdapterOfBibleVerseEntity: EntityInsertAdapter<BibleVerseEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfBibleBookEntity = object : EntityInsertAdapter<BibleBookEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `books` (`id`,`bibleId`,`name`,`nameLong`,`abbreviation`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BibleBookEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.bibleId)
        statement.bindText(3, entity.name)
        statement.bindText(4, entity.nameLong)
        statement.bindText(5, entity.abbreviation)
      }
    }
    this.__insertAdapterOfBibleChapterEntity = object : EntityInsertAdapter<BibleChapterEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `chapters` (`id`,`bibleId`,`number`,`bookId`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BibleChapterEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.bibleId)
        statement.bindText(3, entity.number)
        statement.bindText(4, entity.bookId)
      }
    }
    this.__insertAdapterOfBibleVerseEntity = object : EntityInsertAdapter<BibleVerseEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `verses` (`id`,`bibleId`,`bookId`,`chapterId`,`verse`,`text`,`reference`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BibleVerseEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.bibleId)
        statement.bindText(3, entity.bookId)
        statement.bindText(4, entity.chapterId)
        statement.bindLong(5, entity.verse.toLong())
        statement.bindText(6, entity.text)
        statement.bindText(7, entity.reference)
      }
    }
  }

  public override suspend fun insertBooks(books: List<BibleBookEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBibleBookEntity.insert(_connection, books)
  }

  public override suspend fun insertChapters(chapters: List<BibleChapterEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBibleChapterEntity.insert(_connection, chapters)
  }

  public override suspend fun insertVerses(verses: List<BibleVerseEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBibleVerseEntity.insert(_connection, verses)
  }

  public override suspend fun getBooks(bibleId: String): List<BibleBookEntity> {
    val _sql: String = "SELECT * FROM books WHERE bibleId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, bibleId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfBibleId: Int = getColumnIndexOrThrow(_stmt, "bibleId")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfNameLong: Int = getColumnIndexOrThrow(_stmt, "nameLong")
        val _cursorIndexOfAbbreviation: Int = getColumnIndexOrThrow(_stmt, "abbreviation")
        val _result: MutableList<BibleBookEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BibleBookEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_cursorIndexOfId)
          val _tmpBibleId: String
          _tmpBibleId = _stmt.getText(_cursorIndexOfBibleId)
          val _tmpName: String
          _tmpName = _stmt.getText(_cursorIndexOfName)
          val _tmpNameLong: String
          _tmpNameLong = _stmt.getText(_cursorIndexOfNameLong)
          val _tmpAbbreviation: String
          _tmpAbbreviation = _stmt.getText(_cursorIndexOfAbbreviation)
          _item = BibleBookEntity(_tmpId,_tmpBibleId,_tmpName,_tmpNameLong,_tmpAbbreviation)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getChapters(bookId: String, bibleId: String):
      List<BibleChapterEntity> {
    val _sql: String = "SELECT * FROM chapters WHERE bookId = ? AND bibleId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, bookId)
        _argIndex = 2
        _stmt.bindText(_argIndex, bibleId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfBibleId: Int = getColumnIndexOrThrow(_stmt, "bibleId")
        val _cursorIndexOfNumber: Int = getColumnIndexOrThrow(_stmt, "number")
        val _cursorIndexOfBookId: Int = getColumnIndexOrThrow(_stmt, "bookId")
        val _result: MutableList<BibleChapterEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BibleChapterEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_cursorIndexOfId)
          val _tmpBibleId: String
          _tmpBibleId = _stmt.getText(_cursorIndexOfBibleId)
          val _tmpNumber: String
          _tmpNumber = _stmt.getText(_cursorIndexOfNumber)
          val _tmpBookId: String
          _tmpBookId = _stmt.getText(_cursorIndexOfBookId)
          _item = BibleChapterEntity(_tmpId,_tmpBibleId,_tmpNumber,_tmpBookId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getVerses(chapterId: String, bibleId: String):
      List<BibleVerseEntity> {
    val _sql: String = "SELECT * FROM verses WHERE chapterId = ? AND bibleId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, chapterId)
        _argIndex = 2
        _stmt.bindText(_argIndex, bibleId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfBibleId: Int = getColumnIndexOrThrow(_stmt, "bibleId")
        val _cursorIndexOfBookId: Int = getColumnIndexOrThrow(_stmt, "bookId")
        val _cursorIndexOfChapterId: Int = getColumnIndexOrThrow(_stmt, "chapterId")
        val _cursorIndexOfVerse: Int = getColumnIndexOrThrow(_stmt, "verse")
        val _cursorIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _cursorIndexOfReference: Int = getColumnIndexOrThrow(_stmt, "reference")
        val _result: MutableList<BibleVerseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BibleVerseEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_cursorIndexOfId)
          val _tmpBibleId: String
          _tmpBibleId = _stmt.getText(_cursorIndexOfBibleId)
          val _tmpBookId: String
          _tmpBookId = _stmt.getText(_cursorIndexOfBookId)
          val _tmpChapterId: String
          _tmpChapterId = _stmt.getText(_cursorIndexOfChapterId)
          val _tmpVerse: Int
          _tmpVerse = _stmt.getLong(_cursorIndexOfVerse).toInt()
          val _tmpText: String
          _tmpText = _stmt.getText(_cursorIndexOfText)
          val _tmpReference: String
          _tmpReference = _stmt.getText(_cursorIndexOfReference)
          _item =
              BibleVerseEntity(_tmpId,_tmpBibleId,_tmpBookId,_tmpChapterId,_tmpVerse,_tmpText,_tmpReference)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRandomVerse(bibleId: String): BibleVerseEntity? {
    val _sql: String = "SELECT * FROM verses WHERE bibleId = ? ORDER BY RANDOM() LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, bibleId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfBibleId: Int = getColumnIndexOrThrow(_stmt, "bibleId")
        val _cursorIndexOfBookId: Int = getColumnIndexOrThrow(_stmt, "bookId")
        val _cursorIndexOfChapterId: Int = getColumnIndexOrThrow(_stmt, "chapterId")
        val _cursorIndexOfVerse: Int = getColumnIndexOrThrow(_stmt, "verse")
        val _cursorIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _cursorIndexOfReference: Int = getColumnIndexOrThrow(_stmt, "reference")
        val _result: BibleVerseEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_cursorIndexOfId)
          val _tmpBibleId: String
          _tmpBibleId = _stmt.getText(_cursorIndexOfBibleId)
          val _tmpBookId: String
          _tmpBookId = _stmt.getText(_cursorIndexOfBookId)
          val _tmpChapterId: String
          _tmpChapterId = _stmt.getText(_cursorIndexOfChapterId)
          val _tmpVerse: Int
          _tmpVerse = _stmt.getLong(_cursorIndexOfVerse).toInt()
          val _tmpText: String
          _tmpText = _stmt.getText(_cursorIndexOfText)
          val _tmpReference: String
          _tmpReference = _stmt.getText(_cursorIndexOfReference)
          _result =
              BibleVerseEntity(_tmpId,_tmpBibleId,_tmpBookId,_tmpChapterId,_tmpVerse,_tmpText,_tmpReference)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getVerseAtIndex(index: Int, bibleId: String): BibleVerseEntity? {
    val _sql: String = "SELECT * FROM verses WHERE bibleId = ? LIMIT 1 OFFSET ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, bibleId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, index.toLong())
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfBibleId: Int = getColumnIndexOrThrow(_stmt, "bibleId")
        val _cursorIndexOfBookId: Int = getColumnIndexOrThrow(_stmt, "bookId")
        val _cursorIndexOfChapterId: Int = getColumnIndexOrThrow(_stmt, "chapterId")
        val _cursorIndexOfVerse: Int = getColumnIndexOrThrow(_stmt, "verse")
        val _cursorIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _cursorIndexOfReference: Int = getColumnIndexOrThrow(_stmt, "reference")
        val _result: BibleVerseEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_cursorIndexOfId)
          val _tmpBibleId: String
          _tmpBibleId = _stmt.getText(_cursorIndexOfBibleId)
          val _tmpBookId: String
          _tmpBookId = _stmt.getText(_cursorIndexOfBookId)
          val _tmpChapterId: String
          _tmpChapterId = _stmt.getText(_cursorIndexOfChapterId)
          val _tmpVerse: Int
          _tmpVerse = _stmt.getLong(_cursorIndexOfVerse).toInt()
          val _tmpText: String
          _tmpText = _stmt.getText(_cursorIndexOfText)
          val _tmpReference: String
          _tmpReference = _stmt.getText(_cursorIndexOfReference)
          _result =
              BibleVerseEntity(_tmpId,_tmpBibleId,_tmpBookId,_tmpChapterId,_tmpVerse,_tmpText,_tmpReference)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getBookCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM books"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun hasBook(bookId: String, bibleId: String): Boolean {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM books WHERE id = ? AND bibleId = ?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, bookId)
        _argIndex = 2
        _stmt.bindText(_argIndex, bibleId)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getVerseCountForBible(bibleId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM verses WHERE bibleId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, bibleId)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun searchVerses(query: String, bibleId: String): List<BibleVerseEntity> {
    val _sql: String =
        "SELECT * FROM verses WHERE text LIKE '%' || ? || '%' AND bibleId = ? LIMIT 50"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        _argIndex = 2
        _stmt.bindText(_argIndex, bibleId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfBibleId: Int = getColumnIndexOrThrow(_stmt, "bibleId")
        val _cursorIndexOfBookId: Int = getColumnIndexOrThrow(_stmt, "bookId")
        val _cursorIndexOfChapterId: Int = getColumnIndexOrThrow(_stmt, "chapterId")
        val _cursorIndexOfVerse: Int = getColumnIndexOrThrow(_stmt, "verse")
        val _cursorIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _cursorIndexOfReference: Int = getColumnIndexOrThrow(_stmt, "reference")
        val _result: MutableList<BibleVerseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BibleVerseEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_cursorIndexOfId)
          val _tmpBibleId: String
          _tmpBibleId = _stmt.getText(_cursorIndexOfBibleId)
          val _tmpBookId: String
          _tmpBookId = _stmt.getText(_cursorIndexOfBookId)
          val _tmpChapterId: String
          _tmpChapterId = _stmt.getText(_cursorIndexOfChapterId)
          val _tmpVerse: Int
          _tmpVerse = _stmt.getLong(_cursorIndexOfVerse).toInt()
          val _tmpText: String
          _tmpText = _stmt.getText(_cursorIndexOfText)
          val _tmpReference: String
          _tmpReference = _stmt.getText(_cursorIndexOfReference)
          _item =
              BibleVerseEntity(_tmpId,_tmpBibleId,_tmpBookId,_tmpChapterId,_tmpVerse,_tmpText,_tmpReference)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
