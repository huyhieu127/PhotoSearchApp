package com.huyhieu.feature.photo_list

import com.huyhieu.base.BaseVm
import com.huyhieu.core.network.network_monitor.NetworkMonitor
import com.huyhieu.domain.common.ResultError
import com.huyhieu.domain.model.PhotoModel
import com.huyhieu.domain.usecase.SearchPhotosUsecase
import com.huyhieu.library.ui.model.ItemState
import com.huyhieu.library.ui.shared.DataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotoListVM @Inject constructor(
    private val searchPhotoUseCase: SearchPhotosUsecase,
    private val networkMonitor: NetworkMonitor,
) : BaseVm<PhotoListContract.State, PhotoListContract.Event, PhotoListContract.Effect>(PhotoListContract.State()) {

    val isNetworkConnection get() = networkMonitor.isConnected()

    override fun onEvent(event: PhotoListContract.Event) {
        when (event) {
            is PhotoListContract.Event.OnSuggestionQuery -> {
                searchPhotoSuggestions(event.query)
            }

            is PhotoListContract.Event.OnSearchPhoto -> {
                searchPhoto(event.query, event.page)
            }

            is PhotoListContract.Event.OnLoadMore -> {
                loadMore()
            }

            is PhotoListContract.Event.OnRetry -> {
                retry()
            }

            is PhotoListContract.Event.OnRefresh -> {
                refresh()
            }

            is PhotoListContract.Event.OnPhotoDetailVisibilityChanged -> {
                changePhotoDetailVisibility(event.visible, event.index)
            }

            is PhotoListContract.Event.OnNoConnectionVisibilityChanged -> {
                updateState { copy(isNoConnection = event.visible) }
            }

            is PhotoListContract.Event.OnApplyChanges -> {
                filter(event.orientation, event.size, event.color)
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

    private fun searchPhotoSuggestions(query: String) {
        searchPhoto(query, 1)
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
                updateState {
                    copy(
                        query = query,
                        page = page,
                        isLoading = it,
                        hasError = false,
                    )
                }
            },
            onSuccess = {
                if (page == 1) {
                    updateState { copy(photos = it.photos.map { photo -> photo.toItemState() }) }
                } else {
                    updateState { copy(photos = photos + it.photos.map { photo -> photo.toItemState() }) }
                }
            },
            onError = { code, msg ->
                if (code == ResultError.NO_CONNECTION.code) {
                    updateState { copy(isNoConnection = true) }
                }
                updateState { copy(hasError = true) }
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
                onEvent(PhotoListContract.Event.OnSearchPhoto(state.value.query, state.value.page))
            } else {
                onEvent(PhotoListContract.Event.OnLoadMore)
            }
        } else {
            updateState { copy(isNoConnection = true) }
        }
    }

    private fun refresh() {
        onEvent(PhotoListContract.Event.OnSearchPhoto(state.value.query, 1))
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
