package com.huyhieu.domain.usecase

import com.huyhieu.domain.repository.DatabaseRepository
import javax.inject.Inject

class DeleteSearchHistoryByIdDbUsecase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
) {
    suspend operator fun invoke(id: Int) = databaseRepository.deleteSearchHistory(id)
}