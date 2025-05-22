package com.huyhieu.feature.photo_search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.huyhieu.domain.model.SearchHistoryModel
import com.huyhieu.library.ui.R
import com.huyhieu.library.ui.component.PhotoDetailUi
import com.huyhieu.library.ui.component.PhotosGridUi
import com.huyhieu.library.ui.dialog.NoInternetConnectionDialog
import com.huyhieu.library.ui.shared.DataProvider
import com.huyhieu.library.ui.shared.LocalAnimatedNavScope
import com.huyhieu.library.ui.shared.LocalAnimatedPhotoSearchScope
import com.huyhieu.library.ui.shared.LocalSharePhotoSearchScope
import com.huyhieu.library.ui.shared.LocalSharedNavScope
import com.huyhieu.library.ui.shared.SharedTransitionKey
import com.huyhieu.library.ui.util.LaunchEffectContract
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PhotoSearchScreen(
    modifier: Modifier = Modifier,
    vm: PhotoSearchVM = hiltViewModel(),
    onBackPressed: () -> Unit = {},
) {
    val state by vm.state.collectAsState()
    val listPhotosState = rememberLazyStaggeredGridState()

    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val onBack: () -> Unit = {
        if (state.isFocused) {
            keyboard?.hide()
            focusManager.clearFocus()
            vm.onEvent(PhotoSearchContract.Event.OnFocusChanged(false))
        } else {
            onBackPressed()
        }
    }

    LaunchEffectContract(vm) { effect ->
        when (effect) {
            PhotoSearchContract.Effect.PopBackStack -> {
                onBack()
            }

            PhotoSearchContract.Effect.ScrollToTop -> {
                listPhotosState.requestScrollToItem(0)
            }
        }
    }

    BackHandler(state.isFocused || state.isPhotoDetailVisible) {
        if (state.isPhotoDetailVisible) {
            vm.onEvent(PhotoSearchContract.Event.OnPhotoDetailVisibilityChanged(false, state.photoIndex))
        } else {
            onBack()
        }
    }

    LaunchedEffect(Unit) {
        vm.onEvent(PhotoSearchContract.Event.OnInitial)
    }

    LaunchedEffect(state.query, state.page) {
        vm.onEvent(PhotoSearchContract.Event.OnSearchPhoto(state.query, state.page))
    }

    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharePhotoSearchScope provides this,
        ) {
            AnimatedContent(
                state.isPhotoDetailVisible,
                label = "open_detail",
            ) { targetState ->
                CompositionLocalProvider(
                    LocalAnimatedPhotoSearchScope provides this,
                ) {
                    if (targetState) {
                        PhotoDetailUi(
                            photoState = state.photos[state.photoIndex],
                            index = state.photoIndex,
                            isNetworkConnection = vm.isNetworkConnection,
                            sharedScope = this@SharedTransitionLayout,
                            animatedScope = this@AnimatedContent,
                        ) {
                            vm.onEvent(PhotoSearchContract.Event.OnPhotoDetailVisibilityChanged(false, state.photoIndex))
                        }
                    } else {
                        PhotoSearchUi(
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
                vm.onEvent(PhotoSearchContract.Event.OnNoConnectionVisibilityChanged(false))
                vm.onEvent(PhotoSearchContract.Event.OnRetry)
            },
            onConfirm = {
                vm.onEvent(PhotoSearchContract.Event.OnNoConnectionVisibilityChanged(false))
            },
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PhotoSearchUi(
    modifier: Modifier,
    state: PhotoSearchContract.State = PhotoSearchContract.State(),
    onEvent: (PhotoSearchContract.Event) -> Unit = {},
    onEffect: (PhotoSearchContract.Effect) -> Unit = {},
    listPhotosState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) {
    val sharedScope = LocalSharedNavScope.current
    val animatedScope = LocalAnimatedNavScope.current

    with(sharedScope) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .padding(
                            start = 8.dp,
                            end = 12.dp,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable {
                                onEffect(PhotoSearchContract.Effect.PopBackStack)
                            }
                            .padding(4.dp),
                        tint = Color.DarkGray,
                    )
                    Spacer(Modifier.width(8.dp))
                    SearchTextField(
                        modifier = modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(SharedTransitionKey.PhotoSearch.BOX),
                            animatedVisibilityScope = animatedScope,
                        ),
                        text = state.text,
                        query = state.query,
                        isAutoFocused = state.isFocused,
                        onValueChange = {
                            onEvent(PhotoSearchContract.Event.OnGetSuggestedKeyword(it))
                        },
                        onFocused = {
                            onEvent(PhotoSearchContract.Event.OnFocusChanged(it))
                        },
                        onSearch = {
                            onEvent(PhotoSearchContract.Event.OnValueChanged(it))
                        },
                    )
                }
                AnimatedContent(
                    targetState = state.isFocused,
                    label = "search_helper",
                ) { isShowInput ->
                    if (isShowInput) {
                        SearchHelperUi(
                            modifier = Modifier,
                            state = state,
                            onEvent = onEvent,
                        )
                    } else {
                        PhotosGridUi(
                            photos = state.photos,
                            listPhotosState = listPhotosState,
                            isNoData = state.isNoData,
                            isLoadingMore = state.isLoading,
                            hasError = state.hasError,
                            orientation = state.orientation,
                            size = state.size,
                            color = state.color,
                            sharedScope = LocalSharePhotoSearchScope.current,
                            animatedScope = LocalAnimatedPhotoSearchScope.current,
                            onRefresh = {
                                onEvent(PhotoSearchContract.Event.OnRefresh)
                            },
                            onRetry = {
                                onEvent(PhotoSearchContract.Event.OnRetry)
                            },
                            onLoadMore = {
                                onEvent(PhotoSearchContract.Event.OnLoadMore)
                            },
                            onPhotoClick = { index ->
                                onEvent(PhotoSearchContract.Event.OnPhotoDetailVisibilityChanged(true, index))
                            },
                            onApplyChanges = { orientation, size, color ->
                                onEvent(PhotoSearchContract.Event.OnApplyChanges(orientation, size, color))
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchHelperUi(
    modifier: Modifier = Modifier,
    state: PhotoSearchContract.State,
    onEvent: (PhotoSearchContract.Event) -> Unit,
    onEffect: (PhotoSearchContract.Effect) -> Unit = {},
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var isShow by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isShow = true
    }
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        AnimatedVisibility(
            visible = isShow,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut(),
        ) {
            Suggestion(
                suggestions = state.listSuggestions,
                onItemClick = {
                    onEvent(PhotoSearchContract.Event.OnValueChanged((it)))
                    keyboard?.hide()
                    focusManager.clearFocus()
                },
            )
        }
        AnimatedVisibility(
            visible = isShow && state.listHistories.isNotEmpty(),
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut(),
        ) {
            SearchHistory(
                histories = state.listSuggestedKeywords.ifEmpty { state.listHistories },
                onItemClick = {
                    onEvent(PhotoSearchContract.Event.OnValueChanged((it.keyword)))
                    keyboard?.hide()
                    focusManager.clearFocus()
                },
                onDeleteHistory = {
                    onEvent(PhotoSearchContract.Event.OnDeleteSearchHistory(it.id))
                },
            )
        }
    }
}

@Composable
fun SearchHistory(
    histories: List<SearchHistoryModel> = emptyList(),
    onItemClick: (SearchHistoryModel) -> Unit = {},
    onDeleteHistory: (SearchHistoryModel) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            "Search history",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            ),
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            contentPadding = PaddingValues(bottom = 12.dp),

            ) {
            items(histories.take(10)) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clickable {
                                onItemClick(it)
                            }
                            .padding(
                                vertical = 4.dp,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_clock),
                            modifier = Modifier.clip(CircleShape),
                            contentDescription = null,
                            tint = Color.DarkGray,
                        )
                        Text(
                            text = it.keyword,
                            modifier = Modifier.weight(1f),
                            style = TextStyle(
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                        Icon(
                            Icons.Rounded.Clear,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    onDeleteHistory(it)
                                },
                            contentDescription = null,
                            tint = Color.DarkGray,
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.LightGray.copy(0.2F)),
                    )
                }
            }
        }
    }
}

