package com.roadlink.core.infrastructure.feedback

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.FeedbackRepositoryPort
import com.roadlink.core.infrastructure.user.UserDynamoEntity

class FeedbackRepositoryAdapter(private val mapper: DynamoDBMapper) : FeedbackRepositoryPort {
    override fun save(feedback: Feedback): Feedback {
        mapper.save(FeedbackDynamoEntity.from(feedback))
        return feedback
    }

    override fun findOrFail(criteria: FeedbackCriteria): Feedback {
        TODO("Not yet implemented")
    }

    override fun findAll(criteria: FeedbackCriteria): List<Feedback> {
        val expressionAttributeValues = mutableMapOf(
            ":entityId" to AttributeValue().withS("EntityId#Feedback"),
        )
        var conditionExpression = "EntityId = :entityId"

        if (criteria.id != null) {
            expressionAttributeValues[":id"] = AttributeValue().withS(criteria.id.toString())
            conditionExpression = "$conditionExpression AND Id = :id"
        }

        if (criteria.receiverId != null) {
            expressionAttributeValues[":receiverId"] =
                AttributeValue().withS(criteria.receiverId.toString())
            conditionExpression = "$conditionExpression AND ReceiverId = :receiverId"
        }

        val q = buildQuery(conditionExpression, expressionAttributeValues)
        return mapper.query(FeedbackDynamoEntity::class.java, q).map { it.toDomain() }
    }

    private fun buildQuery(
        conditionExpression: String,
        expressionAttributeValues: MutableMap<String, AttributeValue>
    ): DynamoDBQueryExpression<FeedbackDynamoEntity>? {
        return DynamoDBQueryExpression<FeedbackDynamoEntity>()
            .withIndexName("ReceiverIdLSI")
            .withKeyConditionExpression(conditionExpression)
            .withExpressionAttributeValues(expressionAttributeValues)

    }
}