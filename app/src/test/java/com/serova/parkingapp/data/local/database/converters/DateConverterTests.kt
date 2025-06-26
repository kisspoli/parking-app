package com.serova.parkingapp.data.local.database.converters

import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull

class DateConverterTests {

    @Test
    fun `fromString should parse valid ISO date string`() {
        // Arrange
        val validDateString = "2025-07-15"

        // Act
        val result = DateConverter.fromString(validDateString)

        // Assert
        assertEquals(LocalDate(2025, 7, 15), result)
    }

    @Test
    fun `fromString should return null for null input`() {
        // Act
        val result = DateConverter.fromString(null)

        // Assert
        assertNull(result)
    }

    @Test
    fun `toString should format valid LocalDate to ISO string`() {
        // Arrange
        val date = LocalDate(2025, 12, 31)

        // Act
        val result = DateConverter.toString(date)

        // Assert
        assertEquals("2025-12-31", result)
    }

    @Test
    fun `toString should return null for null input`() {
        // Act
        val result = DateConverter.toString(null)

        // Assert
        assertNull(result)
    }

    @Test
    fun `converter should handle round trip conversion`() {
        // Arrange
        val originalDate = LocalDate(2025, 6, 15)

        // Act
        val stringValue = DateConverter.toString(originalDate)
        val convertedDate = DateConverter.fromString(stringValue)

        // Assert
        assertEquals(originalDate, convertedDate)
    }
}