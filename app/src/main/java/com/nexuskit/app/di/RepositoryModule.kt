package com.nexuskit.app.di

import com.nexuskit.app.data.repository.NoteRepositoryImpl
import com.nexuskit.app.data.repository.PreferencesRepositoryImpl
import com.nexuskit.app.data.repository.ToolRepositoryImpl
import com.nexuskit.app.domain.repository.INoteRepository
import com.nexuskit.app.domain.repository.IPreferencesRepository
import com.nexuskit.app.domain.repository.IToolRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds repository interfaces to their implementations.
 * Must be abstract class (not object) because @Binds requires abstract functions.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindToolRepository(
        impl: ToolRepositoryImpl
    ): IToolRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): IPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        impl: NoteRepositoryImpl
    ): INoteRepository
}
