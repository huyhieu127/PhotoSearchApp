package com.huyhieu.domain.usecase

import com.huyhieu.domain.repository.DatabaseRepository
import javax.inject.Inject

class GetSearchHistoryDbUsecase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
) {
    operator fun invoke(take: Int) = databaseRepository.getListHistories(take)
}