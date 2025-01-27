package com.sandymist.android.debuglib.db

import androidx.room.TypeConverter

class Converters {
    // Convert List<String> to a String
    @TypeConverter
    fun fromListOfStrings(list: List<String>?): String? {
        return list?.joinToString(",") // Join the list elements with a comma
    }

    // Convert String back to List<String>
    @TypeConverter
    fun toListOfStrings(value: String?): List<String>? {
        return value?.split(",") // Split the string back into a list of strings
    }
}
