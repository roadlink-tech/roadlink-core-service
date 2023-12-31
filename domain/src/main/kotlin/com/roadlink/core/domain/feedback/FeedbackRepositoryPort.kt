package com.roadlink.core.domain.feedback

import java.util.*

interface FeedbackRepositoryPort {
    fun save(feedback: Feedback): Feedback
    fun findOrFail(criteria: FeedbackCriteria): Feedback
    fun findAll(criteria: FeedbackCriteria): List<Feedback>
}

class FeedbackCriteria(
    val id: UUID? = null,
    val rating: Int = 0,
    val receiverId: UUID? = null,
    val reviewerId: UUID? = null
)