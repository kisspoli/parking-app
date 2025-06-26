package com.serova.parkingapp.domain.usecase.bookings

import com.serova.parkingapp.domain.model.booking.Booking
import com.serova.parkingapp.domain.model.resource.Resource
import com.serova.parkingapp.domain.repository.BookingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookingsUseCase @Inject constructor(
    private val repository: BookingsRepository
) {
    suspend operator fun invoke(
        respectCache: Boolean = true,
        forceUpdate: Boolean = true
    ): Flow<Resource<List<Booking>>> {
        return repository.getBookings(respectCache, forceUpdate)
    }
}