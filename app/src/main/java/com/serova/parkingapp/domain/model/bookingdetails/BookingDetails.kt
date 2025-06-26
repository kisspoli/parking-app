package com.serova.parkingapp.domain.model.bookingdetails

import kotlinx.datetime.LocalDate

data class BookingDetails(
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
