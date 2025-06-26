package com.serova.parkingapp.data.mapper

import com.serova.parkingapp.data.api.model.dto.BookingDetailsDto
import com.serova.parkingapp.domain.model.bookingdetails.BookingDetails

object BookingDetailsMapper {
    fun toDomain(dto: BookingDetailsDto): BookingDetails {
        return BookingDetails(
            id = dto.id,
            date = dto.date,
            reservedBy = dto.reservedBy,
            carModel = dto.carModel,
            stateNumber = dto.stateNumber,
            row = dto.row,
            column = dto.column,
            isReserved = dto.isReserved,
            isReservedByMe = dto.isReservedByMe,
        )
    }
}