package com.huyhieu.library.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

private val current get() = System.currentTimeMillis()
private var time = current

fun Modifier.awaitClickable(duration: Long = 500L, onClick: () -> Unit) = clickable {
    val over = current - time
    if (over > duration) {
        onClick()
        time = current
    }
}