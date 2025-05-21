package com.huyhieu.hilt

import com.huyhieu.data.datasource.DatabaseDatasource
import com.huyhieu.data.datasource.DatabaseDatasourceImpl
import com.huyhieu.data.datasource.PixelsDatasource
import com.huyhieu.data.datasource.PixelsDatasourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DatasourceModule {

    @Binds
    @Singleton
    fun bindPixelsDatasource(pixelsDatasourceImpl: PixelsDatasourceImpl): PixelsDatasource

    @Binds
    @Singleton
    fun bindDatabaseDatasource(databaseDatasourceImpl: DatabaseDatasourceImpl): DatabaseDatasource

}