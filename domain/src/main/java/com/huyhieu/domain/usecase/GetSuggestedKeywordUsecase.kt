package com.huyhieu.domain.usecase

import com.huyhieu.domain.model.SearchHistoryModel
import com.huyhieu.domain.repository.DatabaseRepository
import javax.inject.Inject

class GetSuggestedKeywordUsecase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
) {
    suspend operator fun invoke(keyword: String, take: Int): List<SearchHistoryModel> {
        val result = databaseRepository.searchHistoryByQuery(keyword, take)
        return if (result.size < take) {
            val fill = databaseRepository
                .getHistories(take)
                .filterNot { recent -> result.any { it.keyword == recent.keyword } }
                .take(take - result.size)
            (result + fill)
        } else {
            result
        }
    }
}