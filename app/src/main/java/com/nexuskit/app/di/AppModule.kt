package com.nexuskit.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton @Named("IoDispatcher")
    fun provideIo(): CoroutineDispatcher = Dispatchers.IO

    @Provides @Singleton @Named("MainDispatcher")
    fun provideMain(): CoroutineDispatcher = Dispatchers.Main

    @Provides @Singleton @Named("DefaultDispatcher")
    fun provideDefault(): CoroutineDispatcher = Dispatchers.Default
}
