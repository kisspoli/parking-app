package com.serova.parkingapp.data.api

import com.serova.parkingapp.data.api.model.request.BookingRequestRequest
import com.serova.parkingapp.data.api.model.response.BookingRequestResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

interface BookingRequestApi {
    @POST("parking/booking_request")
    suspend fun makeBooking(
        @Body request: BookingRequestRequest
    ): BookingRequestResponse

    @DELETE("parking/booking_request")
    suspend fun deleteBooking(
        @Header("bookingId") id: String
    ): BookingRequestResponse
}