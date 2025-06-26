package com.serova.parkingapp.data.mapper

import com.serova.parkingapp.data.api.model.dto.BookingDto
import com.serova.parkingapp.data.local.database.entity.BookingEntity
import com.serova.parkingapp.domain.model.booking.Booking

object BookingMapper {
    fun toDomain(dto: BookingDto): Booking {
        return Booking(
            id = dto.id,
            date = dto.date,
            carModel = dto.carModel,
            stateNumber = dto.stateNumber,
            row = dto.row,
            column = dto.column,
            isConfirmed = dto.isConfirmed
        )
    }

    fun toEntity(dto: BookingDto): BookingEntity {
        return BookingEntity(
            id = dto.id,
            date = dto.date,
            carModel = dto.carModel,
            stateNumber = dto.stateNumber,
            row = dto.row,
            column = dto.column,
            isConfirmed = dto.isConfirmed
        )
    }

    fun toDto(entity: BookingEntity): BookingDto {
        return BookingDto(
            id = entity.id,
            date = entity.date,
            carModel = entity.carModel,
            stateNumber = entity.stateNumber,
            row = entity.row,
            column = entity.column,
            isConfirmed = entity.isConfirmed
        )
    }
}