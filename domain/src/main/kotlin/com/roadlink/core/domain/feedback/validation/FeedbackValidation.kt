package com.roadlink.core.domain.feedback.validation

import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.validation.BaseValidationService
import com.roadlink.core.domain.validation.Validation

class FeedbackValidationService(
    override val validations: List<Validation<Feedback>> = listOf(
        ReviewerAndReceiverMustBeDifferent(),
        RatingValueMustBeBetweenOneAndFive()
    )
) : BaseValidationService<Feedback>()

class ReviewerAndReceiverMustBeDifferent : Validation<Feedback> {
    override fun execute(entity: Feedback) {
        if (entity.receiverId == entity.reviewerId) {
            throw FeedbackException.InvalidReviewerIdAndReceiverId(
                entity.reviewerId,
                entity.receiverId
            )
        }
    }
}

class RatingValueMustBeBetweenOneAndFive : Validation<Feedback> {
    override fun execute(entity: Feedback) {
        if (entity.rating !in 1..5) {
            throw FeedbackException.InvalidRating(entity.rating)
        }
    }
}