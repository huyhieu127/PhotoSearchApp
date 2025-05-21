package com.huyhieu.feature.photo_list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.EaseInOutCirc
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.huyhieu.library.ui.R
import com.huyhieu.library.ui.common.FilterBox
import com.huyhieu.library.ui.component.PhotoDetailUi
import com.huyhieu.library.ui.component.PhotosGridUi
import com.huyhieu.library.ui.dialog.NoInternetConnectionDialog
import com.huyhieu.library.ui.shared.DataProvider
import com.huyhieu.library.ui.shared.LocalAnimatedNavScope
import com.huyhieu.library.ui.shared.LocalAnimatedPhotoListScope
import com.huyhieu.library.ui.shared.LocalSharePhotoListScope
import com.huyhieu.library.ui.shared.LocalSharedNavScope
import com.huyhieu.library.ui.shared.SharedTransitionKey
import com.huyhieu.library.ui.theme.BubbleText
import com.huyhieu.library.ui.util.LaunchEffectContract
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun PhotoListScreenPreview(modifier: Modifier = Modifier) {
    PhotoListScreen(modifier = modifier)
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PhotoListScreen(
    modifier: Modifier = Modifier,
    vm: PhotoListVM = hiltViewModel(),
    onNavToSearch: () -> Unit = {},
) {
    val state by vm.state.collectAsState()
    val scope = rememberCoroutineScope()
    val listPhotosState = rememberLazyStaggeredGridState()

    LaunchedEffect(state.page) {
        if (state.page != 1) {
            if (state.page == 0 && state.query.isEmpty()) {
                val suggestions = DataProvider.listSuggestions.random()
                vm.onEvent(PhotoListContract.Event.OnSuggestionQuery(suggestions))
            } else {
                vm.onEvent(PhotoListContract.Event.OnSearchPhoto(state.query, state.page))
            }
        }
    }

    BackHandler(state.isPhotoDetailVisible) {
        if (state.isPhotoDetailVisible) {
            vm.onEvent(PhotoListContract.Event.OnPhotoDetailVisibilityChanged(false, state.photoIndex))
        }
    }

    LaunchEffectContract(vm) { effect ->
        when (effect) {
            PhotoListContract.Effect.PopBackStack -> {
                val firstIndex = listPhotosState.firstVisibleItemIndex
                if (state.photoIndex - firstIndex < 2) {
                    scope.launch {
                        listPhotosState.scrollToItem(state.photoIndex)
                    }
                }
                vm.onEvent(PhotoListContract.Event.OnPhotoDetailVisibilityChanged(false, state.photoIndex))
            }

            PhotoListContract.Effect.NavToSearch -> {
                onNavToSearch()
            }
        }
    }

    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharePhotoListScope provides this,
        ) {
            AnimatedContent(
                state.isPhotoDetailVisible,
                label = "open_detail",
            ) { targetState ->
                CompositionLocalProvider(
                    LocalAnimatedPhotoListScope provides this,
                ) {
                    if (targetState) {
                        PhotoDetailUi(
                            index = state.photoIndex,
                            photoState = state.photos[state.photoIndex],
                            isNetworkConnection = vm.isNetworkConnection,
                            sharedScope = this@SharedTransitionLayout,
                            animatedScope = this@AnimatedContent,
                        ) {
                            vm.onEvent(PhotoListContract.Event.OnPhotoDetailVisibilityChanged(false, state.photoIndex))
                        }
                    } else {
                        PhotoListUi(
                            modifier = modifier,
                            state = state,
                            onEvent = vm::onEvent,
                            onEffect = vm::onEffect,
                            listPhotosState = listPhotosState,
                        )
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = state.isNoConnection,
    ) {
        NoInternetConnectionDialog(
            onDismiss = {
                vm.onEvent(PhotoListContract.Event.OnNoConnectionVisibilityChanged(false))
                vm.onEvent(PhotoListContract.Event.OnRetry)
            },
            onConfirm = {
                vm.onEvent(PhotoListContract.Event.OnNoConnectionVisibilityChanged(false))
            },
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PhotoListUi(
    modifier: Modifier,
    state: PhotoListContract.State,
    onEvent: (PhotoListContract.Event) -> Unit,
    onEffect: (PhotoListContract.Effect) -> Unit = {},
    listPhotosState: LazyStaggeredGridState = remember { LazyStaggeredGridState() },
) {
    val sharedScope = LocalSharedNavScope.current
    val animatedScope = LocalAnimatedNavScope.current

    with(sharedScope) {
        Column(
            modifier = modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = SharedTransitionKey.PhotoDetail.ITEM_BOUNDS),
                    animatedVisibilityScope = animatedScope,
                )
                .fillMaxSize()
                .background(Color.White)
                .safeDrawingPadding()
        ) {
            Toolbar(
                isLoading = state.isLoading,
                query = state.query,
                onSearchClick = {
                    onEffect(PhotoListContract.Effect.NavToSearch)
                }
            )
            PhotosGridUi(
                photos = state.photos,
                listPhotosState = listPhotosState,
                isNoData = false,
                isLoadingMore = state.isLoading,
                hasError = state.hasError,
                orientation = state.orientation,
                size = state.size,
                color = state.color,
                sharedScope = LocalSharePhotoListScope.current,
                animatedScope = LocalAnimatedPhotoListScope.current,
                onRefresh = {
                    onEvent(PhotoListContract.Event.OnRefresh)
                },
                onRetry = {
                    onEvent(PhotoListContract.Event.OnRetry)
                },
                onLoadMore = {
                    onEvent(PhotoListContract.Event.OnLoadMore)
                },
                onPhotoClick = { index ->
                    onEvent(PhotoListContract.Event.OnPhotoDetailVisibilityChanged(true, index))
                },
                onApplyChanges = { orientation, size, color ->
                    onEvent(PhotoListContract.Event.OnApplyChanges(orientation, size, color))
                },
            )

        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Toolbar(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    query: String = "",
    onSearchClick: () -> Unit = {},
) {
    val sharedScope = LocalSharedNavScope.current
    val animatedScope = LocalAnimatedNavScope.current
    var isVisibleQuote by remember { mutableStateOf(false) }
    LaunchedEffect(!isLoading) {
        delay(50)
        if (!isVisibleQuote && !isLoading) {
            isVisibleQuote = true
        }
    }
    with(sharedScope) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painterResource(R.drawable.ic_pixels),
                contentDescription = null,
                modifier = Modifier,
            )
            Spacer(Modifier.width(8.dp))
            AnimatedVisibility(
                visible = isVisibleQuote,
                enter = scaleIn(
                    animationSpec = tween(durationMillis = 500, easing = EaseInOutCirc),
                    transformOrigin = TransformOrigin(0f, 1f) // Bottom-Start (left-bottom)
                ),
                exit = scaleOut(
                    animationSpec = tween(durationMillis = 300),
                    transformOrigin = TransformOrigin(0f, 1f)
                ),
                modifier = Modifier
                    .padding(bottom = 7.dp)
            ) {
                Text(
                    "“$query”",
                    modifier = Modifier
                        .background(
                            BubbleText,
                            RoundedCornerShape(
                                topStartPercent = 50,
                                topEndPercent = 50,
                                bottomEndPercent = 50,
                            ),
                        )
                        .padding(
                            horizontal = 10.dp,
                            vertical = 8.dp,
                        ),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                    ),
                    color = Color.White,
                )
            }
            Spacer(Modifier.weight(1F))
            Box(
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(SharedTransitionKey.PhotoSearch.BOX),
                        animatedVisibilityScope = animatedScope
                    )
                    .clip(CircleShape)
                    .clickable(onClick = onSearchClick)
                    .border(1.dp, Color.LightGray, CircleShape)
                    .size(42.dp)
                    .padding(11.dp),
            ) {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = null,
                    modifier = Modifier
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(SharedTransitionKey.PhotoSearch.IMAGE),
                            animatedVisibilityScope = animatedScope
                        ),
                    tint = Color.DarkGray,
                )
            }
        }
    }
}
