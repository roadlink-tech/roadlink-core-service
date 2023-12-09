package com.roadlink.core.domain.user.google

import com.roadlink.core.domain.DomainEntity
import java.util.UUID

data class GoogleUser(
    val googleId: String = "",
    val userId: UUID
) : DomainEntity
