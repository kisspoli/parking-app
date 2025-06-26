package com.serova.parkingapp.data.repository

import com.serova.parkingapp.data.api.LogoutApi
import com.serova.parkingapp.data.local.cache.personaldata.PersonalDataCache
import com.serova.parkingapp.data.local.storage.SecureStorage
import com.serova.parkingapp.domain.repository.LogoutRepository
import javax.inject.Inject

class LogoutRepositoryImpl @Inject constructor(
    private var logoutApi: LogoutApi,
    private var secureStorage: SecureStorage,
    private var personalDataCache: PersonalDataCache,
    private var bookingsCache: PersonalDataCache
) : LogoutRepository {
    override suspend fun logout(): Result<Unit> {
        return try {
            logoutApi.logout()
            clearData()
            Result.success(Unit)
        } catch (e: Exception) {
            clearData()
            Result.failure(e)
        }
    }

    private suspend fun clearData() {
        secureStorage.clear()
        secureStorage.clear()
        personalDataCache.clear()
        bookingsCache.clear()
    }
}