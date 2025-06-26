package com.serova.parkingapp.data.mapper

import com.serova.parkingapp.data.api.model.dto.BookingDetailsDto
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BookingDetailsMapperTests {

    @Test
    fun `toDomain should map all fields correctly when reserved`() {
        // Arrange
        val dto = BookingDetailsDto(
            id = "1",
            date = LocalDate.parse("2025-07-05"),
            reservedBy = "Иванов И.И.",
            carModel = "Toyota Camry",
            stateNumber = "A123BC",
            row = 3,
            column = 5,
            isReserved = true,
            isReservedByMe = true
        )

        // Act
        val result = BookingDetailsMapper.toDomain(dto)

        // Assert
        assertEquals("1", result.id)
        assertEquals(LocalDate.parse("2025-07-05"), result.date)
        assertEquals("Иванов И.И.", result.reservedBy)
        assertEquals("Toyota Camry", result.carModel)
        assertEquals("A123BC", result.stateNumber)
        assertEquals(3, result.row)
        assertEquals(5, result.column)
        assertTrue(result.isReserved)
        assertTrue(result.isReservedByMe)
    }

    @Test
    fun `toDomain should handle nulls correctly when not reserved`() {
        // Arrange
        val dto = BookingDetailsDto(
            id = "1",
            date = LocalDate.parse("2025-07-05"),
            reservedBy = null,
            carModel = null,
            stateNumber = null,
            row = 2,
            column = 4,
            isReserved = false,
            isReservedByMe = false
        )

        // Act
        val result = BookingDetailsMapper.toDomain(dto)

        // Assert
        assertEquals("1", result.id)
        assertEquals(LocalDate.parse("2025-07-05"), result.date)
        assertNull(result.reservedBy)
        assertNull(result.carModel)
        assertNull(result.stateNumber)
        assertEquals(2, result.row)
        assertEquals(4, result.column)
        assertFalse(result.isReserved)
        assertFalse(result.isReservedByMe)
    }
}