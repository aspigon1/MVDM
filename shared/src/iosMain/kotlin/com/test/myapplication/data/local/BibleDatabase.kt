@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.test.myapplication.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getDatabaseBuilder(): RoomDatabase.Builder<BibleDatabase> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    val dbFile = (documentDirectory?.path ?: "") + "/bible_database.db"
    return Room.databaseBuilder<BibleDatabase>(
        name = dbFile,
        factory =  { BibleDatabaseConstructor.initialize() }
    )
}
