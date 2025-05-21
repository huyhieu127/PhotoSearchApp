package com.huyhieu.library.ui.util

import androidx.compose.ui.graphics.Color

fun String.toColor(): Color {
    val hex = this.removePrefix("#")
    val colorLong = hex.toLong(16)
    return when (hex.length) {
        6 -> Color(0xFF000000 or colorLong) // Add full alpha
        8 -> Color(colorLong) // Already includes alpha
        else -> Color.Black
    }
}
