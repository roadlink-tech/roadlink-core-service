package com.roadlink.core.api.feedback

import com.roadlink.core.domain.feedback.Feedback
import java.util.UUID

object FeedbackFactory {

    fun common(
        id: UUID = UUID.randomUUID(),
        receiverId: UUID = UUID.randomUUID(),
        reviewerId: UUID = UUID.randomUUID(),
        comment: String = "Sin comentarios",
        tripId: UUID = UUID.randomUUID(),
        rating: Int = 5
    ): Feedback {
        return Feedback(
            id = id,
            reviewerId = reviewerId,
            receiverId = receiverId,
            rating = rating,
            comment = comment,
            tripId = tripId
        )
    }
}