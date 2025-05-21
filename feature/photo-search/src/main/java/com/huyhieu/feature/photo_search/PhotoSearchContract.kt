package com.huyhieu.feature.photo_search

import com.huyhieu.base.contract.Contract
import com.huyhieu.domain.model.PhotoModel
import com.huyhieu.domain.model.SearchHistoryModel
import com.huyhieu.library.ui.model.ItemState


sealed interface PhotoSearchContract {
    data class State(
        val text: String = "",
        val isFocused: Boolean = true,

        val listSuggestedKeywords: List<SearchHistoryModel> = emptyList(),
        val listHistories: List<SearchHistoryModel> = emptyList(),
        val listSuggestions: List<String> = emptyList(),

        val query: String = "",
        val page: Int = 1,
        val isLoading: Boolean = false,
        val photos: List<ItemState<PhotoModel>> = emptyList(),

        val isNoData: Boolean = true,
        val hasError: Boolean = false,

        val isPhotoDetailVisible: Boolean = false,
        val photoIndex: Int = 0,

        val orientation: Int = -1,
        val size: Int = -1,
        val color: Int = -1,

        val isNoConnection: Boolean = false,
    ) : Contract.State

    sealed interface Event : Contract.Event {
        data object OnInitial : Event
        data class OnFocusChanged(val isFocused: Boolean) : Event
        data class OnValueChanged(val value: String) : Event
        data class OnSearchPhoto(val query: String, val page: Int) : Event
        data object OnRefresh : Event
        data object OnRetry : Event
        data object OnLoadMore : Event
        data class OnPhotoDetailVisibilityChanged(val visible: Boolean, val index: Int) : Event
        data class OnNoConnectionVisibilityChanged(val visible: Boolean) : Event

        data class OnInsertSearchHistory(val query: String) : Event
        data class OnDeleteSearchHistory(val id: Int) : Event
        data object OnGetSearchHistory : Event
        data class OnGetSuggestedKeyword(val query: String, val isReload: Boolean = false) : Event
        data class OnApplyChanges(val orientation: Int, val size: Int, val color: Int) : Event
    }

    sealed interface Effect : Contract.Effect {
        data object PopBackStack : Effect
        data object ScrollToTop : Effect
    }
}