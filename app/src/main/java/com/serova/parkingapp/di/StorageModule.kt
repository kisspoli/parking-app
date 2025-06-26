package com.serova.parkingapp.di

import android.content.Context
import com.serova.parkingapp.data.local.storage.SecureStorage
import com.serova.parkingapp.data.local.storage.SecureStorageImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideSecureStorage(
        @ApplicationContext context: Context
    ): SecureStorage = SecureStorageImpl(context)
}