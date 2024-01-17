package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbQuery
import com.roadlink.core.infrastructure.dynamodb.DynamoDbQueryMapper
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import java.util.*


class FeedbackDynamoDbQuery(
    val id: UUID? = null,
    private val rating: Int = 0,
    private val receiverId: UUID? = null,
    private val reviewerId: UUID? = null,
) : BaseDynamoDbQuery() {
    override var entityId: String = "EntityId#Feedback"

    override fun fieldsInFilterExpression(): List<String> {
        val candidates = attributeNames.subtract(fieldsInKeyCondition().toSet()).toMutableList()

        if (candidates.isEmpty()) {
            return emptyList()
        }
        if (id == null) {
            candidates.remove("id")
        }
        if (rating == 0) {
            candidates.remove("rating")
        }
        if (receiverId == null) {
            candidates.remove("receiverId")
        }
        if (reviewerId == null) {
            candidates.remove("reviewerId")
        }
        return candidates
    }

    override fun indexName(): String {
        if (this.id != null) {
            return ""
        }
        if (this.rating > 0) {
            return "RatingGSI"
        }
        if (this.reviewerId != null) {
            return "ReviewerIdLSI"
        }
        if (this.receiverId != null) {
            return "ReceiverIdLSI"
        }
        throw DynamoDbException.InvalidQuery()
    }

    override fun fieldsInKeyCondition(): List<String> {
        if (id != null) {
            return listOf("id", "entityId")
        }
        if (rating > 0) {
            return listOf("rating")
        }
        if (receiverId != null) {
            return listOf("receiverId", "entityId")
        }
        if (reviewerId != null) {
            return listOf("reviewerId", "entityId")
        }
        throw DynamoDbException.InvalidKeyConditionExpression()
    }
}

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