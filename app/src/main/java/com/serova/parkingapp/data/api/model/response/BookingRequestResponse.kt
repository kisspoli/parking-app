package com.serova.parkingapp.data.api.model.response

import kotlinx.serialization.Serializable

@Serializable
data class BookingRequestResponse(
    val status: String,
    val requestId: String
)