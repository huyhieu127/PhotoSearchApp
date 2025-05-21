package com.huyhieu.data.mapper

import com.huyhieu.data.remote.resp.PhotoResp
import com.huyhieu.data.remote.resp.SearchPhotoResp
import com.huyhieu.data.remote.resp.SrcResp
import com.huyhieu.domain.model.PhotoModel
import com.huyhieu.domain.model.SearchPhotoModel
import com.huyhieu.domain.model.SrcModel


fun SearchPhotoResp.toSearchPhotoModel() = SearchPhotoModel(
    nextPage = nextPage,
    page = page,
    perPage = perPage,
    photos = photos.map { it.toPhotoModel() },
    totalResults = totalResults,
)

fun PhotoResp.toPhotoModel() = PhotoModel(
    alt = alt,
    avgColor = avgColor,
    height = height,
    id = id,
    liked = liked,
    photographer = photographer,
    photographerId = photographerId,
    photographerUrl = photographerUrl,
    src = src.toSrcModel(),
    url = url,
    width = width,
)

fun SrcResp.toSrcModel() = SrcModel(
    landscape = landscape,
    large = large,
    large2x = large2x,
    medium = medium,
    original = original,
    portrait = portrait,
    small = small,
    tiny = tiny,
)