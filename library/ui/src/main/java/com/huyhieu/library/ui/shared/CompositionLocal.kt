package com.huyhieu.library.ui.shared

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf
import coil3.ImageLoader

//Global
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedNavScope = compositionLocalOf<SharedTransitionScope> { throw Exception("LocalSharedTransitionScope is not found!") }

val LocalAnimatedNavScope = compositionLocalOf<AnimatedVisibilityScope> { throw Exception("LocalAnimatedVisibilityScope is not found!") }

val LocalImageListLoader = compositionLocalOf<ImageLoader> { throw Exception("LocalImageListLoader is not found!") }

val LocalImageOriginLoader = compositionLocalOf<ImageLoader> { throw Exception("LocalImageOriginLoader is not found!") }

//Screen
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharePhotoListScope = compositionLocalOf<SharedTransitionScope> { throw Exception("LocalSharePhotoListScope is not found!") }

val LocalAnimatedPhotoListScope = compositionLocalOf<AnimatedVisibilityScope> { throw Exception("LocalAnimatedPhotoListScope is not found!") }

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharePhotoSearchScope = compositionLocalOf<SharedTransitionScope> { throw Exception("LocalSharePhotoSearchScope is not found!") }

val LocalAnimatedPhotoSearchScope = compositionLocalOf<AnimatedVisibilityScope> { throw Exception("LocalAnimatedPhotoSearchScope is not found!") }
