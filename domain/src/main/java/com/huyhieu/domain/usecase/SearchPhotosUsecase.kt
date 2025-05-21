package com.huyhieu.domain.usecase

import com.huyhieu.domain.repository.PixelsRepository
import javax.inject.Inject

class SearchPhotosUsecase @Inject constructor(
    private val pixelsRepository: PixelsRepository,
) {
    operator fun invoke(query: String, orientation: String, size: String, color: String, page: Int, perPage: Int) = pixelsRepository.searchPhotos(query, orientation, size, color, page, perPage)
}