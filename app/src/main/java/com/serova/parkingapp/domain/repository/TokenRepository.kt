package com.serova.parkingapp.domain.repository

import kotlinx.datetime.Instant

interface TokenRepository {
    suspend fun saveAccessToken(accessToken: String, validUntil: Instant)
    suspend fun getAccessToken(returnInvalid: Boolean = false): String?
    suspend fun saveRefreshToken(refreshToken: String)
    suspend fun getRefreshToken(): String?
}