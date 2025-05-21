package com.huyhieu.domain.model


data class SearchPhotoModel(
    val nextPage: String = "",
    val page: Int = 0,
    val perPage: Int = 0,
    val photos: List<PhotoModel> = listOf(),
    val totalResults: Int = 0,
)