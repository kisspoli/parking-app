package com.serova.parkingapp.data.local.storage

interface SecureStorage {
    suspend fun save(key: String, value: String)
    suspend fun get(key: String): String?
    suspend fun delete(key: String)
    suspend fun clear()
}