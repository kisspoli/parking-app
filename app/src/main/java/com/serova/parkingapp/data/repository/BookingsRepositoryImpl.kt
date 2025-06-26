package com.serova.parkingapp.data.repository

import com.serova.parkingapp.data.api.BookingsApi
import com.serova.parkingapp.data.local.cache.bookings.BookingsCache
import com.serova.parkingapp.data.mapper.BookingMapper.toDomain
import com.serova.parkingapp.domain.model.booking.Booking
import com.serova.parkingapp.domain.model.resource.Resource
import com.serova.parkingapp.domain.repository.BookingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookingsRepositoryImpl @Inject constructor(
    private val bookingsApi: BookingsApi,
    private val cache: BookingsCache
) : BookingsRepository {
    override suspend fun getBookings(
        respectCache: Boolean,
        forceUpdate: Boolean
    ): Flow<Resource<List<Booking>>> = flow {
        val cachedBookings = cache.getBookings()
        try {
            if (respectCache && cachedBookings.isNotEmpty()) {
                emit(Resource.Loading())
                emit(Resource.Success(cachedBookings.map { toDomain(it) }
                    .sortedByDescending { it.date }))
            }

            if (forceUpdate || cachedBookings.isEmpty()) {
                emit(Resource.Loading())
                val bookings = bookingsApi.getBookings()
                cache.saveBookings(bookings.bookings)
                emit(Resource.Success(bookings.bookings.map { toDomain(it) }
                    .sortedByDescending { it.date }))
            }

        } catch (e: Exception) {
            emit(Resource.Loading())

            if (respectCache) {
                emit(Resource.Error(cachedBookings.map { toDomain(it) }
                    .sortedByDescending { it.date }, e))
            } else {
                emit(Resource.Error(null, e))
            }
        }
    }
}