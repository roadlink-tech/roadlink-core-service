package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.infrastructure.dynamodb.DynamoDbQueryMapper

class FeedbackDynamoDbQueryMapper : DynamoDbQueryMapper<FeedbackCriteria, FeedbackDynamoDbQuery> {
    override fun from(criteria: FeedbackCriteria): FeedbackDynamoDbQuery {
        return FeedbackDynamoDbQuery(
            id = criteria.id,
            rating = criteria.rating,
            reviewerId = criteria.reviewerId,
            receiverId = criteria.receiverId
        )
    }
}