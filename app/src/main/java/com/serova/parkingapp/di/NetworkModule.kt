package com.serova.parkingapp.di

import android.content.Context
import com.serova.parkingapp.R
import com.serova.parkingapp.data.api.AuthorizeApi
import com.serova.parkingapp.data.api.AuthorizeByRefreshApi
import com.serova.parkingapp.data.api.BookingRequestApi
import com.serova.parkingapp.data.api.BookingsApi
import com.serova.parkingapp.data.api.BookingsDetailsApi
import com.serova.parkingapp.data.api.LogoutApi
import com.serova.parkingapp.data.api.PersonalDataApi
import com.serova.parkingapp.data.api.interceptor.AppInfoInterceptor
import com.serova.parkingapp.data.api.interceptor.AuthInterceptor
import com.serova.parkingapp.data.api.interceptor.ErrorHandlingInterceptor
import com.serova.parkingapp.data.local.storage.SecureStorage
import com.serova.parkingapp.data.ssl.SSLCertificateHelper
import com.serova.parkingapp.data.ssl.SSLCertificateHelperImpl
import com.serova.parkingapp.domain.repository.AuthRepository
import com.serova.parkingapp.domain.repository.LanguageRepository
import com.serova.parkingapp.domain.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // region Common dependencies

    @Provides
    @Reusable
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Reusable
    fun provideAppInfoInterceptor(
        @ApplicationContext context: Context,
        languageRepository: LanguageRepository
    ): AppInfoInterceptor = AppInfoInterceptor(context, languageRepository)

    @Provides
    @Reusable
    fun provideAuthInterceptor(
        authRepository: AuthRepository,
        tokenRepository: TokenRepository
    ): AuthInterceptor = AuthInterceptor(authRepository, tokenRepository)

    @Provides
    @Reusable
    fun provideErrorHandlingInterceptor(
        json: Json,
        secureStorage: SecureStorage
    ): ErrorHandlingInterceptor = ErrorHandlingInterceptor(json, secureStorage)

    @Provides
    @Reusable
    fun provideSSLCertificateHelper(
        @ApplicationContext context: Context
    ): SSLCertificateHelper = SSLCertificateHelperImpl(context)

    // endregion

    // region Auth requests

    @Provides
    @Named("AuthOkHttpClient")
    @Singleton
    fun provideAuthOkHttpClient(
        sslCertificateHelper: SSLCertificateHelper,
        appInfoInterceptor: AppInfoInterceptor,
        errorInterceptor: ErrorHandlingInterceptor
    ): OkHttpClient {
        val (sslContext, x509TrustManager) = sslCertificateHelper.createSSLContext(R.raw.server)
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, x509TrustManager)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .addInterceptor(appInfoInterceptor)
            .addInterceptor(errorInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Named("AuthRetrofit")
    @Singleton
    fun provideAuthRetrofit(
        json: Json,
        @Named("AuthOkHttpClient") okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:443/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()

    @Provides
    fun provideAuthorizeApi(
        @Named("AuthRetrofit") retrofit: Retrofit
    ): AuthorizeApi = retrofit.create(AuthorizeApi::class.java)

    @Provides
    fun provideAuthorizeByRefreshApi(
        @Named("AuthRetrofit") retrofit: Retrofit
    ): AuthorizeByRefreshApi = retrofit.create(AuthorizeByRefreshApi::class.java)

    // endregion

    // region Other requests

    @Provides
    @Singleton
    fun provideMainOkHttpClient(
        sslCertificateHelper: SSLCertificateHelper,
        appInfoInterceptor: AppInfoInterceptor,
        authInterceptor: AuthInterceptor,
        errorInterceptor: ErrorHandlingInterceptor
    ): OkHttpClient {
        val (sslContext, x509TrustManager) = sslCertificateHelper.createSSLContext(R.raw.server)
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, x509TrustManager)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .addInterceptor(appInfoInterceptor)
            .addInterceptor(errorInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideMainRetrofit(
        json: Json,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:443/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()

    @Provides
    fun provideGetPersonalDataApi(
        retrofit: Retrofit
    ): PersonalDataApi = retrofit.create(PersonalDataApi::class.java)

    @Provides
    fun provideLogoutApi(
        retrofit: Retrofit
    ): LogoutApi = retrofit.create(LogoutApi::class.java)

    @Provides
    fun provideBookingsApi(
        retrofit: Retrofit
    ): BookingsApi = retrofit.create(BookingsApi::class.java)

    @Provides
    fun provideBookingRequestApi(
        retrofit: Retrofit
    ): BookingRequestApi = retrofit.create(BookingRequestApi::class.java)

    @Provides
    fun provideBookingsDetailsApi(
        retrofit: Retrofit
    ): BookingsDetailsApi = retrofit.create(BookingsDetailsApi::class.java)

    // endregion
}