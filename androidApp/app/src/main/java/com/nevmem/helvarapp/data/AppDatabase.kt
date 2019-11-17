package com.nevmem.helvarapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Room::class],
    exportSchema = false,
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roomsDao(): RoomsDao
}