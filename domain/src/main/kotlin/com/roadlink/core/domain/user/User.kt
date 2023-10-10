package com.roadlink.core.domain.user

import java.util.*

data class User(
    val id: UUID,
    val email: String,
    val firstName: String,
    val lastName: String
)