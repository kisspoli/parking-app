package com.serova.parkingapp.data.local.cache.personaldata

import com.serova.parkingapp.data.local.cache.BaseCache

interface PersonalDataCache : BaseCache {
    suspend fun getPersonalData(): String?
    suspend fun savePersonalData(fullName: String)
}