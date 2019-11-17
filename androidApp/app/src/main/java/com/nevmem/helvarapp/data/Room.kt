package com.nevmem.helvarapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nevmem.helvarapp.R
import com.nevmem.helvarapp.type_converters.ListOfPairTypeConverter
import com.nevmem.helvarapp.type_converters.RoomModeEnumConverters

@Entity
class Room(val name: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    enum class RoomMode {
        RELAX, WORK, SLEEP, CUSTOM
    }

    var temperature: Int = 4500
    set (value) {
        if (value == field)
            return
        field = value
        recalcMode()
    }

    val roomId: String
    get () {
        return when (name.toLowerCase()) {
            "living room" -> "0"
            "entrance" -> "1"
            "kitchen" -> "2"
            else -> "3"
        }
    }

    val roomName: String
    get() {
        return when (name.toLowerCase()) {
            "living room" -> "living"
            "entrance" -> "entrance"
            "kitchen" -> "kitchen"
            else -> "fuck"
        }
    }

    @TypeConverters(ListOfPairTypeConverter::class)
    var wifiProfile = ArrayList<Pair<String, Double>>()

    fun calculateProfileSimilarity(profile: List<Pair<String, Double>>): Double {
        val names = wifiProfile.map { it.first }.toHashSet() + profile.map { it.first }.toHashSet()
        var similarity = 0.0
        names.forEach { name ->
            var first = 0.0
            wifiProfile.find { name == it.first }?.let { first = it.second }
            var second = 0.0
            profile.find { name == it.first }?.let { second = it.second }
            similarity += (first - second) * (first - second)
        }
        return similarity
    }

    val imageId: Int
    get () {
        return when (name) {
            "Kitchen" -> R.drawable.food
            "Living room" -> R.drawable.tv
            "Entrance" -> R.drawable.walk
            else -> R.drawable.family
        }
    }

    var brightness: Int = 50
    set (value) {
        if (value == field)
            return
        field = value
        recalcMode()
    }

    @TypeConverters(RoomModeEnumConverters::class)
    var mode: RoomMode = RoomMode.CUSTOM

    var bySensor: Boolean = true

    fun modeString(): String {
        return when (mode) {
            RoomMode.CUSTOM -> "Custom preset"
            RoomMode.RELAX -> "Relax mode"
            RoomMode.SLEEP -> "Sleep mode"
            RoomMode.WORK -> "Work mode"
        }
    }

    private fun recalcMode() {
        // TODO: (modes handling)
    }
}