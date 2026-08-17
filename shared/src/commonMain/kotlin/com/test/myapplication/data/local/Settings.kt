package com.test.myapplication.data.local

import com.russhwolf.settings.Settings

expect fun createSettings(): Settings

object MvmSettings {
    private val settings: Settings by lazy { createSettings() }
    
    private const val KEY_VERSE_TEXT = "verse_text"
    private const val KEY_VERSE_REF = "verse_ref"
    private const val KEY_VERSE_DATE = "verse_date"

    fun saveDailyVerse(text: String, reference: String, date: Long) {
        settings.putString(KEY_VERSE_TEXT, text)
        settings.putString(KEY_VERSE_REF, reference)
        settings.putLong(KEY_VERSE_DATE, date)
    }

    fun getDailyVerseText(): String? = settings.getStringOrNull(KEY_VERSE_TEXT)
    fun getDailyVerseRef(): String? = settings.getStringOrNull(KEY_VERSE_REF)
    fun getDailyVerseDate(): Long = settings.getLong(KEY_VERSE_DATE, 0L)
}
