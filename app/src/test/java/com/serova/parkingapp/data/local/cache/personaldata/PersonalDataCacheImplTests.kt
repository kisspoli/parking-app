package com.serova.parkingapp.data.local.cache.personaldata

import com.serova.parkingapp.data.local.storage.SecureStorage
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class PersonalDataCacheImplTests {

    @Mock
    private lateinit var secureStorage: SecureStorage

    @InjectMocks
    private lateinit var personalDataCache: PersonalDataCacheImpl

    @Test
    fun `getPersonalData should return stored value`() = runBlocking {
        // Arrange
        val testName = "Test Name"
        whenever(secureStorage.get("userFullName")).thenReturn(testName)

        // Act
        val result = personalDataCache.getPersonalData()

        // Assert
        assertEquals(testName, result)
        verify(secureStorage).get("userFullName")
        Unit
    }

    @Test
    fun `getPersonalData should return null when no value stored`() = runBlocking {
        // Arrange
        whenever(secureStorage.get("userFullName")).thenReturn(null)

        // Act
        val result = personalDataCache.getPersonalData()

        // Assert
        assertNull(result)
        verify(secureStorage).get("userFullName")
        Unit
    }

    @Test
    fun `savePersonalData should store value`() = runBlocking {
        // Arrange
        val testName = "Test Name"

        // Act
        personalDataCache.savePersonalData(testName)

        // Assert
        verify(secureStorage).save("userFullName", testName)
    }

    @Test
    fun `savePersonalData should handle empty string`() = runBlocking {
        // Arrange
        val emptyName = ""

        // Act
        personalDataCache.savePersonalData(emptyName)

        // Assert
        verify(secureStorage).save("userFullName", emptyName)
    }

    @Test
    fun `clear should remove stored value`() = runBlocking {
        // Act
        personalDataCache.clear()

        // Assert
        verify(secureStorage).delete("userFullName")
    }

    @Test
    fun `clear should work even if no value stored`() = runBlocking {
        // Act
        personalDataCache.clear()

        // Assert
        verify(secureStorage).delete("userFullName")
    }
}