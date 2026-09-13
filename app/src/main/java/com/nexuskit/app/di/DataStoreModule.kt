package com.nexuskit.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.data.datastore.NexusKitDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * Provides the raw DataStore<Preferences> instance.
     * NexusKitDataStore is auto-injected by Hilt via its @Inject constructor.
     * No explicit @Provides for NexusKitDataStore is needed, but provided for full compatibility.
     */
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile(AppConstants.APP_DATASTORE_NAME)
            }
        )

    @Provides
    @Singleton
    fun provideNexusKitDataStore(
        dataStore: DataStore<Preferences>
    ): NexusKitDataStore = NexusKitDataStore(dataStore)
}
