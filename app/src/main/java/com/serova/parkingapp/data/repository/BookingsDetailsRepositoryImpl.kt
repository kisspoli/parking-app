package com.serova.parkingapp.data.repository

import android.util.Log
import com.serova.parkingapp.data.api.BookingsDetailsApi
import com.serova.parkingapp.data.mapper.BookingDetailsMapper.toDomain
import com.serova.parkingapp.domain.model.bookingdetails.BookingDetails
import com.serova.parkingapp.domain.repository.BookingsDetailsRepository
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class BookingsDetailsRepositoryImpl @Inject constructor(
    private val bookingsDetailsApi: BookingsDetailsApi
) : BookingsDetailsRepository {

    private val tag = this.javaClass.simpleName

    override suspend fun getBookings(date: LocalDate): Result<List<BookingDetails>> {
        Log.d(tag, "Fetching bookings for date: $date")
        try {
            val result = bookingsDetailsApi.getBookingsDetails(date)
            Log.i(tag, "Successfully fetched ${result.bookingsDetails.size} bookings for $date")
            return Result.success(result.bookingsDetails.map { toDomain(it) })
        } catch (e: Exception) {
            Log.e(tag, "Failed to fetch bookings for $date", e)
            return Result.failure(e)
        }
    }
}