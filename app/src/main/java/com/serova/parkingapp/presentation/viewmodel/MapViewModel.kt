package com.serova.parkingapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serova.parkingapp.R
import com.serova.parkingapp.domain.model.bookingdetails.BookingDetails
import com.serova.parkingapp.domain.model.exception.ApiException
import com.serova.parkingapp.domain.usecase.bookingsdetails.BookingsDetailsUseCase
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
class MapViewModel @Inject constructor(
    private val bookingsDetailsUseCase: BookingsDetailsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadMap(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = runCatching {
                bookingsDetailsUseCase(date).getOrThrow()
            }

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isError = false,
                            bookingsDetails = result.getOrNull() ?: emptyList()
                        )
                    }
                },
                onFailure = { error ->
                    when (error) {
                        else -> _uiState.update {
                            it.copy(
                                isError = true,
                                alertData = createAlert(error)
                            )
                        }
                    }
                }
            )

            _uiState.update { it.copy(isLoading = false) }
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

    fun dismissAlert() {
        _uiState.update { it.copy(alertData = null) }
    }

    data class UiState(
        override val isLoading: Boolean = false,
        override val alertData: AlertData? = null,
        override val shouldLogout: Boolean = false,
        val isError: Boolean = false,
        val bookingsDetails: List<BookingDetails> = emptyList()
    ) : CommonUiState()
}