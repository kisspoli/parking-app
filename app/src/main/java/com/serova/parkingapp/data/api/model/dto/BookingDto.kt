package com.serova.parkingapp.data.api.model.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class BookingDto(
    val id: String,
    val date: LocalDate,
    val carModel: String,
    val stateNumber: String,
    val row: Int?,
    val column: Int?,
    val isConfirmed: Boolean
)
