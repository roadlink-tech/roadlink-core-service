package com.roadlink.application.feedback

import com.roadlink.core.domain.feedback.Feedback
import java.util.UUID

object FeedbackFactory {

    fun common(
        id: UUID = UUID.randomUUID(),
        receiverId: UUID = UUID.randomUUID(),
        reviewerId: UUID = UUID.randomUUID(),
        rating: Int = 5,
        comment: String = "Todo estuvo bien!"
    ): Feedback {
        return Feedback(
            id = id,
            receiverId = receiverId,
            reviewerId = reviewerId,
            rating = rating,
            comment = comment
        )
    }
}