package com.serova.parkingapp.domain.repository

import com.serova.parkingapp.domain.model.bookingdetails.BookingDetails
import kotlinx.datetime.LocalDate

interface BookingsDetailsRepository {
    suspend fun getBookings(
        date: LocalDate
    ): Result<List<BookingDetails>>
}