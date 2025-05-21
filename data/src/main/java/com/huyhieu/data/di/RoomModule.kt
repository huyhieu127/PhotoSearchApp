package com.huyhieu.data.di

import android.app.Application
import com.huyhieu.data.local.room.AppRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun providerRoomDatabase(application: Application) = AppRoomDatabase.getInstance(application)

    @Provides
    @Singleton
    fun providerSearchHistoryDao(appRoomDatabase: AppRoomDatabase) = appRoomDatabase.getSearchHistoryDao()
}