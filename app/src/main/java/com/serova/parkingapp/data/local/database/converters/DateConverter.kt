package com.serova.parkingapp.data.local.database.converters

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format

object DateConverter {
    private val formatter = LocalDate.Formats.ISO

    @TypeConverter
    fun fromString(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, formatter) }
    }

    @TypeConverter
    fun toString(date: LocalDate?): String? {
        return date?.format(formatter)
    }
}