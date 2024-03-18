package com.roadlink.core.api.feedback

import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import java.util.UUID

object FeedbackSolicitudeFactory {

    fun common(
        id: UUID = UUID.randomUUID(),
        tripLegId: UUID = UUID.randomUUID(),
        receiverId: UUID = UUID.randomUUID(),
        reviewerId: UUID = UUID.randomUUID(),
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