package com.huyhieu.library.ui.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.huyhieu.domain.model.PhotoModel
import com.huyhieu.library.ui.R
import com.huyhieu.library.ui.bottom_sheet.FilterBottomSheet
import com.huyhieu.library.ui.common.AppPullToRefresh
import com.huyhieu.library.ui.common.FilterBox
import com.huyhieu.library.ui.common.LazyVerticalStaggeredGridLoadMore
import com.huyhieu.library.ui.common.ShimmerLoadingItem
import com.huyhieu.library.ui.model.ItemState
import com.huyhieu.library.ui.shared.LocalImageListLoader
import com.huyhieu.library.ui.shared.SharedTransitionKey
import com.huyhieu.library.ui.util.toColor

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PhotosGridUi(
    photos: List<ItemState<PhotoModel>>,
    listPhotosState: LazyStaggeredGridState,
    isNoData: Boolean,
    isLoadingMore: Boolean,
    hasError: Boolean,
    orientation: Int,
    size: Int,
    color: Int,
    sharedScope: SharedTransitionScope,
    animatedScope: AnimatedVisibilityScope,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onPhotoClick: (Int) -> Unit,
    onApplyChanges: (Int, Int, Int) -> Unit,
) {
    var isShowBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .background(Color.LightGray.copy(0.2f)),
        )
        if (photos.isNotEmpty()){
            FilterBox(
                orientation = orientation,
                size = size,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(vertical = 4.dp),
                onOrientationClick = {
                    isShowBottomSheet = true
                },
                onSizeClick = {
                    isShowBottomSheet = true
                },
                onColorClick = {
                    isShowBottomSheet = true
                }
            )
        }
        AppPullToRefresh(
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                photos.isNotEmpty() -> {
                    with(sharedScope) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyVerticalStaggeredGridLoadMore(
                                modifier = Modifier.fillMaxSize(),
                                listState = listPhotosState,
                                itemList = photos,
                                isLoadingMore = isLoadingMore,
                                hasError = hasError,
                                onLoadMore = onLoadMore,
                                itemLoading = {
                                    val aspectRatio = listOf((3 / 4F), (4 / 3F)).random()
                                    ShimmerLoadingItem(
                                        modifier = Modifier
                                            .aspectRatio(aspectRatio)
                                            .clip(RoundedCornerShape(20.dp))
                                            .padding(6.dp),
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                },
                                itemError = {
                                    Box(
                                        modifier = Modifier
                                            .aspectRatio(1F)
                                            .padding(6.dp)
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color.LightGray.copy(0.25F)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Button(onRetry) {
                                            Text("Retry")
                                        }
                                    }
                                },
                            ) { index, itemState ->
                                val photo = itemState.data
                                val avgColor = remember(photo.avgColor) { photo.avgColor.toColor() }
                                val id = remember(photo.id) { photo.id }
                                val imageLoader = LocalImageListLoader.current
                                val state = rememberSharedContentState(key = "${SharedTransitionKey.PhotoDetail.PHOTO}${id}")
                                Box(
                                    modifier = Modifier
                                        .sharedElement(
                                            sharedContentState = state,
                                            animatedVisibilityScope = animatedScope,
                                        )
                                        .aspectRatio(itemState.width.toFloat() / itemState.height.toFloat())
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White)
                                        .clickable {
                                            if (!isTransitionActive) onPhotoClick(index)
                                        },
                                ) {
                                    SubcomposeAsyncImage(
                                        model = itemState.url,
                                        imageLoader = imageLoader,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(avgColor),
                                        loading = {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                            ) {
                                                Image(
                                                    painter = painterResource(R.drawable.ic_photo),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .align(Alignment.Center)
                                                        .size(32.dp),
                                                    colorFilter = ColorFilter.tint(Color.White, BlendMode.SrcIn)
                                                )
                                            }
                                        },
                                        error = {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                            ) {
                                                Image(
                                                    painter = painterResource(R.drawable.ic_photo),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .align(Alignment.Center)
                                                        .size(32.dp),
                                                    colorFilter = ColorFilter.tint(Color.Red, BlendMode.SrcIn)
                                                )
                                            }
                                        },
                                    )

                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .fillMaxWidth()
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(
                                                        Color.Transparent,
                                                        avgColor.copy(0.9F),
                                                    )
                                                )
                                            )
                                            .padding(top = 40.dp)
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Image(
                                            painterResource(R.drawable.avatar),
                                            modifier = Modifier
                                                .sharedElement(
                                                    sharedContentState = rememberSharedContentState(key = "${SharedTransitionKey.PhotoDetail.AVATAR}${id}"),
                                                    animatedVisibilityScope = animatedScope,
                                                )
                                                .size(20.dp)
                                                .clip(CircleShape),
                                            contentDescription = null,
                                        )
                                        Spacer(modifier = Modifier.size(6.dp))
                                        Text(
                                            photo.photographer,
                                            color = Color.White,
                                            style = TextStyle(fontSize = 13.sp),
                                            modifier = Modifier
                                                .sharedElement(
                                                    sharedContentState = rememberSharedContentState(key = "${SharedTransitionKey.PhotoDetail.PHOTOGRAPHER}${id}"),
                                                    animatedVisibilityScope = animatedScope,
                                                )
                                                .weight(1F),
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1,
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.White, Color.Transparent)
                                        ),
                                    ),
                            )
                        }
                    }
                }

                isNoData -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 80.dp)
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painterResource(R.drawable.img_no_data),
                            contentDescription = null,
                            modifier = Modifier,
                            contentScale = ContentScale.FillWidth,
                        )
                        Text(
                            "No images found!", style = TextStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                            ), color = Color.DarkGray, modifier = Modifier.offset(y = -30.dp)
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(
                            "Please try again with a different keyword.", color = Color.DarkGray, style = TextStyle(
                                fontSize = 16.sp,
                            ), textAlign = TextAlign.Center, modifier = Modifier.offset(y = -30.dp)
                        )
                    }
                }

                isLoadingMore -> {
                    LazyVerticalStaggeredGrid(
                        state = rememberLazyStaggeredGridState(),
                        columns = StaggeredGridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(6.dp),
                    ) {
                        items(15) {
                            val aspectRatio = listOf((3 / 4F), (4 / 3F)).random()
                            ShimmerLoadingItem(
                                modifier = Modifier
                                    .aspectRatio(aspectRatio)
                                    .clip(RoundedCornerShape(20.dp))
                                    .padding(6.dp)
                            )
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 80.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painterResource(R.drawable.img_error),
                            contentDescription = null,
                            modifier = Modifier,
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(
                            "Oops!",
                            style = TextStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = Color.DarkGray,
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            "Something went wrong!",
                            color = Color.DarkGray,
                            style = TextStyle(
                                fontSize = 16.sp,
                            ),
                        )
                        Spacer(modifier = Modifier.size(24.dp))
                        Button(onRetry) {
                            Text("Try Again")
                        }
                    }
                }
            }
        }
    }

    if (isShowBottomSheet) {
        FilterBottomSheet(
            orientation = orientation,
            size = size,
            color = color,
            onDismiss = {
                isShowBottomSheet = false
            },
            onApplyChanges = { p1, p2, p3 ->
                isShowBottomSheet = false
                onApplyChanges(p1, p2, p3)
            }
        )
    }
}