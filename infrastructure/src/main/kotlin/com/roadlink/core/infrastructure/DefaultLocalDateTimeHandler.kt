package com.roadlink.core.infrastructure

import java.time.LocalDate
import java.time.format.DateTimeFormatter

// TODO mover esto al modulo de aplicación
object DefaultLocalDateTimeHandler  {
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