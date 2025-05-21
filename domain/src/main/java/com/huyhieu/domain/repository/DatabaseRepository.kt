package com.huyhieu.domain.repository

import com.huyhieu.domain.model.SearchHistoryModel
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    suspend fun insertSearchHistory(searchHistory: SearchHistoryModel): Long
    suspend fun deleteSearchHistory(id: Int): Int
    suspend fun searchHistoryByQuery(keyword: String, take: Int): List<SearchHistoryModel>
    suspend fun getHistories(take: Int): List<SearchHistoryModel>

    fun getListHistories(take: Int): Flow<List<SearchHistoryModel>>
}