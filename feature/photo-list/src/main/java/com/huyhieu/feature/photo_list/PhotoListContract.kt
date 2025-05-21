package com.huyhieu.feature.photo_list

import com.huyhieu.base.contract.Contract
import com.huyhieu.domain.model.PhotoModel
import com.huyhieu.library.ui.model.ItemState

sealed interface PhotoListContract {
    data class State(
        val page: Int = 0,
        val query: String = "",
        val isLoading: Boolean = false,
        val photos: List<ItemState<PhotoModel>> = emptyList(),
        val hasError: Boolean = false,
        val isPhotoDetailVisible: Boolean = false,
        val photoIndex: Int = 0,

        val orientation: Int = -1,
        val size: Int = -1,
        val color: Int = -1,

        val isEndReached: Boolean = false,

        val isNoConnection: Boolean = false,
    ) : Contract.State

    sealed interface Event : Contract.Event {
        data class OnSuggestionQuery(val query: String) : Event
        data class OnSearchPhoto(val query: String, val page: Int = 1) : Event
        data object OnRefresh : Event
        data object OnRetry : Event
        data object OnLoadMore : Event
        data class OnPhotoDetailVisibilityChanged(val visible: Boolean, val index: Int) : Event
        data class OnNoConnectionVisibilityChanged(val visible: Boolean) : Event
        data class OnApplyChanges(val orientation: Int, val size: Int, val color: Int) : Event
    }

    sealed interface Effect : Contract.Effect {
        data object PopBackStack : Effect
        data object NavToSearch : Effect
    }
}