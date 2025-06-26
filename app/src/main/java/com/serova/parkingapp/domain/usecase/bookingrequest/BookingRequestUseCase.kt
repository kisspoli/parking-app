package com.serova.parkingapp.domain.usecase.bookingrequest

import com.serova.parkingapp.domain.repository.BookingRequestRepository
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class BookingRequestUseCase @Inject constructor(
    private val repository: BookingRequestRepository
) {
    suspend fun makeBooking(
        date: LocalDate,
        carModel: String,
        stateNumber: String
    ): Result<Unit> {
        return repository.makeBookingRequest(date, carModel, stateNumber)
    }

    suspend fun deleteBooking(id: String): Result<Unit> {
        return repository.deleteBookingRequest(id)
    }
}