package com.sean.inventorymanager.data

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        val output = date.toString()
        return output
    }

    @TypeConverter
    fun toLocalDate(x: String): LocalDate {
        val localDate = LocalDate.parse(x)
        return localDate
    }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String {
        val output = dateTime.toString()
        return output
    }

    @TypeConverter
    fun toLocalDateTime(x: String): LocalDateTime {
        val localDateTime = LocalDateTime.parse(x)
        return localDateTime
    }
}