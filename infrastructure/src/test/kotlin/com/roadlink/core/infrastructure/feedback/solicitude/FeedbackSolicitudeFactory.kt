package com.roadlink.core.infrastructure.feedback.solicitude

import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import java.util.*

object FeedbackSolicitudeFactory {

    fun custom(
        id: UUID = UUID.randomUUID(),
        tripId: UUID = UUID.randomUUID(),
        reviewerId: UUID = UUID.randomUUID(),
        receiverId: UUID = UUID.randomUUID(),
        status: FeedbackSolicitude.Status = FeedbackSolicitude.Status.PENDING
    ): FeedbackSolicitude {
        return FeedbackSolicitude(
            id = id,
            tripId = tripId,
            receiverId = receiverId,
            reviewerId = reviewerId,
            status = status
        )
    }
}