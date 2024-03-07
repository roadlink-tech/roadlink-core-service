package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.feedback.Feedback
import java.util.UUID

object FeedbackFactory {

    fun custom(
        id: UUID = UUID.randomUUID(),
        receiverId: UUID = UUID.randomUUID(),
        reviewerId: UUID = UUID.randomUUID(),
        tripLegId: UUID = UUID.randomUUID(),
        rating: Int = 1
    ): Feedback {
        return Feedback(
            id = id,
            receiverId = receiverId,
            reviewerId = reviewerId,
            rating = rating,
            comment = "Everything was ok!",
            tripLegId = tripLegId
        )
    }
}