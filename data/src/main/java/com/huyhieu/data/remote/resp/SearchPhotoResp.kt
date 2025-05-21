package com.huyhieu.data.remote.resp


import com.google.gson.annotations.SerializedName
import com.huyhieu.domain.model.SearchPhotoModel

class SearchPhotoResp(
    @SerializedName("next_page")
    val nextPage: String = "",
    @SerializedName("page")
    val page: Int = 0,
    @SerializedName("per_page")
    val perPage: Int = 0,
    @SerializedName("photos")
    val photos: List<PhotoResp> = listOf(),
    @SerializedName("total_results")
    val totalResults: Int = 0,
)