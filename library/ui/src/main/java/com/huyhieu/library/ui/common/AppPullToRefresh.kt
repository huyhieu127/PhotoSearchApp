package com.huyhieu.library.ui.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.huyhieu.library.ui.R
import com.huyhieu.library.ui.theme.Sky
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPullToRefresh(
    onRefresh: (() -> Unit),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {

    val localDensity = LocalDensity.current
    var heightPullToRefresh by remember { mutableStateOf(240.dp) }

    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val state = rememberPullToRefreshState()

    val distanceFraction = { state.distanceFraction.coerceIn(0f, 1f) }

    val offset by animateDpAsState(
        targetValue = when {
            isRefreshing -> heightPullToRefresh
            distanceFraction() in 0f..1f -> (distanceFraction() * heightPullToRefresh)
            distanceFraction() > 1f -> (heightPullToRefresh + (((distanceFraction() - 1f) * .1f) * heightPullToRefresh))
            else -> 0.dp
        }, label = "animateDpAsStateOffset"
    )
    Box(
        modifier = modifier.pullToRefresh(
            isRefreshing = isRefreshing,
            state = state,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    delay(50L)
                    isRefreshing = false
                    onRefresh()
                }
            },
        ),
    ) {

        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationY = offset.roundToPx().toFloat()
                },
        ) {
            content()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                //.offset { IntOffset(0, -heightPullToRefresh.roundToPx()) }
                .graphicsLayer {
                    //translationY = offset.roundToPx().toFloat()// + heightPullToRefresh.roundToPx()
                }
                .onGloballyPositioned { coordinates ->
                    heightPullToRefresh = with(localDensity) { coordinates.size.height.toDp() }
                },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier = Modifier.graphicsLayer {
                        val progress = distanceFraction()
                        this.alpha = progress
                        this.scaleX = progress
                        this.scaleY = progress
                    },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painterResource(R.drawable.ic_fan),
                        contentDescription = "Refresh",
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer {
                                val progress = distanceFraction()
                                this.rotationZ = progress * 360
                            },
                        tint = Sky,
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 1.dp)
                            .width(2.dp)
                            .height(6.dp)
                            .background(Sky, CircleShape)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    if (distanceFraction() == 1F) {
                        "Release to refresh"
                    } else {
                        "Pull down to refresh"
                    },
                    color = Color.DarkGray,
                    modifier = Modifier.graphicsLayer {
                        val progress = distanceFraction()
                        this.alpha = progress
                        this.scaleX = progress
                        this.scaleY = progress
                        this.translationY = (-46.dp.roundToPx()).toFloat() + (46.dp.roundToPx()).toFloat() * progress
                    },
                )
            }

        }
    }
}