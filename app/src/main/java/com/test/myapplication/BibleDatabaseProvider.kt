package com.test.myapplication

import android.content.Context
import com.test.myapplication.data.local.BibleDatabase
import com.test.myapplication.data.local.getDatabaseBuilder
import com.test.myapplication.data.local.getRoomDatabase

object BibleDatabaseProvider {
    @Volatile
    private var INSTANCE: BibleDatabase? = null

    fun getDatabase(context: Context): BibleDatabase {
        return INSTANCE ?: synchronized(this) {
            val builder = getDatabaseBuilder(context)
            val instance = getRoomDatabase(builder)
            INSTANCE = instance
            instance
        }
    }
}
