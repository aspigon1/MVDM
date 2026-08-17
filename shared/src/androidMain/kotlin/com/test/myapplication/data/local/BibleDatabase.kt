@file:JvmName("BibleDatabaseAndroidKt")
package com.test.myapplication.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<BibleDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("bible_database")
    return Room.databaseBuilder<BibleDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
