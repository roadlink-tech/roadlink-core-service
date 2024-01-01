package com.roadlink.core.domain.feedback.validation

import com.roadlink.core.domain.DomainException
import java.util.*

sealed class FeedbackException(override val message: String, throwable: Throwable? = null) :
    DomainException(message, throwable) {

    class InvalidReviewerIdAndReceiverId(reviewerId: UUID, receiverId: UUID) :
        FeedbackException(message = "ReceiverId $receiverId and ReviewerId $reviewerId can not be equals")

    class InvalidRating(rating: Int) : FeedbackException(message = "Rating must be between 1 and 5")
}