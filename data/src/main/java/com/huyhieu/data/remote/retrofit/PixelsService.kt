package com.huyhieu.data.remote.retrofit

import com.huyhieu.data.remote.resp.SearchPhotoResp
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PixelsService {
    @GET("/v1/search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("orientation") orientation: String,
        @Query("size") size: String,
        @Query("color") color: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): Response<SearchPhotoResp>
}