package com.roadlink.core.infrastructure

import com.roadlink.application.LocalDateTimeHandler
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DefaultLocalDateTimeHandler : LocalDateTimeHandler {
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    override fun from(dateTime: String): LocalDate? {
        return if (dateTime.isNotEmpty()) {
            LocalDate.parse(dateTime, formatter)
        } else {
            null
        }
    }

    override fun toString(dateTime: LocalDate?): String {
        return if (dateTime != null) {
            dateTime.format(formatter)
        } else {
            ""
        }
    }
}