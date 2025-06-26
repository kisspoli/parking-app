package com.serova.parkingapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serova.parkingapp.R
import com.serova.parkingapp.domain.model.exception.ApiException
import com.serova.parkingapp.domain.usecase.auth.LoginUseCase
import com.serova.parkingapp.domain.usecase.auth.RefreshAuthUseCase
import com.serova.parkingapp.presentation.viewmodel.data.AlertData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val refreshAuthUseCase: RefreshAuthUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        tryAutoLogin()
    }

    private fun tryAutoLogin() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = runCatching {
                refreshAuthUseCase().getOrThrow()
            }

            _uiState.update { it.copy(isLoading = false) }
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isAuthenticated = true
                        )
                    }
                },
                onFailure = { error ->
                    when (error) {
                        is ApiException -> _uiState.update {
                            it.copy(
                                alertData = AlertData(
                                    title = context.getString(R.string.common_error_title),
                                    message = error.error.message
                                )
                            )
                        }
                    }
                }
            )
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = runCatching {
                loginUseCase(username, password).getOrThrow()
            }

            _uiState.update { it.copy(isLoading = false) }
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isAuthenticated = true,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            alertData = when (error) {
                                is ApiException -> AlertData(
                                    title = context.getString(R.string.common_error_title),
                                    message = error.error.message
                                )

                                else -> AlertData(
                                    title = context.getString(R.string.common_error_title),
                                    message = context.getString(R.string.common_error_description)
                                )
                            }
                        )
                    }
                }
            )
        }
    }

    fun dismissAlert() {
        _uiState.update { it.copy(alertData = null) }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val isAuthenticated: Boolean = false,
        val alertData: AlertData? = null
    )
}