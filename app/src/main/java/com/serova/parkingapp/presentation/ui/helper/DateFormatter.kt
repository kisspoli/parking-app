package com.serova.parkingapp.presentation.ui.helper

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

fun LocalDate.euFormatted(): String {
    return format(
        LocalDate.Format {
            dayOfMonth(); char('.'); monthNumber(); char('.'); year()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
fun DatePickerState.toLocalDate(): LocalDate? {
    return selectedDateMillis?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
    }
}