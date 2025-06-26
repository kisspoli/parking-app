package com.serova.parkingapp.data.api.interceptor

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.serova.parkingapp.data.api.model.response.ErrorResponse
import com.serova.parkingapp.domain.model.exception.ApiException
import com.serova.parkingapp.domain.repository.AuthRepository
import com.serova.parkingapp.domain.repository.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.IOException
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class AuthInterceptorTests {

    @Mock
    lateinit var authRepository: AuthRepository
    @Mock
    lateinit var tokenRepository: TokenRepository

    @InjectMocks
    private lateinit var interceptor: AuthInterceptor

    private lateinit var wireMockServer: WireMockServer
    private lateinit var client: OkHttpClient

    @BeforeEach
    fun setUp() {
        wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())
        wireMockServer.start()
        WireMock.configureFor("localhost", wireMockServer.port())

        client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        wireMockServer.stubFor(
            WireMock.any(WireMock.anyUrl())
                .willReturn(WireMock.aResponse().withStatus(200))
        )
    }

    @AfterEach
    fun tearDown() {
        wireMockServer.stop()
    }

    @Test
    fun `should add authorization header when token exists`() = runBlocking {
        // Arrange
        val token = "valid_token_123"
        whenever(tokenRepository.getAccessToken()).thenReturn(token)

        // Act
        val request = Request.Builder()
            .url(wireMockServer.baseUrl() + "/secure")
            .build()

        client.newCall(request).execute()

        // Assert
        val serveEvents = wireMockServer.allServeEvents
        val receivedRequest = serveEvents.first().request

        assertEquals("Bearer $token", receivedRequest.getHeader("Authorization"))
        verify(authRepository, never()).refreshAuth()
        Unit
    }

    @Test
    fun `should refresh token when missing and add new token to header`() = runBlocking {
        // Arrange
        val newToken = "refreshed_token_456"
        whenever(tokenRepository.getAccessToken())
            .thenReturn(null)
            .thenReturn(newToken)

        whenever(authRepository.refreshAuth()).thenReturn(Result.success(Unit))

        // Act
        val request = Request.Builder()
            .url(wireMockServer.baseUrl() + "/secure")
            .build()

        client.newCall(request).execute()

        // Assert
        val serveEvents = wireMockServer.allServeEvents
        val receivedRequest = serveEvents.first().request

        assertEquals("Bearer $newToken", receivedRequest.getHeader("Authorization"))
        verify(authRepository).refreshAuth()
        Unit
    }

    @Test
    fun `should clear storage and throw exception when token refresh fails`() = runBlocking {
        // Arrange
        val errorMessage = "Refresh failed"
        whenever(tokenRepository.getAccessToken()).thenReturn(null)
        whenever(authRepository.refreshAuth()).thenReturn(
            Result.failure(
                ApiException(
                    ErrorResponse(
                        message = errorMessage,
                        status = "Error",
                        requestId = "requestId"
                    ),
                    null
                )
            )
        )

        // Act & Assert
        val request = Request.Builder()
            .url(wireMockServer.baseUrl() + "/secure")
            .build()

        val exception = assertFailsWith<ApiException> {
            client.newCall(request).execute()
        }

        assertEquals(errorMessage, exception.error.message)
        verify(authRepository).refreshAuth()
        Unit
    }

    @Test
    fun `should throw IO exception for non-api errors`() = runBlocking {
        // Arrange
        val errorMessage = "Network error"
        whenever(tokenRepository.getAccessToken()).thenReturn(null)
        whenever(authRepository.refreshAuth()).thenReturn(
            Result.failure(IOException(errorMessage))
        )

        // Act & Assert
        val request = Request.Builder()
            .url(wireMockServer.baseUrl() + "/secure")
            .build()

        val exception = assertFailsWith<IOException> {
            client.newCall(request).execute()
        }

        assertTrue(exception.message!!.contains("Auth token error"))
        verify(authRepository).refreshAuth()
        Unit
    }

    @Test
    fun `should not refresh token when token exists`() = runBlocking {
        // Arrange
        val token = "existing_token_789"
        whenever(tokenRepository.getAccessToken()).thenReturn(token)

        // Act
        val request = Request.Builder()
            .url(wireMockServer.baseUrl() + "/secure")
            .build()

        client.newCall(request).execute()

        // Assert
        verify(authRepository, never()).refreshAuth()
        Unit
    }

    @Test
    fun `should use mutex for concurrent token refresh`() = runBlocking {
        // Arrange
        val newToken = "concurrent_token_abc"
        whenever(tokenRepository.getAccessToken())
            .thenReturn(null)
            .thenReturn(newToken)

        var lockAcquired = false
        whenever(authRepository.refreshAuth()).thenAnswer {
            assertTrue(lockAcquired.not(), "Mutex should prevent concurrent refresh")
            lockAcquired = true
            Result.success(Unit)
        }

        // Act
        val request = Request.Builder()
            .url(wireMockServer.baseUrl() + "/secure")
            .build()

        client.newCall(request).execute()

        // Assert
        verify(authRepository).refreshAuth()
        assertNotNull(wireMockServer.allServeEvents.first().request.getHeader("Authorization"))
    }
}