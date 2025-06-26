package com.serova.parkingapp.data.api.model.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class BookingDetailsDto(
    val id: String,
    val date: LocalDate,
    val reservedBy: String?,
    val carModel: String?,
    val stateNumber: String?,
    val row: Int,
    val column: Int,
    val isReserved: Boolean,
    val isReservedByMe: Boolean
)
