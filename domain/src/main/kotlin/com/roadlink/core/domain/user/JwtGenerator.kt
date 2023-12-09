package com.roadlink.core.domain.user

import java.util.UUID

interface JwtGenerator {
    fun generate(userId: UUID): String
}