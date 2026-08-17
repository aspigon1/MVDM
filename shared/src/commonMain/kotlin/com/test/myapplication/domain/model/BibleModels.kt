package com.test.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BibleResponse<T>(val data: T)

@Serializable
data class Bible(
    val id: String,
    val name: String,
    val abbreviation: String,
    val description: String? = null
)

@Serializable
data class Book(
    val id: String,
    val bibleId: String,
    val name: String,
    val nameLong: String,
    val abbreviation: String
)

@Serializable
data class Chapter(
    val id: String,
    val bibleId: String,
    val number: String,
    val bookId: String,
    val content: String? = null
)

@Serializable
data class VerseSearchResult(
    val query: String,
    val verses: List<Verse>
)

@Serializable
data class Verse(
    val id: String,
    val orgId: String = "",
    val bibleId: String,
    val bookId: String,
    val chapterId: String,
    val text: String,
    val reference: String,
    val number: Int = 0
)
