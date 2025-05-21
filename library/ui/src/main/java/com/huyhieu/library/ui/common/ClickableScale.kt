package com.huyhieu.library.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ClickableScale(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    targetScale: Float = 0.8f,
    clip: Shape = RectangleShape,
    content: @Composable BoxScope.() -> Unit,
) {
    var pressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (pressed) targetScale else 1f,
        animationSpec = tween(150),
        label = "click-scale"
    )

    Box(
        modifier = Modifier
            .clip(clip)
            .scale(scale)
            .then(modifier)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try {
                            awaitRelease()
                        } finally {
                            pressed = false
                            onClick()
                        }
                    },
                    onTap = {
                        scope.launch {
                            pressed = true
                            delay(150)
                            pressed = false
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center,
        content = content
    )
}
