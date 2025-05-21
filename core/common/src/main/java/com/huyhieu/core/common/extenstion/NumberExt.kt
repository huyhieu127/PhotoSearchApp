package com.huyhieu.core.common.extenstion

import kotlin.random.Random

fun ClosedFloatingPointRange<Float>.random(): Float {
    return Random.nextFloat() * (endInclusive - start) + start
}