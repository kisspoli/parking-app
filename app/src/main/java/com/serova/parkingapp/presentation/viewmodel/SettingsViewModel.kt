package com.serova.parkingapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serova.parkingapp.R
import com.serova.parkingapp.domain.model.exception.ApiException
import com.serova.parkingapp.domain.model.resource.Resource
import com.serova.parkingapp.domain.model.settings.AppLanguage
import com.serova.parkingapp.domain.model.settings.AppTheme
import com.serova.parkingapp.domain.usecase.language.GetLanguageFlowUseCase
import com.serova.parkingapp.domain.usecase.language.GetLanguageUseCase
import com.serova.parkingapp.domain.usecase.language.SaveLanguageUseCase
import com.serova.parkingapp.domain.usecase.logout.LogoutUseCase
import com.serova.parkingapp.domain.usecase.personaldata.PersonalDataUseCase
import com.serova.parkingapp.domain.usecase.theme.GetThemeFlowUseCase
import com.serova.parkingapp.domain.usecase.theme.GetThemeUseCase
import com.serova.parkingapp.domain.usecase.theme.SaveThemeUseCase
import com.serova.parkingapp.presentation.viewmodel.data.AlertData
import com.serova.parkingapp.presentation.viewmodel.state.CommonUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val personalDataUseCase: PersonalDataUseCase,
    private val logoutUseCase: LogoutUseCase,
    @ApplicationContext private val context: Context,
    private val getLanguageUseCase: GetLanguageUseCase,
    private val getLanguageFlowUseCase: GetLanguageFlowUseCase,
    private val saveLanguageUseCase: SaveLanguageUseCase,
    private val getThemeUseCase: GetThemeUseCase,
    private val getThemeFlowUseCase: GetThemeFlowUseCase,
    private val saveThemeUseCase: SaveThemeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _recreateActivity = MutableSharedFlow<Unit>()
    val recreateActivity: SharedFlow<Unit> = _recreateActivity

    init {
        loadFullName()
        observePreferences()
    }

    private fun observePreferences() {
        _uiState.update {
            it.copy(
                currentLanguage = getLanguageUseCase(),
                currentTheme = getThemeUseCase()
            )
        }
        viewModelScope.launch {
            combine(
                getLanguageFlowUseCase(),
                getThemeFlowUseCase()
            ) { lang, theme ->
                lang to theme
            }.collect { prefs ->
                _uiState.update {
                    it.copy(
                        currentLanguage = prefs.first,
                        currentTheme = prefs.second
                    )
                }
            }
        }
    }

    fun setLanguage(language: AppLanguage) = viewModelScope.launch {
        _uiState.update { it.copy(currentLanguage = language) }

        val currentLanguage = getLanguageUseCase()

        val currentLanguageLocale = if (currentLanguage.localeCode.isEmpty()) {
            context.resources.configuration.locales.get(0).language
        } else {
            currentLanguage.localeCode
        }

        val newLanguageLocale = if (language.localeCode.isEmpty()) {
            context.resources.configuration.locales.get(0).language
        } else {
            language.localeCode
        }

        saveLanguageUseCase(language)

        if (newLanguageLocale != currentLanguageLocale) {
            _recreateActivity.emit(Unit)
        }
    }

    fun setTheme(theme: AppTheme) = viewModelScope.launch {
        _uiState.update { it.copy(currentTheme = theme) }
        saveThemeUseCase(theme)
    }

    private fun loadFullName() {
        if (uiState.value.fullName.isNullOrEmpty()) {
            _uiState.update { it.copy(isLoading = true) }
        }
        viewModelScope.launch {
            personalDataUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                fullName = resource.data,
                                isLoading = false,
                                alertData = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                fullName = resource.data ?: it.fullName,
                                isLoading = false,
                                alertData = createAlert(resource.error),
                                shouldLogout = shouldLogout(resource.error)
                            )
                        }
                    }

                    is Resource.Loading -> {
                        if (uiState.value.fullName != null) {
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }
                }
            }
        }
    }

    private fun createAlert(error: Throwable): AlertData {
        return when (error) {
            is ApiException -> AlertData(
                title = context.getString(R.string.common_error_title),
                message = error.error.message
            )

            else -> AlertData(
                title = context.getString(R.string.common_error_title),
                message = context.getString(R.string.common_error_description)
            )
        }
    }

    private fun shouldLogout(error: Throwable): Boolean {
        return (error is ApiException) && (error.error.status == "UNKNOWN_TOKEN")
    }

    suspend fun logout() {
        _uiState.update { it.copy(isLoggingOut = true) }
        runCatching {
            logoutUseCase().getOrThrow()
        }
        _uiState.update { it.copy(shouldLogout = true) }
    }

    fun dismissAlert() {
        _uiState.update { it.copy(alertData = null) }
    }

    data class UiState(
        override val isLoading: Boolean = false,
        override val alertData: AlertData? = null,
        override val shouldLogout: Boolean = false,
        val isLoggingOut: Boolean = false,
        val fullName: String? = null,
        val currentTheme: AppTheme = AppTheme.SYSTEM,
        val currentLanguage: AppLanguage = AppLanguage.SYSTEM
    ) : CommonUiState()
}