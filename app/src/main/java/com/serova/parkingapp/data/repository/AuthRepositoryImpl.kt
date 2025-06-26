package com.serova.parkingapp.data.repository

import android.util.Log
import com.serova.parkingapp.data.api.AuthorizeApi
import com.serova.parkingapp.data.api.AuthorizeByRefreshApi
import com.serova.parkingapp.data.api.model.request.AuthorizeByRefreshRequest
import com.serova.parkingapp.data.api.model.request.AuthorizeRequest
import com.serova.parkingapp.domain.repository.AuthRepository
import com.serova.parkingapp.domain.repository.TokenRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authorizeByRefreshApi: AuthorizeByRefreshApi,
    private val authorizeApi: AuthorizeApi,
    private val tokenRepository: TokenRepository
) : AuthRepository {

    private val tag = this.javaClass.simpleName

    override suspend fun login(username: String, password: String): Result<Unit> {
        Log.d(tag, "Attempting login for user: $username")
        return try {
            authorizeApi.login(AuthorizeRequest(username, password)).let { response ->
                Log.i(tag, "Login successful for user: $username. Saving tokens")
                tokenRepository.saveAccessToken(response.accessToken, response.validUntil)
                tokenRepository.saveRefreshToken(response.refreshToken)
            }

            Log.i(tag, "Tokens saved successfully for user: $username")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Login failed for user: $username", e)
            Result.failure(e)
        }
    }

    override suspend fun refreshAuth(): Result<Unit> {
        Log.d(tag, "Attempting token refresh")
        return try {
            val refreshToken = tokenRepository.getRefreshToken()
            if (refreshToken == null) {
                Log.d(tag, "Refresh failed: no refresh token available")
                return Result.failure(NullPointerException("No refresh token available"))
            }

            Log.d(tag, "Refresh token available. Refreshing")
            authorizeByRefreshApi.login(AuthorizeByRefreshRequest(refreshToken)).let { response ->
                Log.i(tag, "Token refresh successful. Saving new tokens")
                tokenRepository.saveAccessToken(response.accessToken, response.validUntil)
                tokenRepository.saveRefreshToken(response.refreshToken)
            }

            Log.i(tag, "New tokens saved successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Token refresh failed", e)
            Result.failure(e)
        }
    }
}