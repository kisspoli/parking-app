package com.serova.parkingapp.domain.repository

import com.serova.parkingapp.domain.model.booking.Booking
import com.serova.parkingapp.domain.model.resource.Resource
import kotlinx.coroutines.flow.Flow

interface BookingsRepository {
    suspend fun getBookings(
        respectCache: Boolean,
        forceUpdate: Boolean
    ): Flow<Resource<List<Booking>>>
}