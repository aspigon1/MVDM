package com.test.myapplication.data.local

import androidx.room.*

@Dao
interface BibleDao {
    @Query("SELECT * FROM books WHERE bibleId = :bibleId")
    suspend fun getBooks(bibleId: String): List<BibleBookEntity>

    @Query("SELECT * FROM chapters WHERE bookId = :bookId AND bibleId = :bibleId")
    suspend fun getChapters(bookId: String, bibleId: String): List<BibleChapterEntity>

    @Query("SELECT * FROM verses WHERE chapterId = :chapterId AND bibleId = :bibleId")
    suspend fun getVerses(chapterId: String, bibleId: String): List<BibleVerseEntity>

    @Query("SELECT * FROM verses WHERE bibleId = :bibleId ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomVerse(bibleId: String): BibleVerseEntity?

    @Query("SELECT * FROM verses WHERE bibleId = :bibleId LIMIT 1 OFFSET :index")
    suspend fun getVerseAtIndex(index: Int, bibleId: String): BibleVerseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BibleBookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<BibleChapterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<BibleVerseEntity>)
    
    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM books WHERE id = :bookId AND bibleId = :bibleId)")
    suspend fun hasBook(bookId: String, bibleId: String): Boolean

    @Query("SELECT COUNT(*) FROM verses WHERE bibleId = :bibleId")
    suspend fun getVerseCountForBible(bibleId: String): Int

    @Query("SELECT * FROM verses WHERE text LIKE '%' || :query || '%' AND bibleId = :bibleId LIMIT 50")
    suspend fun searchVerses(query: String, bibleId: String): List<BibleVerseEntity>
}
