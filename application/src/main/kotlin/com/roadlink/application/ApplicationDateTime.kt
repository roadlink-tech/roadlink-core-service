package com.roadlink.application

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ApplicationDateTime {
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    fun from(dateTime: String): LocalDate? {
        return if (dateTime.isNotEmpty()) {
            LocalDate.parse(dateTime, formatter)
        } else {
            null
        }
    }

    fun toString(dateTime: LocalDate?): String {
        return if (dateTime != null) {
            dateTime.format(formatter)
        } else {
            ""
        }
    }
}