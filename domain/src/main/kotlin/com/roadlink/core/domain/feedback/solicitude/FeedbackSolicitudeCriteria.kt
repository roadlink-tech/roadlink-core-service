package com.roadlink.core.domain.feedback.solicitude

import com.roadlink.core.domain.DomainCriteria
import java.util.*

class FeedbackSolicitudeCriteria(
    val id: UUID? = null,
    val receiverId: UUID? = null,
    val reviewerId: UUID? = null,
    val tripLegId: UUID? = null,
    val status: FeedbackSolicitude.Status? = null
) : DomainCriteria