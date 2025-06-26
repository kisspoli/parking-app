package com.serova.parkingapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.serova.parkingapp.data.local.preferences.PreferencesKeys.LANGUAGE_KEY
import com.serova.parkingapp.data.local.preferences.PreferencesKeys.PREFS_NAME
import com.serova.parkingapp.data.local.preferences.PreferencesKeys.THEME_KEY
import com.serova.parkingapp.domain.model.settings.AppLanguage
import com.serova.parkingapp.domain.model.settings.AppTheme
import com.serova.parkingapp.domain.usecase.theme.GetThemeFlowUseCase
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var getThemeFlowUseCase: GetThemeFlowUseCase

    override fun attachBaseContext(newBase: Context) {
        val language = getLanguageFromPrefs(newBase)
        super.attachBaseContext(applyLanguageConfiguration(newBase, language))
    }

    private fun getLanguageFromPrefs(context: Context): AppLanguage {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val storedName = prefs.getString(LANGUAGE_KEY, AppLanguage.SYSTEM.name)
            ?: AppLanguage.SYSTEM.name
        return AppLanguage.valueOf(storedName)
    }

    private fun getThemeFromPrefs(context: Context): AppTheme {
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val storedName = prefs.getString(THEME_KEY, AppTheme.SYSTEM.name)
            ?: AppTheme.SYSTEM.name
        return AppTheme.valueOf(storedName)
    }

    private fun applyLanguageConfiguration(context: Context, language: AppLanguage): Context {
        val locale = when (language) {
            AppLanguage.SYSTEM -> Locale.getDefault()
            else -> Locale(language.localeCode)
        }

        return context.createConfigurationContext(
            Configuration().apply {
                setLocale(locale)
                setLayoutDirection(locale)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        @SuppressLint("SourceLockedOrientationActivity")
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            ParkingApp(
                appTheme = getThemeFromPrefs(applicationContext),
                getThemeFlowUseCase = getThemeFlowUseCase
            )
        }
    }
}