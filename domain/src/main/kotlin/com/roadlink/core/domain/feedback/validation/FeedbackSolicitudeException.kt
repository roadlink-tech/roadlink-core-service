package com.roadlink.core.domain.feedback.validation

import com.roadlink.core.domain.DomainException
import java.util.*

sealed class FeedbackSolicitudeException(override val message: String, val code: String, throwable: Throwable? = null) :
    DomainException(message, code, throwable) {

    class FeedbackSolicitudeAlreadyRejected(pendingFeedbackId: UUID) : FeedbackSolicitudeException(
        message = "Pending feedback $pendingFeedbackId was already rejected",
        code = "PENDING_FEEDBACK_ALREADY_REJECTED"
    )

    class FeedbackSolicitudeAlreadyCompleted(pendingFeedbackId: UUID) : FeedbackSolicitudeException(
        message = "Pending feedback $pendingFeedbackId was already completed",
        code = "PENDING_FEEDBACK_ALREADY_COMPLETED"
    )
}