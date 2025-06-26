package com.serova.parkingapp.domain.model.booking

import kotlinx.datetime.LocalDate

data class Booking(
    val id: String,
    val date: LocalDate,
    val carModel: String,
    val stateNumber: String,
    val row: Int?,
    val column: Int?,
    val isConfirmed: Boolean
)
