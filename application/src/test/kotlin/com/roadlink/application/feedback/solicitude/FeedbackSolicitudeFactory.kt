package com.roadlink.application.feedback.solicitude

import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import java.util.*

object FeedbackSolicitudeFactory {

    fun common(
        id: UUID = UUID.randomUUID(),
        tripId: UUID = UUID.randomUUID(),
        reviewerId: UUID = UUID.randomUUID(),
        receiverId: UUID = UUID.randomUUID(),
        status: FeedbackSolicitude.Status = FeedbackSolicitude.Status.PENDING
    ): FeedbackSolicitude {
        return FeedbackSolicitude(
            id = id,
            tripId = tripId,
            reviewerId = reviewerId,
            receiverId = receiverId,
            status = status
        )
    }
}