package com.serova.parkingapp.data.api

import com.serova.parkingapp.data.api.model.response.BookingsDetailsResponse
import kotlinx.datetime.LocalDate
import retrofit2.http.GET
import retrofit2.http.Header

interface BookingsDetailsApi {
    @GET("parking/bookings/details")
    suspend fun getBookingsDetails(
        @Header("date") date: LocalDate
    ): BookingsDetailsResponse
}