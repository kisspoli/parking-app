package com.serova.parkingapp.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.serova.parkingapp.data.local.database.entity.BookingEntity

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings")
    suspend fun getAll(): List<BookingEntity>

    @Upsert
    suspend fun insertOrIgnore(bookings: List<BookingEntity>)

    @Update
    suspend fun update(booking: BookingEntity)

    @Query("DELETE FROM bookings WHERE id NOT IN (:validIds)")
    suspend fun deleteNotIn(validIds: List<String>)

    @Query("DELETE FROM bookings")
    suspend fun clear()

    @Transaction
    suspend fun fullSync(bookings: List<BookingEntity>) {

        val existingIds = getAll().map { it.id }
        val newIds = bookings.map { it.id }

        deleteNotIn(newIds)

        val toInsert = mutableListOf<BookingEntity>()
        val toUpdate = mutableListOf<BookingEntity>()

        bookings.forEach { booking ->
            if (existingIds.contains(booking.id)) {
                toUpdate.add(booking)
            } else {
                toInsert.add(booking)
            }
        }

        if (toInsert.isNotEmpty()) insertOrIgnore(toInsert)
        if (toUpdate.isNotEmpty()) toUpdate.forEach { update(it) }
    }
}