package com.huyhieu.hilt

import com.huyhieu.data.repository.DatabaseRepositoryImpl
import com.huyhieu.data.repository.PixelsRepositoryImpl
import com.huyhieu.domain.repository.DatabaseRepository
import com.huyhieu.domain.repository.PixelsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindPixelsRepository(pixelsRepositoryImpl: PixelsRepositoryImpl): PixelsRepository

    @Binds
    @Singleton
    fun bindDatabaseRepository(databaseRepositoryImpl: DatabaseRepositoryImpl): DatabaseRepository
}