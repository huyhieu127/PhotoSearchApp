package com.huyhieu.library.ui.util

const val MIN_SCALE_IMAGE = 1F
const val MAX_SCALE_IMAGE = 5F

fun Float.current(min: Float, max: Float, rawMin: Float, rawMax: Float): Float {
    return min + this.progressScale(rawMin, rawMax) * (max - min)
}

fun Float.progressScale(rawMin: Float, rawMax: Float): Float {
    return (this - rawMin) / (rawMax - rawMin)
}