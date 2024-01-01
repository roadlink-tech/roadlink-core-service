package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.FeedbackRepositoryPort
import com.roadlink.core.infrastructure.dynamodb.DynamoDbQuery
import com.roadlink.core.infrastructure.user.exception.UserInfrastructureException
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest


class FeedbackRepositoryAdapter(
    private val dynamoDbClient: DynamoDbClient,
    private val tableName: String = "RoadlinkCore"
) : FeedbackRepositoryPort {
    override fun save(feedback: Feedback): Feedback {
        val item = FeedbackDynamoDbEntity.toItem(feedback)
        val request = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build()

        dynamoDbClient.putItem(request).also { return feedback }
    }

    override fun findOrFail(criteria: FeedbackCriteria): Feedback {
        val result = this.findAll(criteria)
        if (result.isEmpty()) {
            throw UserInfrastructureException.NotFound(
                FeedbackDynamoDbQuery.from(criteria).keyConditionExpression()
            )
        }

        return result.first()
    }

    override fun findAll(criteria: FeedbackCriteria): List<Feedback> {
        val feedbackDynamoCriteria = FeedbackDynamoDbQuery.from(criteria)
        val query = DynamoDbQuery.Builder()
            .withTableName(tableName)
            .withKeyConditionExpression(feedbackDynamoCriteria.keyConditionExpression())
            .withFilterExpression(feedbackDynamoCriteria.filterExpression())
            .withExpressionAttributeValues(feedbackDynamoCriteria.expressionAttributeValues())
            .withIndexName(feedbackDynamoCriteria.indexName())
            .build()

        val queryResponse = dynamoDbClient.query(query)
        val feedbacks: MutableList<FeedbackDynamoDbEntity> = ArrayList()

        queryResponse.items().forEach { item ->
            feedbacks.add(FeedbackDynamoDbEntity.from(item))

        }

        return feedbacks.map { it.toDomain() }
    }

}