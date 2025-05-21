package com.huyhieu.data.datasource

import com.huyhieu.data.remote.resp.SearchPhotoResp
import retrofit2.Response

interface PixelsDatasource {
    suspend fun searchPhotos(query: String, orientation: String, size: String, color: String, page: Int, perPage: Int): Response<SearchPhotoResp>
}