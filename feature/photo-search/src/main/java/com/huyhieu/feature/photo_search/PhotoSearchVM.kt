package com.huyhieu.feature.photo_search

import androidx.lifecycle.viewModelScope
import com.huyhieu.base.BaseVm
import com.huyhieu.core.network.network_monitor.NetworkMonitor
import com.huyhieu.domain.common.ResultError
import com.huyhieu.domain.model.PhotoModel
import com.huyhieu.domain.model.SearchHistoryModel
import com.huyhieu.domain.usecase.DeleteSearchHistoryByIdDbUsecase
import com.huyhieu.domain.usecase.GetSearchHistoryDbUsecase
import com.huyhieu.domain.usecase.GetSuggestedKeywordUsecase
import com.huyhieu.domain.usecase.InsertSearchHistoryDbUsecase
import com.huyhieu.domain.usecase.SearchPhotosUsecase
import com.huyhieu.library.ui.model.ItemState
import com.huyhieu.library.ui.shared.DataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val NUM_TAKE_HISTORY = 15

@HiltViewModel
class PhotoSearchVM @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val searchPhotoUseCase: SearchPhotosUsecase,
    private val insertSearchHistoryUseCase: InsertSearchHistoryDbUsecase,
    private val getSuggestedKeywordUsecase: GetSuggestedKeywordUsecase,
    private val deleteSearchHistoryUseCase: DeleteSearchHistoryByIdDbUsecase,
    private val getSearchHistoryUseCase: GetSearchHistoryDbUsecase,
) : BaseVm<PhotoSearchContract.State, PhotoSearchContract.Event, PhotoSearchContract.Effect>(PhotoSearchContract.State()) {

    val isNetworkConnection get() = networkMonitor.isConnected()

    override fun onEvent(event: PhotoSearchContract.Event) {
        when (event) {
            is PhotoSearchContract.Event.OnInitial -> {
                loadInitial()
            }

            is PhotoSearchContract.Event.OnFocusChanged -> {
                updateState { copy(isFocused = event.isFocused) }
            }

            is PhotoSearchContract.Event.OnValueChanged -> {
                searchNewPhotos(event.value)
            }

            is PhotoSearchContract.Event.OnSearchPhoto -> {
                searchPhoto(event.query, event.page)
            }

            is PhotoSearchContract.Event.OnLoadMore -> {
                loadMore()
            }

            is PhotoSearchContract.Event.OnRetry -> {
                retry()
            }

            is PhotoSearchContract.Event.OnRefresh -> {
                refresh()
            }

            is PhotoSearchContract.Event.OnPhotoDetailVisibilityChanged -> {
                changePhotoDetailVisibility(event.visible, event.index)
            }

            is PhotoSearchContract.Event.OnNoConnectionVisibilityChanged -> {
                updateState { copy(isNoConnection = event.visible) }
            }

            is PhotoSearchContract.Event.OnDeleteSearchHistory -> {
                deleteSearchHistoryById(event.id)
                onEvent(PhotoSearchContract.Event.OnGetSuggestedKeyword(state.value.query, true))
            }

            PhotoSearchContract.Event.OnGetSearchHistory -> {
                getSearchHistory()
            }

            is PhotoSearchContract.Event.OnGetSuggestedKeyword -> {
                getSuggestedKeyword(event.query)
            }

            is PhotoSearchContract.Event.OnInsertSearchHistory -> {
                insertSearchHistory(event.query)
            }

            is PhotoSearchContract.Event.OnApplyChanges -> {
                filter(event.orientation, event.size, event.color)
            }
        }
    }

    private fun getSuggestedKeyword(query: String) {
        updateState { copy(text = query) }
        viewModelScope.launch {
            val result = getSuggestedKeywordUsecase(query, NUM_TAKE_HISTORY)
            updateState {
                copy(listSuggestedKeywords = result)
            }
        }
    }

    private fun insertSearchHistory(query: String) {
        viewModelScope.launch {
            insertSearchHistoryUseCase(SearchHistoryModel(keyword = query))
        }

    }

    private fun deleteSearchHistoryById(id: Int) {
        viewModelScope.launch {
            deleteSearchHistoryUseCase(id)
        }
    }

    private fun getSearchHistory() {
        viewModelScope.launch {
            getSearchHistoryUseCase(NUM_TAKE_HISTORY).collect {
                updateState { copy(listHistories = it) }
            }
        }

    }

    private fun searchNewPhotos(value: String) {
        updateState { copy(text = value) }
        if (value.isNotEmpty()) {
            if (value != state.value.query) {
                updateState {
                    copy(
                        isFocused = false,
                        query = value,
                        page = 1,
                    )
                }
            } else {
                onEvent(PhotoSearchContract.Event.OnInsertSearchHistory(value))
            }
        }
    }

    private fun changePhotoDetailVisibility(visible: Boolean, index: Int) {
        updateState {
            copy(
                isPhotoDetailVisible = visible,
                photoIndex = index,
            )
        }
    }

    private fun loadInitial() {
        val suggestions = DataProvider.listSuggestions
        updateState { copy(listSuggestions = suggestions) }
        onEvent(PhotoSearchContract.Event.OnGetSearchHistory)
    }

    private fun searchPhoto(query: String, page: Int) {
        if (query.isEmpty()) return
        val orientation = if (state.value.orientation == -1) "" else DataProvider.listOrientations[state.value.orientation].lowercase()
        val size = if (state.value.size == -1) "" else DataProvider.listSizes[state.value.size].lowercase()
        val color = if (state.value.color == -1) "" else "#${DataProvider.listColors[state.value.color]}"
        searchPhotoUseCase(
            query = query,
            page = state.value.page,
            perPage = 15,
            orientation = orientation,
            size = size,
            color = color,
        ).collectDataVM(
            onLoading = {
                if (it) {
                    if (page == 1 && state.value.photos.isNotEmpty()) {
                        updateState {
                            copy(
                                isLoading = true,
                                hasError = false,
                                photos = emptyList(),
                                isNoData = false,
                            )
                        }
                        onEffect(PhotoSearchContract.Effect.ScrollToTop)
                    } else {
                        updateState {
                            copy(
                                isLoading = true,
                                hasError = false,
                                isNoData = false,
                            )
                        }
                    }
                } else {
                    updateState { copy(isLoading = false) }
                }
            },
            onSuccess = {
                if (page == 1) {
                    updateState {
                        copy(
                            isNoData = it.photos.isEmpty(),
                            photos = it.photos.map { photo -> photo.toItemState() },
                        )
                    }
                } else {
                    updateState {
                        copy(
                            photos = photos + it.photos.map { photo -> photo.toItemState() },
                        )
                    }
                }
                onEvent(PhotoSearchContract.Event.OnInsertSearchHistory(query))
            },
            onError = { code, msg ->
                if (code == ResultError.NO_CONNECTION.code) {
                    updateState { copy(isNoConnection = true) }
                }
                updateState {
                    copy(hasError = true)
                }
            },
        )
    }

    private fun loadMore() {
        if (!state.value.isLoading) {
            if (isNetworkConnection) {
                updateState {
                    copy(
                        isLoading = true,
                        hasError = false,
                        page = page + 1,
                    )
                }
            } else {
                updateState {
                    copy(
                        isNoConnection = true,
                        hasError = true,
                    )
                }
            }
        }
    }

    private fun retry() {
        if (isNetworkConnection) {
            if (state.value.photos.isEmpty()) {
                onEvent(PhotoSearchContract.Event.OnSearchPhoto(state.value.query, state.value.page))
            } else {
                onEvent(PhotoSearchContract.Event.OnLoadMore)
            }
        } else {
            updateState { copy(isNoConnection = true) }
        }
    }

    private fun refresh() {
        onEvent(PhotoSearchContract.Event.OnSearchPhoto(state.value.query, 1))
    }

    private fun filter(orientation: Int, size: Int, color: Int) {
        updateState { copy(orientation = orientation, size = size, color = color) }
        searchPhoto(state.value.query, 1)
    }

    private fun PhotoModel.toItemState(): ItemState<PhotoModel> {
        val wOrigin = 800
        val hOrigin = 1200
        val hMin = 500
        val newHeight = (hMin..hOrigin).random()
        //val url = if (newHeight < wOrigin) src.landscape else src.portrait
        val url = src.medium

        return ItemState(
            width = wOrigin,
            height = newHeight,
            url = url,
            data = this,
        )
    }
}