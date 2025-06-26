package com.serova.parkingapp.data.api.model.response

import com.serova.parkingapp.data.api.model.dto.BookingDto
import kotlinx.serialization.Serializable

@Serializable
data class BookingsResponse(
    val bookings: List<BookingDto>,
    val status: String,
    val requestId: String
)