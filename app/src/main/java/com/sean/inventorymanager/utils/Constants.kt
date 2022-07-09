package com.sean.inventorymanager.utils

import java.time.format.DateTimeFormatter

class Constants {

    companion object {
        private const val TEST_DATE_FORMAT = "E, MMMM d, yyyy"
        private const val DATE_FORMAT = "M/d/yyyy"
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(TEST_DATE_FORMAT)

        private const val TIME_FORMAT = "h:mm a"
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT)

        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("$DATE_FORMAT $TIME_FORMAT")
    }
}