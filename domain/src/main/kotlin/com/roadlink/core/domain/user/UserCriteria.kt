package com.roadlink.core.domain.user

import com.roadlink.core.domain.DomainCriteria
import java.util.*

data class UserCriteria(
    val id: UUID? = null,
    val email: String = "",
    val userName: String = ""
) : DomainCriteria