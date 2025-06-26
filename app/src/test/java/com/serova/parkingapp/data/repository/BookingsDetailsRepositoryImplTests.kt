package com.serova.parkingapp.data.repository

import com.serova.parkingapp.data.api.BookingsDetailsApi
import com.serova.parkingapp.data.api.model.dto.BookingDetailsDto
import com.serova.parkingapp.data.api.model.response.BookingsDetailsResponse
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import java.io.IOException
import java.io.UncheckedIOException

@ExtendWith(MockitoExtension::class)
class BookingsDetailsRepositoryImplTests {

    @Mock
    private lateinit var bookingsDetailsApi: BookingsDetailsApi
    @InjectMocks
    private lateinit var repository: BookingsDetailsRepositoryImpl

    @Test
    fun `getBookings should return success with bookings on successful API call`() = runBlocking {
        // Arrange
        val date = LocalDate(2025, 12, 12)
        val apiResponse = BookingsDetailsResponse(
            bookingsDetails = listOf(
                createTestBookingDetailsDto("1"),
                createTestBookingDetailsDto("2")
            ),
            status = "OK",
            requestId = "requestId"
        )

        whenever(bookingsDetailsApi.getBookingsDetails(date)).thenReturn(apiResponse)

        // Act
        val result = repository.getBookings(date)

        // Assert
        assertTrue(result.isSuccess)
        val bookings = result.getOrNull()!!
        assertEquals(2, bookings.size)

        assertEquals("1", bookings[0].id)
        assertEquals(date, bookings[0].date)

        assertEquals("2", bookings[1].id)
        assertEquals(date, bookings[1].date)
    }

    @Test
    fun `getBookings should return empty list when no bookings available`() = runBlocking {
        // Arrange
        val date = LocalDate(2025, 12, 12)
        val apiResponse = BookingsDetailsResponse(
            bookingsDetails = emptyList(),
            status = "OK",
            requestId = "requestId"
        )

        whenever(bookingsDetailsApi.getBookingsDetails(date)).thenReturn(apiResponse)

        // Act
        val result = repository.getBookings(date)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun `getBookings should return failure on API exception`() = runBlocking {
        // Arrange
        val date = LocalDate(2025, 12, 12)
        val exception = UncheckedIOException(IOException("Network error"))

        doThrow(exception).whenever(bookingsDetailsApi).getBookingsDetails(date)

        // Act
        val result = repository.getBookings(date)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getBookings should handle different exception types`() = runBlocking {
        // Arrange
        val date = LocalDate(2025, 12, 12)
        val exceptions = listOf(
            RuntimeException("Server error"),
            IllegalStateException("Invalid state"),
            NullPointerException("Unexpected null")
        )

        for (exception in exceptions) {
            doThrow(exception).whenever(bookingsDetailsApi).getBookingsDetails(date)

            // Act
            val result = repository.getBookings(date)

            // Assert
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }
    }

    @Test
    fun `getBookings should map all fields correctly`() = runBlocking {
        // Arrange
        val date = LocalDate(2025, 12, 12)
        val apiResponse = BookingsDetailsResponse(
            bookingsDetails = listOf(
                createTestBookingDetailsDto(
                    id = "1",
                    reservedBy = "Иванов И.И.",
                    carModel = "Toyota Camry",
                    stateNumber = "A123BC",
                    row = 3,
                    column = 5,
                    isReserved = true,
                    isReservedByMe = true
                ),
            ),
            status = "OK",
            requestId = "requestId"
        )

        whenever(bookingsDetailsApi.getBookingsDetails(date)).thenReturn(apiResponse)

        // Act
        val result = repository.getBookings(date)

        // Assert
        assertTrue(result.isSuccess)
        val booking = result.getOrNull()!!.first()

        assertEquals("1", booking.id)
        assertEquals(date, booking.date)
        assertEquals("Иванов И.И.", booking.reservedBy)
        assertEquals("Toyota Camry", booking.carModel)
        assertEquals("A123BC", booking.stateNumber)
        assertEquals(3, booking.row)
        assertEquals(5, booking.column)
        assertTrue(booking.isReserved)
        assertTrue(booking.isReservedByMe)
    }

    private fun createTestBookingDetailsDto(
        id: String = "test-id",
        date: LocalDate = LocalDate(2025, 12, 12),
        reservedBy: String? = "test-user",
        carModel: String? = "test-model",
        stateNumber: String? = "test-number",
        row: Int = 1,
        column: Int = 1,
        isReserved: Boolean = true,
        isReservedByMe: Boolean = false
    ) = BookingDetailsDto(
        id = id,
        date = date,
        reservedBy = reservedBy,
        carModel = carModel,
        stateNumber = stateNumber,
        row = row,
        column = column,
        isReserved = isReserved,
        isReservedByMe = isReservedByMe
    )
}