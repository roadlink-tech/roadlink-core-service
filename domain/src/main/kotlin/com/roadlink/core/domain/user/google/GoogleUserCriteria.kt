package com.roadlink.core.domain.user.google

import com.roadlink.core.domain.DomainCriteria

data class GoogleUserCriteria(
    val googleId: String = ""
) : DomainCriteria
