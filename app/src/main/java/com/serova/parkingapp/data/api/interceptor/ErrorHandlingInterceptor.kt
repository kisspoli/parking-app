package com.serova.parkingapp.data.api.interceptor

import android.util.Log
import com.serova.parkingapp.data.api.model.response.ErrorResponse
import com.serova.parkingapp.data.local.storage.SecureStorage
import com.serova.parkingapp.domain.model.exception.ApiException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class ErrorHandlingInterceptor @Inject constructor(
    private val json: Json,
    private val secureStorage: SecureStorage
) : Interceptor {

    private val tag = this.javaClass.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            val response = try {
                chain.proceed(chain.request())
            } catch (e: IOException) {
                Log.e(tag, "Network error: ${e.message}", e)
                throw IOException("Network connection failed")
            }

            if (!response.isSuccessful) {
                parseError(response)?.let { throw it }
            }
            return@runBlocking response
        }
    }

    private suspend fun parseError(response: Response): ApiException? {
        return try {
            val errorBody = response.body?.string().orEmpty()
            val errorResponse = json.decodeFromString<ErrorResponse>(errorBody)
            Log.w(tag, "Parsed error: $errorResponse")

            ApiException(errorResponse).also {
                if (it.error.status == "UNKNOWN_TOKEN") {
                    secureStorage.clear()
                    Log.w(tag, "Secure storage cleared due to token error")
                }
                Log.e(tag, "API error: ${it.message}", it)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error parsing failed: ${e.message}", e)
            throw IOException("Failed to parse error response", e)
        }
    }
}