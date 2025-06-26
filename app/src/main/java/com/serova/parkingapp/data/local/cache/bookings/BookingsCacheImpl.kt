package com.serova.parkingapp.data.local.cache.bookings

import android.util.Log
import com.serova.parkingapp.data.api.model.dto.BookingDto
import com.serova.parkingapp.data.local.database.dao.BookingDao
import com.serova.parkingapp.data.mapper.BookingMapper.toDto
import com.serova.parkingapp.data.mapper.BookingMapper.toEntity
import javax.inject.Inject

class BookingsCacheImpl @Inject constructor(
    private val bookingDao: BookingDao
) : BookingsCache {

    private val tag = this.javaClass.simpleName

    override suspend fun getBookings(): List<BookingDto> {
        val entities = bookingDao.getAll().also {
            Log.i(tag, "Retrieved ${it.size} bookings from database")
        }

        return entities.map { toDto(it) }
    }

    override suspend fun saveBookings(bookings: List<BookingDto>) {
        val entities = bookings.map { toEntity(it) }

        bookingDao.fullSync(entities)
        Log.i(tag, "Saved ${bookings.size} bookings to database")
    }

    override suspend fun clear() {
        bookingDao.clear()
        Log.i(tag, "Cleared bookings database")
    }
}