package com.huyhieu.domain.repository

import com.huyhieu.domain.common.DataResult
import com.huyhieu.domain.model.SearchPhotoModel
import kotlinx.coroutines.flow.Flow

interface PixelsRepository {
    fun searchPhotos(query: String, orientation: String, size: String, color: String, page: Int, perPage: Int): Flow<DataResult<SearchPhotoModel>>
}