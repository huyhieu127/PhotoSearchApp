package com.huyhieu.domain.usecase

import com.huyhieu.domain.model.SearchHistoryModel
import com.huyhieu.domain.repository.DatabaseRepository
import javax.inject.Inject

class InsertSearchHistoryDbUsecase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) {
    suspend operator fun invoke(searchHistory: SearchHistoryModel) = databaseRepository.insertSearchHistory(searchHistory)
}