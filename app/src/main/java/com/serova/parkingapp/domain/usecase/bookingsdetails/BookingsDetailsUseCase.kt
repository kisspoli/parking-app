package com.serova.parkingapp.domain.usecase.bookingsdetails

import com.serova.parkingapp.domain.model.bookingdetails.BookingDetails
import com.serova.parkingapp.domain.repository.BookingsDetailsRepository
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class BookingsDetailsUseCase @Inject constructor(
    private val repository: BookingsDetailsRepository
) {
    suspend operator fun invoke(date: LocalDate): Result<List<BookingDetails>> {
        return repository.getBookings(date)
    }
}