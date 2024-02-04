package com.roadlink.application

import java.time.LocalDate

interface LocalDateTimeHandler {

    fun from(dateTime: String): LocalDate?

    fun toString(dateTime: LocalDate?): String
}