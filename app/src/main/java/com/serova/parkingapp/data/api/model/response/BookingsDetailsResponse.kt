package com.serova.parkingapp.data.api.model.response

import com.serova.parkingapp.data.api.model.dto.BookingDetailsDto
import kotlinx.serialization.Serializable

@Serializable
data class BookingsDetailsResponse(
    val bookingsDetails: List<BookingDetailsDto>,
    val status: String,
    val requestId: String
)