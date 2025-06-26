package com.serova.parkingapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serova.parkingapp.R
import com.serova.parkingapp.domain.model.booking.Booking
import com.serova.parkingapp.domain.model.exception.ApiException
import com.serova.parkingapp.domain.model.resource.Resource
import com.serova.parkingapp.domain.usecase.bookingrequest.BookingRequestUseCase
import com.serova.parkingapp.domain.usecase.bookings.BookingsUseCase
import com.serova.parkingapp.presentation.viewmodel.data.AlertData
import com.serova.parkingapp.presentation.viewmodel.state.CommonUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyBookingsViewModel @Inject constructor(
    private val bookingsUseCase: BookingsUseCase,
    private val bookingRequestUseCase: BookingRequestUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            bookingsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                bookings = resource.data,
                                isLoading = false
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                bookings = resource.data ?: it.bookings,
                                isLoading = false,
                                alertData = createAlert(resource.error),
                                shouldLogout = shouldLogout(resource.error)
                            )
                        }
                    }

                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun deleteBooking(booking: Booking) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            hideDeletionDialog()
            _uiState.update {
                it.copy(bookings = _uiState.value.bookings.filter { it.id != booking.id })
            }
            val result = runCatching {
                bookingRequestUseCase.deleteBooking(booking.id).getOrThrow()
            }

            result.fold(
                onSuccess = { },
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
            refreshData()
        }
    }

    fun dismissAlert() {
        viewModelScope.launch { _uiState.update { it.copy(alertData = null) } }
    }

    fun showDeletionDialog(booking: Booking) {
        _uiState.update { it.copy(deletionDialogState = booking) }
    }

    fun hideDeletionDialog() {
        _uiState.update { it.copy(deletionDialogState = null) }
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

    data class UiState(
        override val isLoading: Boolean = false,
        override val alertData: AlertData? = null,
        override val shouldLogout: Boolean = false,
        val bookings: List<Booking> = emptyList(),
        val deletionDialogState: Booking? = null
    ) : CommonUiState()
}