package com.huyhieu.library.navigation

import kotlinx.serialization.Serializable

sealed class AppDest {

    @Serializable
    object PhotoList

    @Serializable
    object PhotoSearch
}