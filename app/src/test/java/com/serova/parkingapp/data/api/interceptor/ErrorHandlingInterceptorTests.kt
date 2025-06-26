package com.serova.parkingapp.data.api.interceptor

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.serova.parkingapp.data.local.storage.SecureStorage
import com.serova.parkingapp.domain.model.exception.ApiException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import java.io.IOException
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class ErrorHandlingInterceptorTests {

    @Mock
    lateinit var secureStorage: SecureStorage
    private val json = Json { ignoreUnknownKeys = true }

    private lateinit var interceptor: ErrorHandlingInterceptor
    private lateinit var wireMockServer: WireMockServer
    private lateinit var client: OkHttpClient

    @BeforeEach
    fun setUp() {
        interceptor = ErrorHandlingInterceptor(json, secureStorage)

        wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())
        wireMockServer.start()
        WireMock.configureFor("localhost", wireMockServer.port())

        client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @AfterEach
    fun tearDown() {
        wireMockServer.stop()
    }

    @Test
    fun `should pass through successful responses`() = runBlocking {
        // Arrange
        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/success"))
                .willReturn(WireMock.aResponse().withStatus(200))
        )

        // Act
        val request = Request.Builder()
            .url(wireMockServer.baseUrl() + "/success")
            .build()

        val response = client.newCall(request).execute()

        // Assert
        assertEquals(200, response.code)
        verify(secureStorage, never()).clear()
    }

    @Test
    fun `should throw ApiException for error responses`() = runBlocking {
        // Arrange
        val errorBody = """
            {
                "status": "ERROR",
                "message": "message",
                "requestId": "requestId"
            }
        """.trimIndent()

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/error"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(400)
                        .withBody(errorBody)
                )
        )

        // Act & Assert
        val request = Request.Builder()
            .url(wireMockServer.baseUrl() + "/error")
            .build()

        val exception = assertFailsWith<IOException> {
            client.newCall(request).execute()
        }

        assertNotNull(exception as? ApiException)

        assertEquals("ERROR", exception.error.status)
        assertEquals("message", exception.error.message)
        assertEquals("requestId", exception.error.requestId)
        verify(secureStorage, never()).clear()
    }

    @Test
    fun `should clear storage for UNKNOWN_TOKEN errors`() = runBlocking {
        // Arrange
        val errorBody = """
            {
                "status": "UNKNOWN_TOKEN",
                "message": "message",
                "requestId": "requestId"
            }
        """.trimIndent()

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/token-error"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(401)
                        .withBody(errorBody)
                )
        )

        // Act & Assert
        val request = Request.Builder()
            .url(wireMockServer.baseUrl() + "/token-error")
            .build()

        val exception = assertFailsWith<IOException> {
            client.newCall(request).execute()
        }

        assertNotNull(exception as? ApiException)

        assertEquals("UNKNOWN_TOKEN", exception.error.status)
        verify(secureStorage).clear()
    }

    @Test
    fun `should throw IOException for network errors`() = runBlocking {
        // Arrange
        val localClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        // Act & Assert
        val request = Request.Builder()
            .url("http://unreachable-server-12345/test")
            .build()

        val exception = assertFailsWith<IOException> {
            localClient.newCall(request).execute()
        }

        assertEquals("Network connection failed", exception.message)
        verify(secureStorage, never()).clear()
    }

    @Test
    fun `should throw IOException for invalid error responses`() = runBlocking {
        // Arrange
        val invalidBody = "Invalid JSON"

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/invalid-error"))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(500)
                        .withBody(invalidBody)
                )
        )

        // Act & Assert
        val request = Request.Builder()
            .url(wireMockServer.baseUrl() + "/invalid-error")
            .build()

        val exception = assertFailsWith<IOException> {
            client.newCall(request).execute()
        }

        assertTrue(exception.message!!.contains("Failed to parse error response"))
        verify(secureStorage, never()).clear()
    }
}