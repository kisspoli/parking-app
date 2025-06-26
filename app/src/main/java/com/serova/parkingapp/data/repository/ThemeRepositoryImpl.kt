package com.serova.parkingapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.serova.parkingapp.data.local.preferences.PreferencesKeys.PREFS_NAME
import com.serova.parkingapp.data.local.preferences.PreferencesKeys.THEME_KEY
import com.serova.parkingapp.domain.model.settings.AppTheme
import com.serova.parkingapp.domain.repository.ThemeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ThemeRepository {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val _themeFlow = MutableStateFlow(getCurrentTheme())

    init {
        prefs.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == THEME_KEY) {
                _themeFlow.value = getCurrentTheme()
            }
        }
    }

    override suspend fun saveTheme(theme: AppTheme) {
        prefs.edit {
            putString(THEME_KEY, theme.name)
        }
        _themeFlow.value = theme
    }

    override fun getCurrentTheme(): AppTheme {
        val storedName = prefs.getString(THEME_KEY, AppTheme.SYSTEM.name)
            ?: AppTheme.SYSTEM.name
        return AppTheme.valueOf(storedName)
    }

    override fun getCurrentThemeAsFlow(): Flow<AppTheme> {
        return _themeFlow.asStateFlow()
    }
}