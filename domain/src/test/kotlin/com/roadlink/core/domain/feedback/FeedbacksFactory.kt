package com.roadlink.core.domain.feedback

import java.util.*

object FeedbacksFactory {
    fun common(
        id: UUID = UUID.randomUUID(),
        receiverId: UUID = UUID.randomUUID(),
        reviewerId: UUID = UUID.randomUUID(),
        rating: Int = 5
    ): Feedback {
        return Feedback(
            id = id,
            receiverId = receiverId,
            reviewerId = reviewerId,
            rating = rating,
            tripId = UUID.randomUUID(),
            comment = "No comments bro!"
        )
    }
}