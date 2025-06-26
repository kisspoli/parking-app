package com.serova.parkingapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.serova.parkingapp.data.local.preferences.PreferencesKeys.LANGUAGE_KEY
import com.serova.parkingapp.data.local.preferences.PreferencesKeys.PREFS_NAME
import com.serova.parkingapp.domain.model.settings.AppLanguage
import com.serova.parkingapp.domain.repository.LanguageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class LanguageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LanguageRepository {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val _languageFlow = MutableStateFlow(getCurrentLanguage())

    init {
        prefs.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == LANGUAGE_KEY) {
                _languageFlow.value = getCurrentLanguage()
            }
        }
    }

    override suspend fun saveLanguage(language: AppLanguage) {
        prefs.edit {
            putString(LANGUAGE_KEY, language.name)
        }
        _languageFlow.value = language
    }

    override fun getCurrentLanguage(): AppLanguage {
        val storedName = prefs.getString(LANGUAGE_KEY, AppLanguage.SYSTEM.name)!!
        return runCatching { AppLanguage.valueOf(storedName) }
            .getOrDefault(AppLanguage.SYSTEM)
    }

    override fun getCurrentLanguageAsFlow(): StateFlow<AppLanguage> {
        return _languageFlow.asStateFlow()
    }
}