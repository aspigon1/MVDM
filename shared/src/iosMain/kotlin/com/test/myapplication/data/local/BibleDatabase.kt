package com.test.myapplication.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

fun getDatabaseBuilder(): RoomDatabase.Builder<BibleDatabase> {
    val dbFile = NSHomeDirectory() + "/bible_database.db"
    return Room.databaseBuilder<BibleDatabase>(
        name = dbFile,
        factory =  { BibleDatabase_Impl() }
    )
}
