package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.infrastructure.dynamodb.DynamoCriteria
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbError
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*

class FeedbackDynamoCriteria(
    val id: UUID? = null,
    private val rating: Int = 0,
    private val receiverId: UUID? = null,
    private val reviewerId: UUID? = null
) : DynamoCriteria {
    override fun keyConditionExpression(): String {
        if (id != null) {
            return "EntityId = :entityId AND Id = :id"
        }
        if (rating > 0) {
            return "Rating = :rating"
        }
        if (receiverId != null) {
            return "EntityId = :entityId AND ReceiverId = :receiverId"
        }
        if (reviewerId != null) {
            return "EntityId = :entityId AND ReviewerId = :reviewerId"
        } else {
            throw DynamoDbError.InvalidKeyConditionExpression()
        }
    }

    override fun expressionAttributeValues(): Map<String, AttributeValue> {
        if (id != null) {
            return mapOf(
                ":entityId" to AttributeValue.builder().s("EntityId#Feedback").build(),
                ":id" to AttributeValue.builder().s(id.toString()).build(),
            )
        }
        if (rating > 0) {
            return mapOf(
                ":rating" to AttributeValue.builder().n(rating.toString()).build()
            )
        }
        if (receiverId != null) {
            return mapOf(
                ":entityId" to AttributeValue.builder().s("EntityId#Feedback").build(),
                ":receiverId" to AttributeValue.builder().s(receiverId.toString()).build(),
            )
        }
        if (reviewerId != null) {
            return mapOf(
                ":entityId" to AttributeValue.builder().s("EntityId#Feedback").build(),
                ":reviewerId" to AttributeValue.builder().s(reviewerId.toString()).build(),
            )
        } else {
            throw DynamoDbError.InvalidKeyConditionExpression()
        }
    }

    companion object {
        fun from(criteria: FeedbackCriteria): FeedbackDynamoCriteria {
            return FeedbackDynamoCriteria(
                id = criteria.id,
                rating = criteria.rating,
                reviewerId = criteria.reviewerId,
                receiverId = criteria.receiverId
            )
        }
    }
}