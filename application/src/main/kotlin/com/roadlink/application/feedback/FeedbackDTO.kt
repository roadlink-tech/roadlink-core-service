package com.roadlink.application.feedback

import com.roadlink.application.DefaultIdGenerator
import com.roadlink.core.domain.feedback.Feedback
import java.util.UUID

data class FeedbackDTO(
    val id: UUID = DefaultIdGenerator().next(),
    val reviewerId: UUID,
    val receiverId: UUID,
    val rating: Int,
    val comment: String
) {

    fun toDomain(): Feedback {
        return Feedback(
            id = id,
            receiverId = receiverId,
            reviewerId = reviewerId,
            rating = rating,
            comment = comment
        )
    }

    companion object {
        fun from(feedback: Feedback): FeedbackDTO {
            return FeedbackDTO(
                id = feedback.id,
                reviewerId = feedback.reviewerId,
                receiverId = feedback.receiverId,
                rating = feedback.rating,
                comment = feedback.comment
            )
        }
    }
}