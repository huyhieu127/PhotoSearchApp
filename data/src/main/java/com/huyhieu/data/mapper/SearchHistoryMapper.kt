package com.huyhieu.data.mapper

import com.huyhieu.data.local.room.entity.SearchHistoryEntity
import com.huyhieu.domain.model.SearchHistoryModel

fun SearchHistoryModel.toSearchHistoryEntity() = SearchHistoryEntity(
    keyword = keyword,
    timestamp = timestamp,
)

fun SearchHistoryEntity.toSearchHistoryModel() = SearchHistoryModel(
    id = id,
    keyword = keyword,
    timestamp = timestamp,
)

fun List<SearchHistoryEntity>.toSearchHistoryModelList() = map { it.toSearchHistoryModel() }