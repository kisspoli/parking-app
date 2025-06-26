package com.serova.parkingapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val date: LocalDate,
    val carModel: String,
    val stateNumber: String,
    val row: Int?,
    val column: Int?,
    val isConfirmed: Boolean
)