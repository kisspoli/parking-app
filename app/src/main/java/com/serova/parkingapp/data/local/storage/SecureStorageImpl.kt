package com.serova.parkingapp.data.local.storage

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

class SecureStorageImpl @Inject constructor(
    context: Context
) : SecureStorage {

    private val tag = this.javaClass.simpleName
    private val sharedPrefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    private val keyAlias = "com.serova.parkingapp.keyalias"

    private fun getOrCreateAesKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }


        return if (keyStore.containsAlias(keyAlias)) {
            Log.i(tag, "Using existing key: $keyAlias")
            (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            Log.d(tag, "Generating new key: $keyAlias")
            KeyGenerator.getInstance("AES", "AndroidKeyStore").apply {
                init(
                    KeyGenParameterSpec.Builder(
                        keyAlias,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(256)
                        .build()
                )
            }.generateKey()
        }
    }

    override suspend fun save(key: String, value: String) {
        Log.i(tag, "Saving data for key: $key")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.ENCRYPT_MODE, getOrCreateAesKey())
        }
        val encryptedBytes = cipher.doFinal(value.toByteArray())
        val iv = cipher.iv
        sharedPrefs.edit {
            putString(key, Base64.encodeToString(encryptedBytes, Base64.NO_WRAP))
                .putString("${key}_iv", Base64.encodeToString(iv, Base64.NO_WRAP))
        }
        Log.i(tag, "Data saved successfully for key: $key (IV size: ${iv.size} bytes)")
    }

    override suspend fun get(key: String): String? {
        val encrypted = sharedPrefs.getString(key, null)
        if (encrypted == null) {
            Log.d(tag, "No data found for key: $key")
            return null
        }

        val ivString = sharedPrefs.getString("${key}_iv", null)
        if (ivString == null) {
            Log.e(tag, "IV not found for key: $key")
            return null
        }

        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(
                Cipher.DECRYPT_MODE,
                getOrCreateAesKey(),
                GCMParameterSpec(128, Base64.decode(ivString, Base64.NO_WRAP))
            )
        }
        val decryptedBytes = cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP))
        Log.i(tag, "Data retrieved successfully for key: $key")
        return String(decryptedBytes, Charsets.UTF_8)
    }

    override suspend fun delete(key: String) {
        sharedPrefs.edit {
            remove(key)
                .remove("${key}_iv")
        }
        Log.i(tag, "Data deleted for key: $key")
    }

    override suspend fun clear() {
        sharedPrefs.edit {
            clear()
        }
        Log.d(tag, "Cleared secure storage")
    }
}