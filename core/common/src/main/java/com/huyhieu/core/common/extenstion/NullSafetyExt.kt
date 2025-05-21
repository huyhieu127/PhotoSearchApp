package com.huyhieu.core.common.extenstion

fun Boolean?.orFalse() = this ?: false

fun Int?.orZero() = this ?: 0

fun Float?.orZero() = this ?: 0F

fun Double?.orZero() = this ?: 0.0

inline fun <T, R> withOrNull(receiver: T?, block: T.() -> R): R? {
    receiver ?: return null
    with(receiver) {
        return block()
    }
}