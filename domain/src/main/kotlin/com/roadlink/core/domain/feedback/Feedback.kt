package com.roadlink.core.domain.feedback

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.feedback.validation.FeedbackValidationService
import java.util.*


data class Feedback(
    val id: UUID,
    val reviewerId: UUID,
    val receiverId: UUID,
    val rating: Int,
    val comment: String
) : DomainEntity {

    init {
        FeedbackValidationService().validate(this)
    }
}