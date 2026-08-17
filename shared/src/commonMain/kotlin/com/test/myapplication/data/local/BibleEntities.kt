package com.test.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "books", primaryKeys = ["id", "bibleId"])
@Serializable
data class BibleBookEntity(
    val id: String,
    val bibleId: String,
    val name: String,
    val nameLong: String,
    val abbreviation: String
)

@Entity(tableName = "chapters", primaryKeys = ["id", "bibleId"])
@Serializable
data class BibleChapterEntity(
    val id: String,
    val bibleId: String,
    val number: String,
    val bookId: String
)

@Entity(tableName = "verses", primaryKeys = ["id", "bibleId"])
@Serializable
data class BibleVerseEntity(
    val id: String,
    val bibleId: String,
    val bookId: String,
    val chapterId: String,
    val verse: Int,
    val text: String,
    val reference: String
)