@Composable
fun Suggestion(
    suggestions: List<String> = emptyList(),
    onItemClick: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            "Suggestions",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            ),
        )
        Spacer(Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            (suggestions).onEach {
                Text(
                    text = it, style = TextStyle(
                    fontWeight = FontWeight.Medium,
                ), modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            onItemClick(it)
                        }
                        .border(1.dp, Color.LightGray, CircleShape)
                        .padding(horizontal = 14.dp, vertical = 8.dp))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    text: String = "",
    query: String = "",
    isAutoFocused: Boolean = true,
    onFocused: (Boolean) -> Unit = {},
    onValueChange: (String) -> Unit = {},
    onValueChangeDebounce: (String) -> Unit = {},
    onSearch: (String) -> Unit = {},
) {
    val sharedScope = LocalSharedNavScope.current
    val animatedScope = LocalAnimatedNavScope.current

    var value by remember(text) { mutableStateOf(text) }
    var isFocused by remember(isAutoFocused) { mutableStateOf(isAutoFocused) }

    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    val coroutineScope = rememberCoroutineScope()
    var job: Job? by remember { mutableStateOf(null) }

    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var isShowHint by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        isShowHint = true
        if (query.isEmpty()) {
            delay(300)
            focusRequester.requestFocus()
        }
    }

    Row(
        modifier = modifier
            .background(if (isFocused) Color.LightGray.copy(0.2F) else Color.White, CircleShape)
            .border(1.dp, if (isFocused) Color.LightGray else Color.LightGray.copy(0.5F), CircleShape),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                    onFocused(focusState.isFocused)
                },
            value = value,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = if (value.isNotEmpty()) ImeAction.Search else ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onSearch = {
                keyboard?.hide()
                focusManager.clearFocus()
                onSearch(value)
            }, onDone = {
                keyboard?.hide()
                focusManager.clearFocus()
            }),
            textStyle = TextStyle(),
            onValueChange = {
                value = it
                onValueChange(it)
                job?.cancel()
                job = coroutineScope.launch {
                    delay(1000)
                    onValueChangeDebounce(it)
                }
            },
            decorationBox = { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    contentPadding = PaddingValues(0.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = Color.Black,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                    placeholder = {
                        AnimatedVisibility(
                            visible = isShowHint,
                            enter = fadeIn() + slideInVertically { it / 2 },
                            exit = fadeOut(),
                        ) {
                            Text(
                                "Find free photos",
                                style = TextStyle(),
                                color = Color.Gray,
                                modifier = Modifier,
                            )
                        }
                    },
                )
            },
            singleLine = true,
        )
        AnimatedVisibility(
            value.isNotEmpty(),
            enter = fadeIn(tween(durationMillis = 250, easing = LinearEasing)),
            exit = fadeOut(tween(durationMillis = 250, easing = LinearEasing)),
            modifier = Modifier.offset(x = 8.dp)
        ) {
            Icon(
                Icons.Rounded.Clear,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable {
                        value = ""
                        onValueChange(value)
                        onValueChangeDebounce(value)
                        focusRequester.requestFocus()
                    }
                    .padding(10.dp),
                contentDescription = null,
                tint = Color.DarkGray,
            )
        }
        with(sharedScope) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable {
                        keyboard?.hide()
                        focusManager.clearFocus()
                        onSearch(value)
                    }
                    .padding(11.dp),
            ) {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = null,
                    modifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(SharedTransitionKey.PhotoSearch.IMAGE),
                        animatedVisibilityScope = animatedScope,
                    ),
                    tint = Color.DarkGray,
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun SearchTextFieldPreview() {
    SearchTextField(
        text = "Ocean",
    )
}

@Preview
@Composable
fun PhotoSearchUiPreview() {
    PhotoSearchUi(
        modifier = Modifier,
        state = PhotoSearchContract.State(
            listSuggestions = DataProvider.listSuggestions,
            listHistories = DataProvider.listSuggestions.map { SearchHistoryModel(keyword = it) },
        ),
    )
}