package com.serova.parkingapp.domain.repository

import com.serova.parkingapp.domain.model.settings.AppTheme
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    suspend fun saveTheme(theme: AppTheme)
    fun getCurrentThemeAsFlow(): Flow<AppTheme>
    fun getCurrentTheme(): AppTheme
}