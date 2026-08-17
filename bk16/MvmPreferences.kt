package com.test.myapplication

import android.content.Context
import android.content.SharedPreferences

object MvmPreferences {
    private const val PREFS_NAME = "mvm_prefs"
    private const val KEY_VERSE_TEXT = "verse_text"
    private const val KEY_VERSE_REF = "verse_ref"
    private const val KEY_VERSE_DATE = "verse_date"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveDailyVerse(context: Context, text: String, reference: String, date: Long) {
        getPrefs(context).edit()
            .putString(KEY_VERSE_TEXT, text)
            .putString(KEY_VERSE_REF, reference)
            .putLong(KEY_VERSE_DATE, date)
            .apply()
    }

    fun getDailyVerseText(context: Context): String? {
        return getPrefs(context).getString(KEY_VERSE_TEXT, null)
    }

    fun getDailyVerseRef(context: Context): String? {
        return getPrefs(context).getString(KEY_VERSE_REF, null)
    }

    fun getDailyVerseDate(context: Context): Long {
        return getPrefs(context).getLong(KEY_VERSE_DATE, 0L)
    }
}
