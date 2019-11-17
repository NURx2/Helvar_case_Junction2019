package com.nevmem.helvarapp.type_converters

import androidx.room.TypeConverter
import com.nevmem.helvarapp.data.Room

class RoomModeEnumConverters {
    @TypeConverter
    fun convert(roomMode: Room.RoomMode): String {
        return when (roomMode) {
            Room.RoomMode.WORK -> "work"
            Room.RoomMode.SLEEP -> "sleep"
            Room.RoomMode.RELAX -> "relax"
            Room.RoomMode.CUSTOM -> "custom"
        }
    }

    @TypeConverter
    fun convert(mode: String): Room.RoomMode {
        return when (mode) {
            "work" -> Room.RoomMode.WORK
            "sleep" -> Room.RoomMode.SLEEP
            "relax" -> Room.RoomMode.RELAX
            "custom" -> Room.RoomMode.CUSTOM
            else -> throw IllegalStateException("Wrong string")
        }
    }
}