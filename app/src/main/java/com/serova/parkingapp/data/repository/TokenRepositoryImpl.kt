package com.serova.parkingapp.data.repository

import com.serova.parkingapp.data.local.storage.SecureStorage
import com.serova.parkingapp.domain.repository.TokenRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.until
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val secureStorage: SecureStorage
) : TokenRepository {
    companion object {
        private const val REFRESH_TOKEN_KEY = "refreshToken"
    }

    private var accessToken: String? = null
    private var validUntil: Instant? = null

    override suspend fun getAccessToken(returnInvalid: Boolean): String? {
        return if (returnInvalid) accessToken else validUntil?.let {
            if (Clock.System.now().until(it, DateTimeUnit.SECOND) < 60) null else accessToken
        }
    }

    override suspend fun saveAccessToken(accessToken: String, validUntil: Instant) {
        this.accessToken = accessToken
        this.validUntil = validUntil
    }

    override suspend fun getRefreshToken(): String? {
        return secureStorage.get(REFRESH_TOKEN_KEY)
    }

    override suspend fun saveRefreshToken(refreshToken: String) {
        secureStorage.save(REFRESH_TOKEN_KEY, refreshToken)
    }
}