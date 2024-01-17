package com.roadlink.core.domain.user

import com.roadlink.core.domain.DomainCriteria
import java.util.*

class UserCriteria(
    val id: UUID? = null,
    val email: String = ""
) : DomainCriteria