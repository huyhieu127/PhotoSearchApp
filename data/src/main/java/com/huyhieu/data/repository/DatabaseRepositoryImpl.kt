package com.huyhieu.data.repository

import com.huyhieu.data.datasource.DatabaseDatasource
import com.huyhieu.data.mapper.toSearchHistoryEntity
import com.huyhieu.data.mapper.toSearchHistoryModel
import com.huyhieu.data.mapper.toSearchHistoryModelList
import com.huyhieu.domain.model.SearchHistoryModel
import com.huyhieu.domain.repository.DatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val databaseDatasource: DatabaseDatasource,
) : DatabaseRepository {
    override suspend fun insertSearchHistory(searchHistory: SearchHistoryModel): Long {
        return databaseDatasource.insertSearchHistory(searchHistory.toSearchHistoryEntity())
    }

    override suspend fun deleteSearchHistory(id: Int): Int {
        return databaseDatasource.deleteSearchHistory(id)
    }

    override suspend fun searchHistoryByQuery(keyword: String, take: Int): List<SearchHistoryModel> {
        return databaseDatasource.searchHistoryByQuery(keyword, take).map { it.toSearchHistoryModel() }
    }

    override suspend fun getHistories(take: Int): List<SearchHistoryModel> {
        return databaseDatasource.getHistories(take).map { it.toSearchHistoryModel() }
    }

    override fun getListHistories(take: Int): Flow<List<SearchHistoryModel>> {
        return databaseDatasource.getListHistories(take).map { it.toSearchHistoryModelList() }
    }
}