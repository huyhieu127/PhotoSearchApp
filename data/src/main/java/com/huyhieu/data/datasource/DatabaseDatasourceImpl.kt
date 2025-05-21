package com.huyhieu.data.datasource

import com.huyhieu.data.local.room.dao.SearchHistoryDao
import com.huyhieu.data.local.room.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseDatasourceImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao,
) : DatabaseDatasource {
    override suspend fun insertSearchHistory(searchHistory: SearchHistoryEntity): Long {
        return searchHistoryDao.insert(searchHistory)
    }

    override suspend fun deleteSearchHistory(id: Int): Int {
        return searchHistoryDao.deleteById(id)
    }

    override suspend fun searchHistoryByQuery(keyword: String, take: Int): List<SearchHistoryEntity> {
        return searchHistoryDao.searchHistoryByQuery(keyword, take)
    }

    override suspend fun getHistories(take: Int): List<SearchHistoryEntity> {
        return searchHistoryDao.getHistories(take)
    }

    override fun getListHistories(take: Int): Flow<List<SearchHistoryEntity>> {
        return searchHistoryDao.getListHistories(take)
    }
}