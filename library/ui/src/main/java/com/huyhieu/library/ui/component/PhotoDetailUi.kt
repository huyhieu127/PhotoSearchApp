package com.huyhieu.library.ui.component

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.huyhieu.domain.model.PhotoModel
import com.huyhieu.library.ui.R
import com.huyhieu.library.ui.common.zoomable_image.ZoomableImageCenterViewer
import com.huyhieu.library.ui.dialog.NoInternetConnectionDialog
import com.huyhieu.library.ui.model.ItemState
import com.huyhieu.library.ui.shared.LocalImageListLoader
import com.huyhieu.library.ui.shared.LocalImageOriginLoader
import com.huyhieu.library.ui.shared.SharedTransitionKey
import com.huyhieu.library.ui.util.awaitClickable
import com.huyhieu.library.ui.util.toColor
import kotlinx.coroutines.delay

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PhotoDetailUi(
    modifier: Modifier = Modifier,
    photoState: ItemState<PhotoModel>,
    index: Int,
    isNetworkConnection: Boolean,
    sharedScope: SharedTransitionScope,
    animatedScope: AnimatedContentScope,
    onNavBack: () -> Unit = {},
) {
    val imageLoader = LocalImageListLoader.current
    val imageLoaderOrigin = LocalImageOriginLoader.current

    var isShowNoInternet by remember(isNetworkConnection) { mutableStateOf(!isNetworkConnection) }

    val photo = photoState.data
    val avgColor = remember(photo.avgColor) { photo.avgColor.toColor() }
    val id = remember(photo.id) { photo.id }
    var isLoading by remember { mutableStateOf(false) }
    var stateClose by remember { mutableIntStateOf(0) }

    val onBack = { isTarget: Boolean ->
        if (stateClose == 1 && isTarget) {
            stateClose = 2
            onNavBack()
        }
    }
    BackHandler {
        onBack(true)
    }

    LaunchedEffect(stateClose == 0) {
        delay(200L)
        stateClose = 1
    }

    with(sharedScope) {
        Box(
            modifier = Modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = SharedTransitionKey.PhotoDetail.ITEM_BOUNDS),
                    animatedVisibilityScope = animatedScope,
                )
                .fillMaxSize()
                .background(avgColor.copy(alpha = 0.25F))
                .safeContentPadding()
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "${SharedTransitionKey.PhotoDetail.PHOTO}${id}"),
                            animatedVisibilityScope = animatedScope,
                        )
                        .weight(1F)
                ) {
                    ZoomableImageCenterViewer(
                        model = photoState.data.src.original,
                        imageLoader = imageLoaderOrigin,
                        containerControlColor = avgColor,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(avgColor),
                        loading = {
                            isLoading = true
                            SubcomposeAsyncImage(
                                photoState.url,
                                imageLoader = imageLoader,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                success = {
                                    AsyncImage(
                                        photoState.url,
                                        imageLoader = imageLoader,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                    )
                                },
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
                                }
                            )
                        },
                        onSuccess = {
                            var isInitialed by remember { mutableStateOf(false) }
                            LaunchedEffect(isInitialed) {
                                delay(300L)
                                isInitialed = true
                                isLoading = false
                            }
                            if (!isInitialed) {
                                AsyncImage(
                                    photoState.url,
                                    imageLoader = imageLoader,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        },
                        error = {
                            SubcomposeAsyncImage(
                                photoState.url,
                                imageLoader = imageLoader,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                success = {
                                    AsyncImage(
                                        photoState.url,
                                        imageLoader = imageLoader,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                    )
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
                                }
                            )
                            if (isLoading) {
                                Toast.makeText(LocalContext.current, "Error loading high-resolution image!", Toast.LENGTH_SHORT).show()
                            }
                            isLoading = false
                        },
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1F)

                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(
                                    painterResource(R.drawable.avatar),
                                    modifier = Modifier
                                        .sharedElement(
                                            sharedContentState = rememberSharedContentState(key = "${SharedTransitionKey.PhotoDetail.AVATAR}${id}"),
                                            animatedVisibilityScope = animatedScope,
                                        )
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentDescription = null,
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    photo.photographer,
                                    color = avgColor,
                                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                    modifier = Modifier
                                        .sharedElement(
                                            sharedContentState = rememberSharedContentState(key = "${SharedTransitionKey.PhotoDetail.PHOTOGRAPHER}${id}"),
                                            animatedVisibilityScope = animatedScope,
                                        )
                                        .weight(1F),
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 2,
                                )
                            }
                        }
                        AnimatedVisibility(
                            visible = stateClose == 1,
                            enter = fadeIn() + slideInVertically { (it) },
                            exit = fadeOut() + slideOutVertically { (it) },
                            modifier = Modifier,
                        ) {
                            val context = LocalContext.current
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = avgColor,
                                ),
                            ) {
                                Text(text = "Download")
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = stateClose == 1,
                        enter = fadeIn(animationSpec = TweenSpec(delay = 150)) + slideInVertically(animationSpec = TweenSpec(delay = 150)) { (it) },
                        exit = fadeOut() + slideOutVertically { (it) },
                        modifier = Modifier.padding(
                            top = 12.dp,
                            bottom = 16.dp,
                        ),
                    ) {
                        Text(
                            text = photo.alt,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 4,
                            //
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = isLoading,
                modifier = Modifier
                    .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 1F)
                    .align(Alignment.TopCenter)
                    .padding(top = 50.dp)
                    .size(30.dp),
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { -it }) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .shadow(2.dp)
                        .background(Color.White)
                        .padding(6.dp),
                    strokeWidth = 4.dp,
                )
            }
            AnimatedVisibility(
                visible = stateClose == 1, enter = scaleIn(
                    animationSpec = TweenSpec(
                        delay = 350,
                        durationMillis = 500,
                        easing = EaseOutBounce,
                    )
                ), exit = scaleOut(
                    animationSpec = TweenSpec(durationMillis = 200)
                ), modifier = Modifier
                    .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 1F)
                    .align(Alignment.TopEnd)
                    .padding(28.dp)
            ) {
                Icon(
                    Icons.Rounded.Close, contentDescription = null, tint = Color.White, modifier = Modifier
                        .clip(CircleShape)
                        .awaitClickable(800) {
                            val isTarget = transition.currentState == transition.targetState
                            onBack(isTarget)
                        }
                        .background(Color.Black.copy(0.5F))
                        .padding(10.dp))
            }
        }
    }

    AnimatedVisibility(
        visible = isShowNoInternet,
    ) {
        NoInternetConnectionDialog(
            onDismiss = {
                isShowNoInternet = false
            },
            onConfirm = {
                isShowNoInternet = false
            },
        )
    }
}