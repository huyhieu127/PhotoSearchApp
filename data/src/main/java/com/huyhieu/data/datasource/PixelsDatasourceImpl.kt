package com.huyhieu.data.datasource

import com.huyhieu.data.remote.resp.SearchPhotoResp
import com.huyhieu.data.remote.retrofit.PixelsService
import retrofit2.Response
import javax.inject.Inject

class PixelsDatasourceImpl @Inject constructor(
    private val pixelsService: PixelsService,
) : PixelsDatasource {
    override suspend fun searchPhotos(query: String, orientation: String, size: String, color: String, page: Int, perPage: Int): Response<SearchPhotoResp> {
        return pixelsService.searchPhotos(
            query = query,
            page = page,
            perPage = perPage,
            orientation = orientation,
            size = size,
            color = color,

        )
    }
}