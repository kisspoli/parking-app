package com.serova.parkingapp.data.local.cache.bookings

import com.serova.parkingapp.data.api.model.dto.BookingDto
import com.serova.parkingapp.data.local.cache.BaseCache

interface BookingsCache : BaseCache {
    suspend fun getBookings(): List<BookingDto>
    suspend fun saveBookings(bookings: List<BookingDto>)
}