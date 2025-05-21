package com.huyhieu.library.ui.common.zoomable_image

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.huyhieu.library.ui.util.MAX_SCALE_IMAGE
import com.huyhieu.library.ui.util.MIN_SCALE_IMAGE
import com.huyhieu.library.ui.util.current
import com.huyhieu.library.ui.util.detectTransformGesturesWithEnd
import kotlinx.coroutines.launch

@Composable
fun ZoomableImageSmallViewer(
    model: Any?,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    contentScale: ContentScale = ContentScale.Fit,
    containerControlColor: Color = Color.Black,
) {
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    var rawScale by remember { mutableFloatStateOf(MIN_SCALE_IMAGE) }
    val animatedScale = remember { Animatable(MIN_SCALE_IMAGE) }

    var rawOffset by remember { mutableStateOf(Offset.Zero) }
    val animatedOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    var initialScale by remember { mutableFloatStateOf(MIN_SCALE_IMAGE) }
    val minScale = MIN_SCALE_IMAGE
    val maxScale = MAX_SCALE_IMAGE

    val isZoomedIn by remember(rawScale, initialScale) { mutableStateOf(rawScale > initialScale) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { newContainerSize ->
                    containerSize = newContainerSize
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { tapOffset ->
                            coroutineScope.launch {
                                val zoomedIn = rawScale > initialScale
                                val targetScale = if (zoomedIn) minScale else maxScale

                                val imageScaledWidth = imageSize.width * targetScale
                                val imageScaledHeight = imageSize.height * targetScale

                                val imageTapX = (tapOffset.x - containerSize.width / 2f - rawOffset.x) / rawScale
                                val imageTapY = (tapOffset.y - containerSize.height / 2f - rawOffset.y) / rawScale

                                val newOffsetX = tapOffset.x - containerSize.width / 2f - imageTapX * targetScale
                                val newOffsetY = tapOffset.y - containerSize.height / 2f - imageTapY * targetScale
                                val newOffset = Offset(newOffsetX, newOffsetY)

                                val maxX = ((imageScaledWidth - containerSize.width) / 2f).takeIf { it > 0 } ?: 0f
                                val maxY = ((imageScaledHeight - containerSize.height) / 2f).takeIf { it > 0 } ?: 0f
                                val clampedX = newOffset.x.coerceIn(-maxX, maxX)
                                val clampedY = newOffset.y.coerceIn(-maxY, maxY)

                                coroutineScope.launch {
                                    animatedScale.animateTo(targetScale, animationSpec = spring())
                                }
                                coroutineScope.launch {
                                    animatedOffset.animateTo(Offset(clampedX, clampedY), animationSpec = spring())
                                }

                                rawScale = targetScale
                                rawOffset = Offset(clampedX, clampedY)
                            }
                        })
                }
                .pointerInput(Unit) {
                    detectTransformGesturesWithEnd(
                        onGestureEnd = {
                            coroutineScope.launch {
                                val targetScale = rawScale.coerceIn(minScale, maxScale)

                                val imageScaledWidth = imageSize.width * targetScale
                                val imageScaledHeight = imageSize.height * targetScale

                                val maxX = ((imageScaledWidth - containerSize.width) / 2f).takeIf { it > 0 } ?: 0f
                                val maxY = ((imageScaledHeight - containerSize.height) / 2f).takeIf { it > 0 } ?: 0f

                                val clampedX = rawOffset.x.coerceIn(-maxX, maxX)
                                val clampedY = rawOffset.y.coerceIn(-maxY, maxY)
                                val targetOffset = Offset(clampedX, clampedY)

                                coroutineScope.launch {
                                    animatedScale.animateTo(targetScale, animationSpec = spring())
                                }
                                coroutineScope.launch {
                                    animatedOffset.animateTo(Offset(clampedX, clampedY), animationSpec = spring())
                                }

                                rawScale = targetScale
                                rawOffset = targetOffset
                            }
                        }) { _, pan, zoom, _ ->
                        rawScale *= zoom
                        rawOffset += pan

                        coroutineScope.launch {
                            animatedScale.snapTo(rawScale)
                            animatedOffset.snapTo(rawOffset)
                        }
                    }
                },
        ) {
            AsyncImage(
                model, imageLoader = imageLoader, contentDescription = null, contentScale = contentScale, modifier = Modifier
                    .onSizeChanged { newImageSize ->
                        imageSize = newImageSize
                        if (containerSize.width > 0 && containerSize.height > 0) {
                            val initial = if (containerSize.width > containerSize.height) {
                                containerSize.width.toFloat() / imageSize.width
                            } else {
                                containerSize.height.toFloat() / imageSize.height
                            }
                            initialScale = initial
                            rawScale = initial
                            rawOffset = Offset.Zero

                            coroutineScope.launch {
                                animatedScale.snapTo(initial)
                            }
                            coroutineScope.launch {
                                animatedOffset.snapTo(Offset.Zero)
                            }
                        }
                    }
                    .graphicsLayer(
                        scaleX = animatedScale.value,
                        scaleY = animatedScale.value,
                        translationX = animatedOffset.value.x,
                        translationY = animatedOffset.value.y,
                    )
                    .align(Alignment.Center))
        }
        ZoomableImageControl(
            scale = rawScale.current(MIN_SCALE_IMAGE, MAX_SCALE_IMAGE, minScale, maxScale).coerceIn(MIN_SCALE_IMAGE, MAX_SCALE_IMAGE),
            isZoomCrop = rawScale == initialScale,
            isZoomIn = isZoomedIn,
            containerColor = containerControlColor,
            modifier = Modifier.align(Alignment.BottomEnd),
            onZoomCropClick = {
                val targetScale = if (rawScale != initialScale) {
                    initialScale
                } else {
                    if (isZoomedIn) maxScale else minScale
                }
                rawScale = targetScale
                rawOffset = Offset.Zero
                coroutineScope.launch {
                    animatedScale.animateTo(rawScale, animationSpec = spring())
                }
                coroutineScope.launch {
                    animatedOffset.animateTo(rawOffset, animationSpec = spring())
                }
            },
            onZoomClick = {
                rawScale = if (isZoomedIn) minScale else maxScale
                rawOffset = Offset.Zero
                coroutineScope.launch {
                    animatedScale.animateTo(rawScale, animationSpec = spring())
                }
                coroutineScope.launch {
                    animatedOffset.animateTo(rawOffset, animationSpec = spring())
                }
            },
        )
    }
}