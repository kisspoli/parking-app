package com.serova.parkingapp.data.api.interceptor

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.LocaleList
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.serova.parkingapp.domain.model.settings.AppLanguage
import com.serova.parkingapp.domain.repository.LanguageRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.util.Locale

@ExtendWith(MockitoExtension::class)
class AppInfoInterceptorTests {

    @Mock
    lateinit var mockContext: Context
    @Mock
    lateinit var mockPackageManager: PackageManager
    @Mock
    lateinit var mockResources: Resources
    @Mock
    lateinit var mockConfiguration: Configuration
    @Mock
    lateinit var mockLanguageRepository: LanguageRepository

    @InjectMocks
    private lateinit var interceptor: AppInfoInterceptor
    private lateinit var wireMockServer: WireMockServer
    private lateinit var client: OkHttpClient

    @BeforeEach
    fun setUp() {
        wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())
        wireMockServer.start()
        WireMock.configureFor("localhost", wireMockServer.port())

        whenever(mockContext.packageManager).thenReturn(mockPackageManager)
        whenever(mockContext.packageName).thenReturn("com.example.app")

        whenever(mockContext.getString(anyInt())).thenAnswer {
            "MyApp"
        }


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
    fun `should add all required headers`() {
        // Arrange
        val packageInfo = PackageInfo().apply { versionName = "1.2.3" }
        whenever(mockPackageManager.getPackageInfo("com.example.app", 0)).thenReturn(packageInfo)
        whenever(mockLanguageRepository.getCurrentLanguage()).thenReturn(AppLanguage.ENGLISH)

        // Act
        val request = Request.Builder()
            .url(wireMockServer.baseUrl() + "/test")
            .build()

        client.newCall(request).execute()

        // Assert
        val serveEvents = wireMockServer.allServeEvents
        val receivedRequest = serveEvents.first().request

        assertEquals("MyApp", receivedRequest.getHeader("appName"))
        assertEquals("android", receivedRequest.getHeader("platform"))
        assertEquals("1.2.3", receivedRequest.getHeader("appVersion"))
        assertEquals("en", receivedRequest.getHeader("appLang"))
        assertEquals("application/json", receivedRequest.getHeader("Accept"))
    }

    @Test
    fun `should use device language when repository returns empty`() {
        // Arrange
        val packageInfo = PackageInfo().apply { versionName = "1.2.3" }
        whenever(mockContext.resources).thenReturn(mockResources)
        whenever(mockResources.configuration).thenReturn(mockConfiguration)
        whenever(mockPackageManager.getPackageInfo("com.example.app", 0)).thenReturn(packageInfo)
        whenever(mockLanguageRepository.getCurrentLanguage()).thenReturn(AppLanguage.SYSTEM)
        whenever(mockConfiguration.locales).thenReturn(LocaleList(Locale("fr")))

        // Act
        val request = Request.Builder()
            .url(wireMockServer.baseUrl() + "/test")
            .build()

        client.newCall(request).execute()

        // Assert
        val serveEvents = wireMockServer.allServeEvents
        val receivedRequest = serveEvents.first().request

        assertEquals("fr", receivedRequest.getHeader("appLang"))
    }
}