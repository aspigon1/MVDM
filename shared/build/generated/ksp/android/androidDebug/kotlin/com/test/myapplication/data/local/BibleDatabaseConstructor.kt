package com.test.myapplication.`data`.local

import androidx.room.RoomDatabaseConstructor

public actual object BibleDatabaseConstructor : RoomDatabaseConstructor<BibleDatabase> {
  override fun initialize(): BibleDatabase =
      com.test.myapplication.`data`.local.BibleDatabase_Impl()
}
