package com.huyhieu.library.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun <T> LazyVerticalStaggeredGridLoadMore(
    modifier: Modifier = Modifier,
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(6.dp),
    itemList: List<T>,
    isLoadingMore: Boolean,
    hasError: Boolean,
    onLoadMore: () -> Unit,
    itemLoading: @Composable () -> Unit = {},
    itemError: @Composable () -> Unit = {},
    itemContent: @Composable (Int, T) -> Unit,
) {

    LazyVerticalStaggeredGrid(
        state = listState,
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = contentPadding,
        modifier = modifier,
    ) {
        itemsIndexed(itemList) { index, item ->
            itemContent(index, item)
        }
        if (isLoadingMore) {
            item {
                itemLoading()
            }
        }
        if (hasError) {
            item {
                itemError()
            }
        }
    }

    // Trigger load more
    LaunchedEffect(listState, isLoadingMore, hasError) {
        runCatching {
            snapshotFlow {
                listState.layoutInfo
            }.map { layoutInfo ->
                layoutInfo.visibleItemsInfo.lastOrNull()?.index
            }.distinctUntilChanged()
                .collectLatest { lastVisibleIndex ->
                    val totalItemsCount = itemList.size
                    if (
                        lastVisibleIndex != null
                        && lastVisibleIndex >= totalItemsCount - 3
                        && !isLoadingMore
                        && !hasError
                    ) {
                        onLoadMore()
                    }
                }
        }
    }
}