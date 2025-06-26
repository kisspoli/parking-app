package com.serova.parkingapp.domain.repository

import kotlinx.datetime.LocalDate

interface BookingRequestRepository {
    suspend fun makeBookingRequest(
        date: LocalDate,
        carModel: String,
        stateNumber: String
    ): Result<Unit>

    suspend fun deleteBookingRequest(id: String): Result<Unit>
}