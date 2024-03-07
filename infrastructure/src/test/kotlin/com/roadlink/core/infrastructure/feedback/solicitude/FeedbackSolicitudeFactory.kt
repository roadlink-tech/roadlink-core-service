package com.roadlink.core.infrastructure.feedback.solicitude

import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import java.util.*

object FeedbackSolicitudeFactory {

    fun custom(
        id: UUID = UUID.randomUUID(),
        tripLegId: UUID = UUID.randomUUID(),
        reviewerId: UUID = UUID.randomUUID(),
        receiverId: UUID = UUID.randomUUID(),
        status: FeedbackSolicitude.Status = FeedbackSolicitude.Status.PENDING
    ): FeedbackSolicitude {
        return FeedbackSolicitude(
            id = id,
            tripLegId = tripLegId,
            receiverId = receiverId,
            reviewerId = reviewerId,
            status = status
        )
    }
}