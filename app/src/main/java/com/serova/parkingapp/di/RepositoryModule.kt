package com.serova.parkingapp.di

import com.serova.parkingapp.data.repository.AuthRepositoryImpl
import com.serova.parkingapp.data.repository.BookingRequestRepositoryImpl
import com.serova.parkingapp.data.repository.BookingsDetailsRepositoryImpl
import com.serova.parkingapp.data.repository.BookingsRepositoryImpl
import com.serova.parkingapp.data.repository.LanguageRepositoryImpl
import com.serova.parkingapp.data.repository.LogoutRepositoryImpl
import com.serova.parkingapp.data.repository.PersonalDataRepositoryImpl
import com.serova.parkingapp.data.repository.ThemeRepositoryImpl
import com.serova.parkingapp.data.repository.TokenRepositoryImpl
import com.serova.parkingapp.domain.repository.AuthRepository
import com.serova.parkingapp.domain.repository.BookingRequestRepository
import com.serova.parkingapp.domain.repository.BookingsDetailsRepository
import com.serova.parkingapp.domain.repository.BookingsRepository
import com.serova.parkingapp.domain.repository.LanguageRepository
import com.serova.parkingapp.domain.repository.LogoutRepository
import com.serova.parkingapp.domain.repository.PersonalDataRepository
import com.serova.parkingapp.domain.repository.ThemeRepository
import com.serova.parkingapp.domain.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTokenRepository(
        impl: TokenRepositoryImpl
    ): TokenRepository = impl

    @Provides
    @Reusable
    fun provideAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository = impl

    @Provides
    @Reusable
    fun providePersonalDataRepository(
        impl: PersonalDataRepositoryImpl
    ): PersonalDataRepository = impl

    @Provides
    @Reusable
    fun provideLogoutRepository(
        impl: LogoutRepositoryImpl
    ): LogoutRepository = impl

    @Provides
    @Singleton
    fun provideLanguageRepository(
        impl: LanguageRepositoryImpl
    ): LanguageRepository = impl

    @Provides
    @Singleton
    fun provideThemeRepository(
        impl: ThemeRepositoryImpl
    ): ThemeRepository = impl

    @Provides
    @Reusable
    fun provideBookingsRepository(
        impl: BookingsRepositoryImpl
    ): BookingsRepository = impl

    @Provides
    @Reusable
    fun provideBookingRequestRepository(
        impl: BookingRequestRepositoryImpl
    ): BookingRequestRepository = impl

    @Provides
    @Reusable
    fun provideBookingsDetailsRepository(
        impl: BookingsDetailsRepositoryImpl
    ): BookingsDetailsRepository = impl
}