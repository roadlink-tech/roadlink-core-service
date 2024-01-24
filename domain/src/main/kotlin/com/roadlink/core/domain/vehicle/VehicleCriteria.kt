package com.roadlink.core.domain.vehicle

import com.roadlink.core.domain.DomainCriteria
import java.util.UUID

data class VehicleCriteria(
    val id: UUID? = null,
    val driverId: UUID? = null
) : DomainCriteria