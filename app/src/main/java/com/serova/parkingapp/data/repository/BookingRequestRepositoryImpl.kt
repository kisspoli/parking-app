package com.serova.parkingapp.data.repository

import android.util.Log
import com.serova.parkingapp.data.api.BookingRequestApi
import com.serova.parkingapp.data.api.model.request.BookingRequestRequest
import com.serova.parkingapp.domain.repository.BookingRequestRepository
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class BookingRequestRepositoryImpl @Inject constructor(
    private val bookingsRequestApi: BookingRequestApi
) : BookingRequestRepository {

    private val tag = this.javaClass.simpleName

    override suspend fun makeBookingRequest(
        date: LocalDate,
        carModel: String,
        stateNumber: String
    ): Result<Unit> {
        Log.d(tag, "Making booking request: date=$date, car=$carModel")
        return try {
            bookingsRequestApi.makeBooking(BookingRequestRequest(date, carModel, stateNumber))
            Log.i(tag, "Booking request created successfully for $date, car=$carModel")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to create booking for $date. Car: $carModel", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteBookingRequest(id: String): Result<Unit> {
        Log.d(tag, "Deleting booking request ID: $id")
        return try {
            bookingsRequestApi.deleteBooking(id)
            Log.i(tag, "Booking request $id deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to delete booking request $id", e)
            Result.failure(e)
        }
    }
}