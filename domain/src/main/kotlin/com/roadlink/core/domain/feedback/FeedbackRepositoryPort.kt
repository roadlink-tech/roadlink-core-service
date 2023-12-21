package com.roadlink.core.domain.feedback

import java.util.*

interface FeedbackRepositoryPort {
    fun save(feedback: Feedback): Feedback
    fun findOrFail(criteria: FeedbackCriteria): Feedback
}

class FeedbackCriteria(
    val id: UUID? = null,
    val email: String? = null
)