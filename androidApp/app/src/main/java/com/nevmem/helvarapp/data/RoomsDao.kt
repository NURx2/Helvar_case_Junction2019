package com.nevmem.helvarapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RoomsDao {
    @Insert
    fun addRoom(room: Room)

    @Query("select * from room")
    fun getAll(): List<Room>
}