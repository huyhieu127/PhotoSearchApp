package com.huyhieu.data.remote.retrofit

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class NetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val requestBuilder: Request.Builder = request.newBuilder()
        requestBuilder.apply {
            url(chain.request().url.toString())
            addHeader("Content-Type", "application/json;charset=UTF-8")
        }
        return chain.proceed(requestBuilder.build())
    }
}