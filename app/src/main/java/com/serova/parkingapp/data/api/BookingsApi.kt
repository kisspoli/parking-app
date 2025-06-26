package com.serova.parkingapp.data.api

import com.serova.parkingapp.data.api.model.response.BookingsResponse
import retrofit2.http.GET

interface BookingsApi {
    @GET("parking/bookings")
    suspend fun getBookings(): BookingsResponse
}