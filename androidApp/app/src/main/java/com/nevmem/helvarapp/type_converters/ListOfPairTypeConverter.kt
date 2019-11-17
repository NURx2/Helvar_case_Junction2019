package com.nevmem.helvarapp.type_converters

import androidx.room.TypeConverter

class ListOfPairTypeConverter {
    @TypeConverter
    fun convert(list: ArrayList<Pair<String, Double>>?): String {
        if (list == null)
            return "null"
        return list.joinToString("|") { it.first + "$" + it.second }
    }

    @TypeConverter
    fun convert(str: String): ArrayList<Pair<String, Double>>? {
        if (str == "null")
            return null
        val arrayList = ArrayList<Pair<String, Double>>()
        arrayList.addAll(str.split("|").map {
            val split = it.split("$")
            Pair<String, Double>(split[0], split[1].toDouble()) })
        return arrayList
    }
}