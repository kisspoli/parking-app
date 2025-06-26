package com.serova.parkingapp.data.repository

import com.serova.parkingapp.data.api.BookingRequestApi
import com.serova.parkingapp.data.api.model.request.BookingRequestRequest
import com.serova.parkingapp.data.api.model.response.BookingRequestResponse
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class BookingRequestRepositoryImplTests {

    @Mock
    private lateinit var bookingsRequestApi: BookingRequestApi
    @InjectMocks
    private lateinit var repository: BookingRequestRepositoryImpl

    val successfulResponse = BookingRequestResponse("OK", "requestId")

    @Test
    fun `makeBookingRequest should return success on successful API call`() = runBlocking {
        // Arrange
        val date = LocalDate(2025, 12, 15)
        val carModel = "Toyota Camry"
        val stateNumber = "A123BC"

        whenever(bookingsRequestApi.makeBooking(any())).thenReturn(successfulResponse)

        // Act
        val result = repository.makeBookingRequest(date, carModel, stateNumber)

        // Assert
        assertTrue(result.isSuccess)

        val requestCaptor = argumentCaptor<BookingRequestRequest>()
        verify(bookingsRequestApi).makeBooking(requestCaptor.capture())

        assertEquals(date, requestCaptor.firstValue.date)
        assertEquals(carModel, requestCaptor.firstValue.carModel)
        assertEquals(stateNumber, requestCaptor.firstValue.stateNumber)
    }

    @Test
    fun `makeBookingRequest should return failure on API exception`() = runBlocking {
        // Arrange
        val date = LocalDate(2025, 12, 16)
        val carModel = "Honda Civic"
        val stateNumber = "B234CD"
        val exception = RuntimeException("Network error")

        doThrow(exception).whenever(bookingsRequestApi).makeBooking(any())

        // Act
        val result = repository.makeBookingRequest(date, carModel, stateNumber)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `deleteBookingRequest should return success on successful API call`() = runBlocking {
        // Arrange
        val bookingId = "1"

        whenever(bookingsRequestApi.deleteBooking(bookingId)).thenReturn(successfulResponse)

        // Act
        val result = repository.deleteBookingRequest(bookingId)

        // Assert
        assertTrue(result.isSuccess)
        verify(bookingsRequestApi).deleteBooking(bookingId)
        Unit
    }

    @Test
    fun `deleteBookingRequest should return failure on API exception`() = runBlocking {
        // Arrange
        val bookingId = "1"
        val exception = IllegalStateException("Booking not found")

        doThrow(exception).whenever(bookingsRequestApi).deleteBooking(bookingId)

        // Act
        val result = repository.deleteBookingRequest(bookingId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `deleteBookingRequest should handle empty ID correctly`() = runBlocking {
        // Arrange
        val bookingId = ""
        val exception = IllegalArgumentException("Invalid ID")

        doThrow(exception).whenever(bookingsRequestApi).deleteBooking(bookingId)

        // Act
        val result = repository.deleteBookingRequest(bookingId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `makeBookingRequest should handle empty fields correctly`() = runBlocking {
        // Arrange
        val date = LocalDate(2025, 12, 17)
        val carModel = ""
        val stateNumber = ""



        whenever(bookingsRequestApi.makeBooking(any())).thenReturn(successfulResponse)

        // Act
        val result = repository.makeBookingRequest(date, carModel, stateNumber)

        // Assert
        assertTrue(result.isSuccess)

        val requestCaptor = argumentCaptor<BookingRequestRequest>()
        verify(bookingsRequestApi).makeBooking(requestCaptor.capture())

        assertEquals(date, requestCaptor.firstValue.date)
        assertEquals("", requestCaptor.firstValue.carModel)
        assertEquals("", requestCaptor.firstValue.stateNumber)
    }
}