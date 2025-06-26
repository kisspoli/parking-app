package com.serova.parkingapp.data.local.storage

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.util.Base64
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mockStatic
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SecureStorageImplTests {

    @Mock
    private lateinit var mockContext: Context
    @Mock
    private lateinit var mockSharedPrefs: SharedPreferences
    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor
    @Mock
    private lateinit var mockKeyStore: KeyStore
    @Mock
    private lateinit var mockKeyGenerator: KeyGenerator
    @Mock
    private lateinit var mockSecretKey: SecretKey
    @Mock
    private lateinit var mockCipher: Cipher

    private lateinit var secureStorage: SecureStorageImpl
    private lateinit var mockedKeyStore: MockedStatic<KeyStore>
    private lateinit var mockedKeyGenerator: MockedStatic<KeyGenerator>
    private lateinit var mockedCipher: MockedStatic<Cipher>
    private lateinit var mockedBase64: MockedStatic<Base64>

    @BeforeEach
    fun setup() {
        whenever(
            mockContext.getSharedPreferences(
                anyString(),
                anyInt()
            )
        ).thenReturn(mockSharedPrefs)
        whenever(mockSharedPrefs.edit()).thenReturn(mockEditor)
        whenever(mockEditor.remove(anyString())).thenReturn(mockEditor)
        whenever(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        whenever(mockEditor.clear()).thenReturn(mockEditor)

        mockedKeyStore = mockStatic(KeyStore::class.java)
        mockedKeyStore.`when`<KeyStore> { KeyStore.getInstance("AndroidKeyStore") }
            .thenReturn(mockKeyStore)
        whenever(mockKeyStore.load(null)).then { }

        mockedKeyGenerator = mockStatic(KeyGenerator::class.java)
        mockedKeyGenerator.`when`<KeyGenerator> {
            KeyGenerator.getInstance("AES", "AndroidKeyStore")
        }.thenReturn(mockKeyGenerator)

        mockedCipher = mockStatic(Cipher::class.java)
        mockedCipher.`when`<Cipher> { Cipher.getInstance("AES/GCM/NoPadding") }
            .thenReturn(mockCipher)

        mockedBase64 = mockStatic(Base64::class.java)
        mockedBase64.`when`<String> { Base64.encodeToString(any(), anyInt()) }.thenReturn("encoded")
        mockedBase64.`when`<ByteArray> { Base64.decode(anyString(), anyInt()) }
            .thenReturn(ByteArray(0))

        secureStorage = SecureStorageImpl(mockContext)
    }

    @AfterEach
    fun tearDown() {
        mockedKeyStore.close()
        mockedKeyGenerator.close()
        mockedCipher.close()
        mockedBase64.close()
    }

    @Test
    fun `save should encrypt and store data`() = runBlocking {
        // Arrange
        val key = "test_key"
        val keyAlias = "com.serova.parkingapp.keyalias"
        val value = "test_value"

        whenever(mockKeyStore.containsAlias(anyString())).thenReturn(true)

        val secretKeyEntry = Mockito.mock(KeyStore.SecretKeyEntry::class.java)
        whenever(secretKeyEntry.secretKey).thenReturn(mockSecretKey)

        whenever(mockKeyStore.containsAlias(eq(keyAlias))).thenReturn(true)
        whenever(mockKeyStore.getEntry(eq(keyAlias), eq(null))).thenReturn(secretKeyEntry)

        whenever(mockCipher.doFinal(value.toByteArray())).thenReturn(ByteArray(10))
        whenever(mockCipher.iv).thenReturn(ByteArray(12))

        // Act
        secureStorage.save(key, value)

        // Assert
        verify(mockEditor).putString(eq(key), eq("encoded"))
        verify(mockEditor).putString(eq("${key}_iv"), eq("encoded"))
        verify(mockEditor).apply()
    }

    @Test
    fun `get should return decrypted value`() = runBlocking {
        // Arrange
        val key = "test_key"
        val keyAlias = "com.serova.parkingapp.keyalias"
        val value = "test_value"

        whenever(mockSharedPrefs.getString(key, null)).thenReturn("encrypted_data")
        whenever(mockSharedPrefs.getString("${key}_iv", null)).thenReturn("iv_data")
        whenever(mockKeyStore.containsAlias(anyString())).thenReturn(true)

        val secretKeyEntry = Mockito.mock(KeyStore.SecretKeyEntry::class.java)
        whenever(secretKeyEntry.secretKey).thenReturn(mockSecretKey)

        whenever(mockKeyStore.containsAlias(eq(keyAlias))).thenReturn(true)
        whenever(mockKeyStore.getEntry(eq(keyAlias), eq(null))).thenReturn(secretKeyEntry)

        whenever(mockCipher.doFinal(any())).thenReturn(value.toByteArray())

        // Act
        val result = secureStorage.get(key)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `get should return null for non-existent key`() = runBlocking {
        // Arrange
        val key = "non_existent_key"
        whenever(mockSharedPrefs.getString(key, null)).thenReturn(null)

        // Act
        val result = secureStorage.get(key)

        // Assert
        assertNull(result)
    }

    @Test
    fun `delete should remove key and iv`() = runBlocking {
        // Arrange
        val key = "test_key"

        // Act
        secureStorage.delete(key)

        // Assert
        verify(mockEditor).remove(eq(key))
        verify(mockEditor).remove(eq("${key}_iv"))
        verify(mockEditor).apply()
    }

    @Test
    fun `clear should remove all data`() = runBlocking {
        // Act
        secureStorage.clear()

        // Assert
        verify(mockEditor).clear()
        verify(mockEditor).apply()
    }

    @Test
    fun `get should return null when only key exists without iv`() = runBlocking {
        // Arrange
        val key = "incomplete_key"
        whenever(mockSharedPrefs.getString(key, null)).thenReturn("some_value")
        whenever(mockSharedPrefs.getString("${key}_iv", null)).thenReturn(null)

        // Act
        val result = secureStorage.get(key)

        // Assert
        assertNull(result)
    }

    @Test
    @Disabled("KeyGenParameterSpec isn't available in JVM and Robolectric won't work with AndroidKeyStore")
    fun `should create new key when not exists`() = runBlocking {
        // Arrange
        val key = "test_key"
        val value = "test_value"

        whenever(mockKeyStore.containsAlias(anyString())).thenReturn(false)

        whenever(mockKeyGenerator.generateKey()).thenReturn(mockSecretKey)

        doNothing().whenever(mockKeyGenerator).init(any<KeyGenParameterSpec>())

        whenever(mockCipher.doFinal(value.toByteArray())).thenReturn(ByteArray(10))
        whenever(mockCipher.iv).thenReturn(ByteArray(12))

        // Act
        secureStorage.save(key, value)

        // Assert
        verify(mockKeyGenerator).init(any<SecureRandom>())
        verify(mockKeyGenerator).generateKey()
        verify(mockEditor).putString(eq(key), anyString())
        verify(mockEditor).putString(eq("${key}_iv"), anyString())
        Unit
    }

    @Test
    fun `should throw when decryption fails`() = runBlocking {
        // Arrange
        val key = "test_key"
        val keyAlias = "com.serova.parkingapp.keyalias"

        whenever(mockSharedPrefs.getString(key, null)).thenReturn("encrypted_data")
        whenever(mockSharedPrefs.getString("${key}_iv", null)).thenReturn("iv_data")

        val secretKeyEntry = Mockito.mock(KeyStore.SecretKeyEntry::class.java)
        whenever(secretKeyEntry.secretKey).thenReturn(mockSecretKey)

        whenever(mockKeyStore.containsAlias(eq(keyAlias))).thenReturn(true)
        whenever(mockKeyStore.getEntry(eq(keyAlias), eq(null))).thenReturn(secretKeyEntry)

        whenever(mockCipher.doFinal(any())).thenThrow(RuntimeException("Decryption failed"))

        // Act & Assert
        val exception = assertFailsWith<RuntimeException> {
            runBlocking {
                secureStorage.get(key)
            }
        }
        assertEquals("Decryption failed", exception.message)
    }
}