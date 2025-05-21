package com.huyhieu.domain.model

data class SearchHistoryModel(
    val id: Int = 0,
    val keyword: String,
    val timestamp: Long = System.currentTimeMillis()
)