package com.serova.parkingapp.data.local.cache

interface BaseCache {
    suspend fun clear()
}