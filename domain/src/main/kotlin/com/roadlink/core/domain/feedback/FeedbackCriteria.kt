package com.roadlink.core.domain.feedback

import com.roadlink.core.domain.DomainCriteria
import java.util.*

class FeedbackCriteria(
    val id: UUID? = null,
    val rating: Int = 0,
    val receiverId: UUID? = null,
    val reviewerId: UUID? = null,
    val tripId: UUID? = null
) : DomainCriteria