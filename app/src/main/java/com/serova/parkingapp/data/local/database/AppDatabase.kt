package com.serova.parkingapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.serova.parkingapp.data.local.database.converters.DateConverter
import com.serova.parkingapp.data.local.database.dao.BookingDao
import com.serova.parkingapp.data.local.database.entity.BookingEntity

@Database(
    entities = [BookingEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookingDao(): BookingDao
}