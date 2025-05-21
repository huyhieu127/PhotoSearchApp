package com.huyhieu.data.datasource

import com.huyhieu.data.local.room.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

interface DatabaseDatasource {
    suspend fun insertSearchHistory(searchHistory: SearchHistoryEntity): Long
    suspend fun deleteSearchHistory(id: Int): Int
    suspend fun searchHistoryByQuery(keyword: String, take: Int): List<SearchHistoryEntity>
    suspend fun getHistories(take: Int): List<SearchHistoryEntity>
    fun getListHistories(take: Int): Flow<List<SearchHistoryEntity>>
}