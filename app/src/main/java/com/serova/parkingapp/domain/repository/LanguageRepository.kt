package com.serova.parkingapp.domain.repository

import com.serova.parkingapp.domain.model.settings.AppLanguage
import kotlinx.coroutines.flow.StateFlow

interface LanguageRepository {
    suspend fun saveLanguage(language: AppLanguage)
    fun getCurrentLanguage(): AppLanguage
    fun getCurrentLanguageAsFlow(): StateFlow<AppLanguage>
}