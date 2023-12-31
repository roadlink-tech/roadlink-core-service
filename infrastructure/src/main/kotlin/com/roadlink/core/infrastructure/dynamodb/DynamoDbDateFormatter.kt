package com.roadlink.core.infrastructure.dynamodb

import java.text.SimpleDateFormat
import java.util.*

object DynamoDbDateFormatter {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    fun instance(): SimpleDateFormat {
        dateFormatter.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormatter
    }
}