package com.serova.parkingapp.data.local.cache.personaldata

import android.util.Log
import com.serova.parkingapp.data.local.storage.SecureStorage
import javax.inject.Inject

class PersonalDataCacheImpl @Inject constructor(
    private val secureStorage: SecureStorage
) : PersonalDataCache {
    companion object {
        private const val FULL_NAME_KEY = "userFullName"
    }

    private val tag = this.javaClass.simpleName

    override suspend fun getPersonalData(): String? {
        return secureStorage.get(FULL_NAME_KEY).also {
            Log.i(tag, "Retrieved personal data from database")
        }
    }

    override suspend fun savePersonalData(fullName: String) {
        secureStorage.save(FULL_NAME_KEY, fullName)
        Log.i(tag, "Saved personal data to database")
    }

    override suspend fun clear() {
        secureStorage.delete(FULL_NAME_KEY)
        Log.i(tag, "Cleared personal data database")
    }
}