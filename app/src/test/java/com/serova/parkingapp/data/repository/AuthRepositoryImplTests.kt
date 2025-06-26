package com.serova.parkingapp.data.repository

import com.serova.parkingapp.data.api.AuthorizeApi
import com.serova.parkingapp.data.api.AuthorizeByRefreshApi
import com.serova.parkingapp.data.api.model.request.AuthorizeByRefreshRequest
import com.serova.parkingapp.data.api.model.request.AuthorizeRequest
import com.serova.parkingapp.data.api.model.response.AuthorizeByRefreshResponse
import com.serova.parkingapp.data.api.model.response.AuthorizeResponse
import com.serova.parkingapp.domain.repository.TokenRepository
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class AuthRepositoryImplTests {

    @Mock
    private lateinit var authorizeByRefreshApi: AuthorizeByRefreshApi
    @Mock
    private lateinit var authorizeApi: AuthorizeApi
    @Mock
    private lateinit var tokenRepository: TokenRepository
    @InjectMocks
    private lateinit var authRepository: AuthRepositoryImpl

    @Test
    fun `login should save tokens and return success on successful API call`() = runBlocking {
        // Arrange
        val username = "test_user"
        val password = "test_password"
        val response = AuthorizeResponse(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            validUntil = Instant.parse("2030-12-12T00:00:00Z"),
            status = "OK",
            requestId = "requestId",
        )

        whenever(authorizeApi.login(any())).thenReturn(response)

        // Act
        val result = authRepository.login(username, password)

        // Assert
        assertTrue(result.isSuccess)

        val requestCaptor = argumentCaptor<AuthorizeRequest>()
        verify(authorizeApi).login(requestCaptor.capture())
        assertEquals(username, requestCaptor.firstValue.username)
        assertEquals(password, requestCaptor.firstValue.password)

        verify(tokenRepository).saveAccessToken(
            "accessToken",
            Instant.parse("2030-12-12T00:00:00Z")
        )
        verify(tokenRepository).saveRefreshToken("refreshToken")
    }

    @Test
    fun `login should return failure on any exception`() = runBlocking {
        // Arrange
        val username = "test_user"
        val password = "test_password"
        val exception = RuntimeException("Network error")

        whenever(authorizeApi.login(any())).thenThrow(exception)

        // Act
        val result = authRepository.login(username, password)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(tokenRepository, never()).saveAccessToken(any(), any())
        verify(tokenRepository, never()).saveRefreshToken(any())
    }

    @Test
    fun `refreshAuth should save new tokens and return success on successful API call`() =
        runBlocking {
            // Arrange
            val refreshToken = "oldRefreshToken"
            val response = AuthorizeByRefreshResponse(
                accessToken = "accessToken",
                refreshToken = "refreshToken",
                validUntil = Instant.parse("2030-12-12T00:00:00Z"),
                status = "OK",
                requestId = "requestId",
            )

            whenever(tokenRepository.getRefreshToken()).thenReturn(refreshToken)
            whenever(authorizeByRefreshApi.login(any())).thenReturn(response)

            // Act
            val result = authRepository.refreshAuth()

            // Assert
            assertTrue(result.isSuccess)

            val requestCaptor = argumentCaptor<AuthorizeByRefreshRequest>()
            verify(authorizeByRefreshApi).login(requestCaptor.capture())
            assertEquals(refreshToken, requestCaptor.firstValue.refreshToken)

            verify(tokenRepository).saveAccessToken(
                "accessToken",
                Instant.parse("2030-12-12T00:00:00Z")
            )
            verify(tokenRepository).saveRefreshToken("refreshToken")
        }

    @Test
    fun `refreshAuth should return failure on any exception`() = runBlocking {
        // Arrange
        val refreshToken = "refreshToken"
        val exception = RuntimeException("Server error")

        whenever(tokenRepository.getRefreshToken()).thenReturn(refreshToken)
        whenever(authorizeByRefreshApi.login(any())).thenThrow(exception)

        // Act
        val result = authRepository.refreshAuth()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        verify(tokenRepository, never()).saveAccessToken(any(), any())
        verify(tokenRepository, never()).saveRefreshToken(any())
    }

    @Test
    fun `refreshAuth should handle NullPointerException when refresh token is null`() =
        runBlocking {
            // Arrange
            whenever(tokenRepository.getRefreshToken()).thenReturn(null)

            // Act
            val result = authRepository.refreshAuth()

            // Assert
            assertTrue(result.isFailure)
            assertEquals(
                result.exceptionOrNull()?.javaClass, NullPointerException::class.java
            )
            verify(authorizeByRefreshApi, never()).login(any())
            verify(tokenRepository, never()).saveAccessToken(any(), any())
            verify(tokenRepository, never()).saveRefreshToken(any())
        }
}