package com.huyhieu.library.navigation

import android.content.Context
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.request.CachePolicy
import com.huyhieu.feature.photo_list.PhotoListScreen
import com.huyhieu.feature.photo_search.PhotoSearchScreen
import com.huyhieu.library.ui.shared.LocalAnimatedNavScope
import com.huyhieu.library.ui.shared.LocalImageListLoader
import com.huyhieu.library.ui.shared.LocalImageOriginLoader
import com.huyhieu.library.ui.shared.LocalSharedNavScope
import java.io.File

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigationGraph(
    modifier: Modifier,
    navController: NavHostController = rememberNavController(),
) {
    SharedTransitionLayout(
        //modifier = modifier,
    ) {
        CompositionLocalProvider(
            LocalSharedNavScope provides this,
            LocalImageListLoader provides rememberImageListLoader(),
            LocalImageOriginLoader provides rememberImageOriginLoader(),
        ) {
            NavHost(
                navController = navController,
                startDestination = AppDest.PhotoList,
            ) {
                composable<AppDest.PhotoList> {
                    CompositionLocalProvider(
                        LocalAnimatedNavScope provides this
                    ) {
                        PhotoListScreen(
                            onNavToSearch = {
                                navController.navigate(AppDest.PhotoSearch)
                            },
                        )
                    }
                }

                composable<AppDest.PhotoSearch> {
                    CompositionLocalProvider(LocalAnimatedNavScope provides this) {
                        PhotoSearchScreen(
                            onBackPressed = navController::popBackStackNotNull,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberImageListLoader(context: Context = LocalContext.current) = remember {
    ImageLoader.Builder(context).apply {
        memoryCachePolicy(CachePolicy.ENABLED)
        diskCachePolicy(CachePolicy.ENABLED)
        diskCache {
            DiskCache.Builder().apply {
                directory(File(context.cacheDir, "image_list_cache"))
                maxSizeBytes(100L * 1024 * 1024)// Giới hạn 100MB
            }.build()
        }
    }.build()
}

@Composable
private fun rememberImageOriginLoader(context: Context = LocalContext.current) = remember {
    ImageLoader.Builder(context).apply {
        memoryCachePolicy(CachePolicy.ENABLED)
        diskCachePolicy(CachePolicy.ENABLED)
        diskCache {
            DiskCache.Builder().apply {
                directory(File(context.cacheDir, "image_origin_cache"))
                maxSizeBytes(200L * 1024 * 1024)// Giới hạn 200MB
            }.build()
        }
    }.build()
}

private fun NavController.popBackStackNotNull() {
    if (previousBackStackEntry != null) {
        popBackStack()
    }
}