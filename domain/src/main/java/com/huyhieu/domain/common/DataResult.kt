package com.huyhieu.domain.common

sealed class DataResult<out T> {
    class Loading<T> : DataResult<T>()
    class Success<T>(val data: T) : DataResult<T>()
    class Error<T>(val code: Int = 0, val message: String = "") : DataResult<T>()
    class Complete<T> : DataResult<T>()
}

enum class ResultError(val code: Int) {
    NO_CONNECTION(1001),
    UNKNOWN(1002),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
}