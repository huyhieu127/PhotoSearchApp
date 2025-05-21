package com.huyhieu.data.repository

import com.huyhieu.core.network.network_monitor.NetworkMonitor
import com.huyhieu.data.datasource.PixelsDatasource
import com.huyhieu.data.mapper.ResultMapper
import com.huyhieu.data.mapper.toSearchPhotoModel
import com.huyhieu.domain.common.DataResult
import com.huyhieu.domain.model.SearchPhotoModel
import com.huyhieu.domain.repository.PixelsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PixelsRepositoryImpl @Inject constructor(
    private val pixelsDatasource: PixelsDatasource,
    private val networkMonitor: NetworkMonitor,
) : PixelsRepository, ResultMapper() {
    override fun searchPhotos(query: String, orientation: String, size: String, color: String, page: Int, perPage: Int): Flow<DataResult<SearchPhotoModel>> = callApi(
        hasConnection = networkMonitor.isConnected(),
        api = {
            pixelsDatasource.searchPhotos(query, orientation, size, color, page, perPage)
        },
        mapper = {
            it.toSearchPhotoModel()
        },
    )
}