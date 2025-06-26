package com.serova.parkingapp.data.mapper

import com.serova.parkingapp.data.api.model.dto.BookingDto
import com.serova.parkingapp.data.local.database.entity.BookingEntity
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BookingMapperTests {
    private val confirmedBookingDto = BookingDto(
        id = "1",
        date = LocalDate.parse("2025-07-05"),
        carModel = "Toyota Camry",
        stateNumber = "A123BC",
        row = 3,
        column = 5,
        isConfirmed = true
    )

    private val unconfirmedBookingDto = BookingDto(
        id = "1",
        date = LocalDate.parse("2025-07-05"),
        carModel = "Toyota Camry",
        stateNumber = "A123BC",
        row = null,
        column = null,
        isConfirmed = false
    )

    @Test
    fun `toDomain should map all fields correctly for confirmed booking`() {
        // Act
        val result = BookingMapper.toDomain(confirmedBookingDto)

        // Assert
        assertEquals("1", result.id)
        assertEquals(LocalDate.parse("2025-07-05"), result.date)
        assertEquals("Toyota Camry", result.carModel)
        assertEquals("A123BC", result.stateNumber)
        assertEquals(3, result.row)
        assertEquals(5, result.column)
        assertTrue(result.isConfirmed)
    }

    @Test
    fun `toDomain should map all fields correctly for unconfirmed booking`() {
        // Act
        val result = BookingMapper.toDomain(unconfirmedBookingDto)

        // Assert
        assertEquals("1", result.id)
        assertEquals(LocalDate.parse("2025-07-05"), result.date)
        assertEquals("Toyota Camry", result.carModel)
        assertEquals("A123BC", result.stateNumber)
        assertNull(result.row)
        assertNull(result.column)
        assertFalse(result.isConfirmed)
    }

    @Test
    fun `toEntity should map all fields correctly for confirmed booking`() {
        // Act
        val result = BookingMapper.toEntity(confirmedBookingDto)

        // Assert
        assertEquals("1", result.id)
        assertEquals(LocalDate.parse("2025-07-05"), result.date)
        assertEquals("Toyota Camry", result.carModel)
        assertEquals("A123BC", result.stateNumber)
        assertEquals(3, result.row)
        assertEquals(5, result.column)
        assertTrue(result.isConfirmed)
    }

    @Test
    fun `toEntity should map all fields correctly for unconfirmed booking`() {
        // Act
        val result = BookingMapper.toEntity(unconfirmedBookingDto)

        // Assert
        assertEquals("1", result.id)
        assertEquals(LocalDate.parse("2025-07-05"), result.date)
        assertEquals("Toyota Camry", result.carModel)
        assertEquals("A123BC", result.stateNumber)
        assertNull(result.row)
        assertNull(result.column)
        assertFalse(result.isConfirmed)
    }

    @Test
    fun `toDto should map all fields correctly for confirmed entity`() {
        // Arrange
        val entity = BookingEntity(
            id = "1",
            date = LocalDate.parse("2025-07-05"),
            carModel = "Toyota Camry",
            stateNumber = "A123BC",
            row = 3,
            column = 5,
            isConfirmed = true
        )

        // Act
        val result = BookingMapper.toDto(entity)

        // Assert
        assertEquals("1", result.id)
        assertEquals(LocalDate.parse("2025-07-05"), result.date)
        assertEquals("Toyota Camry", result.carModel)
        assertEquals("A123BC", result.stateNumber)
        assertEquals(3, result.row)
        assertEquals(5, result.column)
        assertTrue(result.isConfirmed)
    }

    @Test
    fun `toDto should map all fields correctly for unconfirmed entity`() {
        // Arrange
        val entity = BookingEntity(
            id = "1",
            date = LocalDate.parse("2025-07-05"),
            carModel = "Toyota Camry",
            stateNumber = "A123BC",
            row = null,
            column = null,
            isConfirmed = false
        )

        // Act
        val result = BookingMapper.toDto(entity)

        // Assert
        assertEquals("1", result.id)
        assertEquals(LocalDate.parse("2025-07-05"), result.date)
        assertEquals("Toyota Camry", result.carModel)
        assertEquals("A123BC", result.stateNumber)
        assertNull(result.row)
        assertNull(result.column)
        assertFalse(result.isConfirmed)
    }
}