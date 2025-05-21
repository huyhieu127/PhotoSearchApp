package com.huyhieu.data.di

import com.google.gson.GsonBuilder
import com.huyhieu.core.common.API_TIME_OUT_SECOND
import com.huyhieu.data.BuildConfig
import com.huyhieu.data.remote.retrofit.Authenticator
import com.huyhieu.data.remote.retrofit.PixelsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideAuthenticator() = Authenticator()

    @Provides
    @Singleton
    fun provideOkHttpClient(authenticator: Authenticator): OkHttpClient {
        val builder = OkHttpClient.Builder()

        builder.apply {
            authenticator(authenticator)
            connectTimeout(API_TIME_OUT_SECOND, TimeUnit.SECONDS)
            readTimeout(API_TIME_OUT_SECOND, TimeUnit.SECONDS)
            writeTimeout(API_TIME_OUT_SECOND, TimeUnit.SECONDS)

            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun providePixelsService(okHttpClient: OkHttpClient): PixelsService {
        val builder = GsonBuilder()
        //builder.registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
        val retrofitBuilder = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.pexels.com/")
            .addConverterFactory(GsonConverterFactory.create(builder.create()))
        return retrofitBuilder.build().create(PixelsService::class.java)
    }
}