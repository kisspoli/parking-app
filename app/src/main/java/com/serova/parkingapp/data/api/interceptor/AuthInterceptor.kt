package com.serova.parkingapp.data.api.interceptor

import android.util.Log
import com.serova.parkingapp.domain.model.exception.ApiException
import com.serova.parkingapp.domain.repository.AuthRepository
import com.serova.parkingapp.domain.repository.TokenRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository
) : Interceptor {

    private val tag = this.javaClass.simpleName
    private val mutex = Mutex()

    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            val request = chain.request()

            val response = try {
                if (shouldRefreshToken()) {
                    Log.i(tag, "Token refresh required")
                    mutex.withLock {
                        authRepository.refreshAuth().getOrThrow()
                    }
                    Log.i(tag, "Token successfully refreshed")
                } else {
                    null
                }

                chain.proceedWithToken(request, tokenRepository.getAccessToken().orEmpty())
            } catch (e: Exception) {
                Log.e(tag, "Authentication error: ${e.message}", e)
                throw e as? ApiException ?: IOException("Auth token error")
            }
            return@runBlocking response
        }
    }

    private fun Interceptor.Chain.proceedWithToken(request: Request, token: String): Response {
        Log.i(
            tag, """
                Headers added:
                - Authorization: Bearer  ${
                token.replaceRange(
                    4,
                    token.length - 4,
                    "*".repeat(token.length - 8)
                )
            }
            """.trimIndent()
        )

        return proceed(
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        )
    }

    private suspend fun shouldRefreshToken(): Boolean {
        return tokenRepository.getAccessToken() == null
    }
}