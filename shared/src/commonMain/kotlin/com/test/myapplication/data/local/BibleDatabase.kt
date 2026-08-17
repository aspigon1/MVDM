@file:kotlin.jvm.JvmName("BibleDatabaseCommonKt")
package com.test.myapplication.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [BibleBookEntity::class, BibleChapterEntity::class, BibleVerseEntity::class], version = 2)
@ConstructedBy(BibleDatabaseConstructor::class)
abstract class BibleDatabase : RoomDatabase() {
    abstract fun bibleDao(): BibleDao
}

// The Room compiler generates the implementation of this class
expect object BibleDatabaseConstructor : RoomDatabaseConstructor<BibleDatabase>

fun getRoomDatabase(
    builder: RoomDatabase.Builder<BibleDatabase>
): BibleDatabase {
    return builder
        .fallbackToDestructiveMigration(true)
        .build()
}
