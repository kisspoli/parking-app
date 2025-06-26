package com.serova.parkingapp.data.api.model.request

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class BookingRequestRequest(
    val date: LocalDate,
    val carModel: String,
    val stateNumber: String
)