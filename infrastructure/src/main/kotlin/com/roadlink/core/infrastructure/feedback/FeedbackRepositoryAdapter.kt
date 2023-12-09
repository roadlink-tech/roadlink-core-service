package com.roadlink.core.infrastructure.feedback

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.FeedbackRepositoryPort

class FeedbackRepositoryAdapter(private val mapper: DynamoDBMapper) : FeedbackRepositoryPort {
    override fun save(feedback: Feedback): Feedback {
        mapper.save(FeedbackDynamoEntity.from(feedback))
        return feedback
    }

    override fun findOrFail(criteria: FeedbackCriteria): Feedback {
        TODO("Not yet implemented")
    }
}