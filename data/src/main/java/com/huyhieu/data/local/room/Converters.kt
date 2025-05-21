package com.huyhieu.data.local.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.huyhieu.data.local.room.entity.SearchHistoryEntity

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromSearchHistoryEntity(searchHistoryEntity: SearchHistoryEntity): String {
        return gson.toJson(searchHistoryEntity)
    }

    @TypeConverter
    fun toSearchHistoryEntity(json: String): SearchHistoryEntity {
        val type = object : TypeToken<SearchHistoryEntity>() {}.type
        return gson.fromJson(json, type)
    }
}
