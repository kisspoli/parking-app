package com.serova.parkingapp.data.local.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.serova.parkingapp.data.local.database.AppDatabase
import com.serova.parkingapp.data.local.database.entity.BookingEntity
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.robolectric.annotation.Config
import tech.apter.junit.jupiter.robolectric.RobolectricExtension

@ExtendWith(RobolectricExtension::class)
@Config(sdk = [Config.OLDEST_SDK])
class BookingDaoTests {

    private lateinit var database: AppDatabase
    private lateinit var bookingDao: BookingDao

    @BeforeEach
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        bookingDao = database.bookingDao()
    }

    @AfterEach
    fun tearDown() {
        database.close()
    }

    private fun createBookingEntity(id: String): BookingEntity {
        return BookingEntity(
            id = id,
            date = LocalDate.parse("2025-05-01"),
            carModel = "Toyota Camry",
            stateNumber = "A123BC",
            row = 1,
            column = 2,
            isConfirmed = true
        )
    }

    @Test
    fun `fullSync should insert new bookings`() = runBlocking {
        // Arrange
        val newBookings = listOf(
            createBookingEntity("1"),
            createBookingEntity("2")
        )

        // Act
        bookingDao.fullSync(newBookings)

        // Assert
        val allBookings = bookingDao.getAll()
        assertEquals(2, allBookings.size)
        assertEquals("1", allBookings[0].id)
        assertEquals("2", allBookings[1].id)

        val booking1 = allBookings.find { it.id == "1" }
        assertEquals(LocalDate.parse("2025-05-01"), booking1?.date)
        assertEquals("Toyota Camry", booking1?.carModel)
        assertEquals("A123BC", booking1?.stateNumber)
        assertEquals(1, booking1?.row)
        assertEquals(2, booking1?.column)
        assertEquals(true, booking1?.isConfirmed)
    }

    @Test
    fun `fullSync should update existing bookings`() = runBlocking {
        // Arrange
        bookingDao.insertOrIgnore(
            listOf(
                createBookingEntity("1").copy(carModel = "Old Model")
            )
        )

        val updatedBookings = listOf(
            createBookingEntity("1").copy(carModel = "New Model")
        )

        // Act
        bookingDao.fullSync(updatedBookings)

        // Assert
        val booking = bookingDao.getAll().first()
        assertEquals("New Model", booking.carModel)

        assertEquals(LocalDate.parse("2025-05-01"), booking.date)
        assertEquals("A123BC", booking.stateNumber)
        assertEquals(1, booking.row)
        assertEquals(2, booking.column)
        assertEquals(true, booking.isConfirmed)
    }

    @Test
    fun `fullSync should delete obsolete bookings`() = runBlocking {
        // Arrange
        bookingDao.insertOrIgnore(
            listOf(
                createBookingEntity("1"),
                createBookingEntity("2"),
                createBookingEntity("3")
            )
        )

        val newBookings = listOf(
            createBookingEntity("1"),
            createBookingEntity("2")
        )

        // Act
        bookingDao.fullSync(newBookings)

        // Assert
        val allBookings = bookingDao.getAll()
        assertEquals(2, allBookings.size)
        assertFalse(allBookings.any { it.id == "3" })
    }

    @Test
    fun `fullSync should handle mixed operations`() = runBlocking {
        // Arrange
        bookingDao.insertOrIgnore(
            listOf(
                createBookingEntity("1").copy(carModel = "Old Model"),
                createBookingEntity("3")
            )
        )

        val newBookings = listOf(
            createBookingEntity("1").copy(carModel = "New Model"),
            createBookingEntity("2")
        )

        // Act
        bookingDao.fullSync(newBookings)

        // Assert
        val allBookings = bookingDao.getAll().sortedBy { it.id }
        assertEquals(2, allBookings.size)

        val booking1 = allBookings[0]
        assertEquals("1", booking1.id)
        assertEquals("New Model", booking1.carModel)

        val booking2 = allBookings[1]
        assertEquals("2", booking2.id)
        assertEquals("Toyota Camry", booking2.carModel)

        assertFalse(allBookings.any { it.id == "3" })
    }

    @Test
    fun `fullSync should handle empty new bookings`() = runBlocking {
        // Arrange
        bookingDao.insertOrIgnore(
            listOf(
                createBookingEntity("1"),
                createBookingEntity("2")
            )
        )

        // Act
        bookingDao.fullSync(emptyList())

        // Assert
        val allBookings = bookingDao.getAll()
        assertTrue(allBookings.isEmpty())
    }

    @Test
    fun `fullSync should handle all new bookings`() = runBlocking {
        // Arrange
        val newBookings = listOf(
            createBookingEntity("1"),
            createBookingEntity("2")
        )

        // Act
        bookingDao.fullSync(newBookings)

        // Assert
        val allBookings = bookingDao.getAll()
        assertEquals(2, allBookings.size)
    }

    @Test
    fun `fullSync should update multiple fields correctly`() = runBlocking {
        // Arrange
        bookingDao.insertOrIgnore(
            listOf(
                createBookingEntity("1")
            )
        )

        val updatedBooking = createBookingEntity("1").copy(
            carModel = "Honda Accord",
            stateNumber = "B456CD",
            row = 3,
            column = 4,
            isConfirmed = false
        )

        // Act
        bookingDao.fullSync(listOf(updatedBooking))

        // Assert
        val booking = bookingDao.getAll().first()
        assertEquals("Honda Accord", booking.carModel)
        assertEquals("B456CD", booking.stateNumber)
        assertEquals(3, booking.row)
        assertEquals(4, booking.column)
        assertEquals(false, booking.isConfirmed)
    }

    @Test
    fun `fullSync should preserve unchanged fields during update`() = runBlocking {
        // Arrange
        val initialBooking = createBookingEntity("1").copy(
            carModel = "Initial Model",
            stateNumber = "INITIAL",
            row = 10,
            column = 20,
            isConfirmed = false
        )
        bookingDao.insertOrIgnore(listOf(initialBooking))

        val updatedBooking = createBookingEntity("1").copy(
            carModel = "Updated Model",
            stateNumber = "INITIAL",
            row = 10,
            column = 20,
            isConfirmed = false
        )

        // Act
        bookingDao.fullSync(listOf(updatedBooking))

        // Assert
        val booking = bookingDao.getAll().first()
        assertEquals("Updated Model", booking.carModel)
        assertEquals("INITIAL", booking.stateNumber)
        assertEquals(10, booking.row)
        assertEquals(20, booking.column)
        assertEquals(false, booking.isConfirmed)
    }
}