package com.serova.parkingapp.di

import android.content.Context
import androidx.room.Room
import com.serova.parkingapp.data.local.cache.bookings.BookingsCache
import com.serova.parkingapp.data.local.cache.bookings.BookingsCacheImpl
import com.serova.parkingapp.data.local.cache.personaldata.PersonalDataCache
import com.serova.parkingapp.data.local.cache.personaldata.PersonalDataCacheImpl
import com.serova.parkingapp.data.local.database.AppDatabase
import com.serova.parkingapp.data.local.database.dao.BookingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "parking-db").build()

    @Provides
    fun provideBookingDao(
        database: AppDatabase
    ): BookingDao = database.bookingDao()

    @Provides
    @Singleton
    fun providePersonalDataCache(
        impl: PersonalDataCacheImpl
    ): PersonalDataCache = impl

    @Provides
    @Singleton
    fun provideBookingsCache(
        impl: BookingsCacheImpl
    ): BookingsCache = impl
}