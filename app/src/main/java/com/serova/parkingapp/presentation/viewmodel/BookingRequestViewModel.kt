package com.serova.parkingapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serova.parkingapp.R
import com.serova.parkingapp.domain.model.exception.ApiException
import com.serova.parkingapp.domain.usecase.bookingrequest.BookingRequestUseCase
import com.serova.parkingapp.presentation.viewmodel.data.AlertData
import com.serova.parkingapp.presentation.viewmodel.state.CommonUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class BookingRequestViewModel @Inject constructor(
    private val bookingRequestUseCase: BookingRequestUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun makeBooking(
        date: LocalDate,
        carModel: String,
        stateNumber: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            hideRequestDialog()
            val result = runCatching {
                bookingRequestUseCase.makeBooking(date, carModel, stateNumber).getOrThrow()
            }

            _uiState.update { it.copy(isLoading = false) }
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            alertData = AlertData(
                                title = context.getString(R.string.common_booking_success_title),
                                message = context.getString(R.string.common_booking_sucess_message)
                            ),
                            shouldClearFields = true
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
                                ),
                                shouldLogout = shouldLogout(error)
                            )
                        }

                        else -> _uiState.update {
                            it.copy(
                                alertData = AlertData(
                                    title = context.getString(R.string.common_error_title),
                                    message = context.getString(R.string.common_error_description)
                                ),
                                shouldLogout = shouldLogout(error)
                            )
                        }
                    }
                }
            )
        }
    }

    fun showRequestDialog(requestDialogState: Triple<LocalDate, String, String>) {
        _uiState.update { it.copy(requestDialogState = requestDialogState) }
    }

    fun hideRequestDialog() {
        _uiState.update { it.copy(requestDialogState = null) }
    }

    fun onFieldsCleared() {
        _uiState.update { it.copy(shouldClearFields = false) }
    }

    fun dismissAlert() {
        _uiState.update { it.copy(alertData = null) }
    }

    private fun shouldLogout(error: Throwable): Boolean {
        return (error is ApiException) && (error.error.status == "UNKNOWN_TOKEN")
    }

    data class UiState(
        override val isLoading: Boolean = false,
        override val alertData: AlertData? = null,
        override val shouldLogout: Boolean = false,
        val requestDialogState: Triple<LocalDate, String, String>? = null,
        val shouldClearFields: Boolean = false
    ) : CommonUiState()
}