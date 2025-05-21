package com.huyhieu.library.ui.common.zoomable_image

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huyhieu.library.ui.R
import com.huyhieu.library.ui.common.ClickableScale
import com.huyhieu.library.ui.common.Shadowed
import com.huyhieu.library.ui.theme.Sky
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@Preview
@Composable
fun ZoomableImageControl(
    modifier: Modifier = Modifier,
    scale: Float = 1F,
    isZoomCrop: Boolean = true,
    isZoomIn: Boolean = false,
    containerColor: Color = Color.Black,
    delay: Long = 600L,
    onZoomCropClick: () -> Unit = {},
    onZoomClick: () -> Unit = {},
) {
    var isShow by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delay)
        isShow = true
    }


    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = isShow,
//            enter = fadeIn(animationSpec = TweenSpec(delay = 150)) + slideInVertically(animationSpec = TweenSpec(delay = 150)) { (it) },
            enter = fadeIn() + slideInVertically { (it) },
            exit = fadeOut() + slideOutVertically { (it) },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd),
        ) {
            Row(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                containerColor.copy(0.9F),
                            )
                        )
                    )
                    .padding(12.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${String.format("%.1f", scale)}X", modifier = Modifier.padding(12.dp), style = TextStyle(
                        color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, shadow = Shadow(
                            color = Color.Gray.copy(0.5F), blurRadius = 5f
                        )
                    )
                )
                Spacer(modifier = Modifier.weight(1F))
                ClickableScale(
                    onClick = onZoomCropClick,
                    modifier = Modifier.padding(12.dp),
                ) {
                    Shadowed {
                        Icon(
                            painter = painterResource(R.drawable.ic_aspect_ratio),
                            contentDescription = null,
                            tint = if (isZoomCrop) Sky else Color.White,
                        )
                    }
                }
                AnimatedContent(
                    targetState = isZoomIn,
                    modifier = Modifier
                        .clickable(
                            indication = null, interactionSource = remember { MutableInteractionSource() }) {
                            onZoomClick()
                        }
                        .padding(12.dp),
                    label = "zoom_control",
                    transitionSpec = {
                        scaleIn(
                            animationSpec = tween(300), initialScale = 0.1f
                        ) togetherWith scaleOut(
                            animationSpec = tween(300), targetScale = 0.0f
                        )
                    },
                ) {
                    if (it) {
                        Shadowed {
                            Icon(
                                painter = painterResource(R.drawable.ic_zoom_in),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier,
                            )
                        }
                    } else {
                        Shadowed {
                            Icon(
                                painter = painterResource(R.drawable.ic_zoom_out),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier,
                            )
                        }
                    }
                }
            }
        }
    }
}