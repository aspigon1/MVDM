@file:kotlin.jvm.JvmName("BibleDatabaseCommonKt")
package com.test.myapplication.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

@Database(
    entities = [BibleBookEntity::class, BibleChapterEntity::class, BibleVerseEntity::class], 
    version = 2,
    exportSchema = false
)
@ConstructedBy(BibleDatabaseConstructor::class)
abstract class BibleDatabase : RoomDatabase() {
    abstract fun bibleDao(): BibleDao
}

// The Room compiler generates the implementation of this class
expect object BibleDatabaseConstructor : RoomDatabaseConstructor<BibleDatabase>

object BibleDatabaseProvider {
    private var INSTANCE: BibleDatabase? = null

    fun getDatabase(builder: RoomDatabase.Builder<BibleDatabase>): BibleDatabase {
        val instance = INSTANCE
        if (instance != null) return instance
        
        val newInstance = getRoomDatabase(builder)
        INSTANCE = newInstance
        return newInstance
    }
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<BibleDatabase>
): BibleDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(true)
        .build()
}
