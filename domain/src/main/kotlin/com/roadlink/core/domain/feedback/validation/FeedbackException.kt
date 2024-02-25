package com.roadlink.core.domain.feedback.validation

import com.roadlink.core.domain.DomainException
import java.util.*

sealed class FeedbackException(override val message: String, val code: String, throwable: Throwable? = null) :
    DomainException(message, code, throwable) {

    class InvalidReviewerIdAndReceiverId(reviewerId: UUID, receiverId: UUID) :
        FeedbackException(
            message = "ReceiverId $receiverId and ReviewerId $reviewerId can not be equals",
            code = "INVALID_REVIEWER_AND_RECEIVER_ID"
        )

    class InvalidRating : FeedbackException(message = "Rating must be between 1 and 5", code = "INVALID_RATING")
}