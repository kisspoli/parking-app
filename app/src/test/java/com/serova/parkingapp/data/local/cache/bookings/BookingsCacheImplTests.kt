package com.serova.parkingapp.data.local.cache.bookings

import com.serova.parkingapp.data.api.model.dto.BookingDto
import com.serova.parkingapp.data.local.database.dao.BookingDao
import com.serova.parkingapp.data.local.database.entity.BookingEntity
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class BookingsCacheImplTests {

    @Mock
    private lateinit var bookingDao: BookingDao

    @InjectMocks
    private lateinit var bookingsCache: BookingsCacheImpl

    private val testDate = LocalDate.parse("2025-07-05")

    private val testDto = BookingDto(
        id = "booking-123",
        date = testDate,
        carModel = "Toyota Camry",
        stateNumber = "A123BC",
        row = 1,
        column = 2,
        isConfirmed = true
    )

    private val testEntity = BookingEntity(
        id = "booking-123",
        date = testDate,
        carModel = "Toyota Camry",
        stateNumber = "A123BC",
        row = 1,
        column = 2,
        isConfirmed = true
    )

    @Test
    fun `getBookings should return mapped DTOs`() = runBlocking {
        // Arrange
        val entities = listOf(testEntity)
        whenever(bookingDao.getAll()).thenReturn(entities)

        // Act
        val result = bookingsCache.getBookings()

        // Assert
        verify(bookingDao).getAll()
        assertEquals(1, result.size)

        val booking = result.first()
        assertEquals("booking-123", booking.id)
        assertEquals(testDate, booking.date)
        assertEquals("Toyota Camry", booking.carModel)
        assertEquals("A123BC", booking.stateNumber)
        assertEquals(1, booking.row)
        assertEquals(2, booking.column)
        assertEquals(true, booking.isConfirmed)
    }

    @Test
    fun `saveBookings should convert and save entities`() = runBlocking {
        // Arrange
        val entitiesCaptor = argumentCaptor<List<BookingEntity>>()
        val dtos = listOf(testDto)

        // Act
        bookingsCache.saveBookings(dtos)

        // Assert
        verify(bookingDao).fullSync(entitiesCaptor.capture())

        val savedEntities = entitiesCaptor.firstValue
        assertEquals(1, savedEntities.size)

        val entity = savedEntities.first()
        assertEquals("booking-123", entity.id)
        assertEquals(testDate, entity.date)
        assertEquals("Toyota Camry", entity.carModel)
        assertEquals("A123BC", entity.stateNumber)
        assertEquals(1, entity.row)
        assertEquals(2, entity.column)
        assertEquals(true, entity.isConfirmed)
    }

    @Test
    fun `saveBookings with empty list should call fullSync with empty list`() = runBlocking {
        // Arrange
        val emptyList = emptyList<BookingDto>()

        // Act
        bookingsCache.saveBookings(emptyList)

        // Assert
        verify(bookingDao).fullSync(emptyList())
    }

    @Test
    fun `clear should call dao clear method`() = runBlocking {
        // Act
        bookingsCache.clear()

        // Assert
        verify(bookingDao).clear()
    }

    @Test
    fun `getBookings should return empty list when dao returns empty`() = runBlocking {
        // Arrange
        whenever(bookingDao.getAll()).thenReturn(emptyList())

        // Act
        val result = bookingsCache.getBookings()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `saveBookings should handle multiple items`() = runBlocking {
        // Arrange
        val entitiesCaptor = argumentCaptor<List<BookingEntity>>()
        val dtos = listOf(
            testDto.copy(id = "1"),
            testDto.copy(id = "2"),
            testDto.copy(id = "3")
        )

        // Act
        bookingsCache.saveBookings(dtos)

        // Assert
        verify(bookingDao).fullSync(entitiesCaptor.capture())
        assertEquals(3, entitiesCaptor.firstValue.size)
        assertEquals(listOf("1", "2", "3"), entitiesCaptor.firstValue.map { it.id })
    }
}