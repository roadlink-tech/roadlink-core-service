package com.roadlink.core.domain.feedback

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.validation.FeedbackValidationService
import java.util.*


/* TODO
 - created date
 - trip id
*/
data class Feedback(
    val id: UUID,
    val reviewerId: UUID,
    val receiverId: UUID,
    val rating: Int,
    val comment: String
) : DomainEntity {

    fun save(feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>): Feedback {
        return feedbackRepository.save(this)
    }

    init {
        FeedbackValidationService().validate(this)
    }
}