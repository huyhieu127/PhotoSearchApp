package com.huyhieu.data.mapper

import com.huyhieu.domain.common.DataResult
import com.huyhieu.domain.common.ResultError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class ResultMapper {
    fun <T, R> callApi(
        hasConnection: Boolean,
        api: suspend () -> Response<T>,
        mapper: (T) -> R,
    ) = flow {
        if (!hasConnection) {
            throw UnknownHostException("No internet connection")
        }
        val response = api.invoke()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            val result = mapper.invoke(body)
            emit(DataResult.Success(result))
        } else {
            //val errorBody = response.errorBody()
            emit(DataResult.Error(code = response.code(), message = response.message()))
        }
    }
        .flowOn(Dispatchers.IO)
        .onStart {
            emit(DataResult.Loading())
        }
        .catch { throwable ->
            val errorCode = when (throwable) {
                is SocketTimeoutException, is UnknownHostException -> {
                    ResultError.NO_CONNECTION
                }

                else -> ResultError.UNKNOWN
            }
            emit(DataResult.Error(code = errorCode.code, message = throwable.message ?: "Something went wrong!"))
        }
        .onCompletion {
            emit(DataResult.Complete())
        }
}