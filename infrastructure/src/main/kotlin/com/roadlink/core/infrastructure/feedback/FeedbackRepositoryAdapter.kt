package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.FeedbackRepositoryPort
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbError
import com.roadlink.core.infrastructure.user.error.UserInfrastructureError
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest

class FeedbackRepositoryAdapter(
    private val dynamoDbClient: DynamoDbClient,
    private val tableName: String = "RoadlinkCore"
) : FeedbackRepositoryPort {
    override fun save(feedback: Feedback): Feedback {
        val item = FeedbackDynamoEntity.toItem(feedback)

        val request = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build()

        dynamoDbClient.putItem(request).also { return feedback }
    }

    override fun findOrFail(criteria: FeedbackCriteria): Feedback {
        val result = this.findAll(criteria)
        if (result.isEmpty()) {
            throw UserInfrastructureError.NotFound(FeedbackDynamoCriteria.from(criteria).keyConditionExpression())
        }

        return result.first()
    }

    private fun buildQuery(
        keyConditionExpression: String,
        filterExpression: String,
        expressionAttributeValues: Map<String, AttributeValue>
    ): QueryRequest {
        if (expressionAttributeValues[":id"] != null) {
            if (filterExpression.isNotEmpty()) {
                return QueryRequest.builder()
                    .tableName(tableName)
                    .keyConditionExpression(keyConditionExpression)
                    .filterExpression(filterExpression)
                    .expressionAttributeValues(expressionAttributeValues)
                    .build()
            }
            return QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression(keyConditionExpression)
                .expressionAttributeValues(expressionAttributeValues)
                .build()
        }
        if (expressionAttributeValues[":rating"] != null) {
            if (filterExpression.isNotEmpty()) {
                return QueryRequest.builder()
                    .indexName("RatingGSI")
                    .tableName(tableName)
                    .keyConditionExpression(keyConditionExpression)
                    .filterExpression(filterExpression)
                    .expressionAttributeValues(expressionAttributeValues)
                    .build()
            }
            return QueryRequest.builder()
                .indexName("RatingGSI")
                .tableName(tableName)
                .keyConditionExpression(keyConditionExpression)
                .expressionAttributeValues(expressionAttributeValues)
                .build()
        }
        if (expressionAttributeValues[":reviewerId"] != null) {
            if (filterExpression.isNotEmpty()) {
                return QueryRequest.builder()
                    .indexName("ReviewerIdLSI")
                    .tableName(tableName)
                    .keyConditionExpression(keyConditionExpression)
                    .expressionAttributeValues(expressionAttributeValues)
                    .filterExpression(filterExpression)
                    .build()
            }
            return QueryRequest.builder()
                .indexName("ReviewerIdLSI")
                .tableName(tableName)
                .keyConditionExpression(keyConditionExpression)
                .expressionAttributeValues(expressionAttributeValues)
                .build()
        }
        if (expressionAttributeValues[":receiverId"] != null) {
            if (filterExpression.isNotEmpty()) {
                return QueryRequest.builder()
                    .indexName("ReceiverIdLSI")
                    .tableName(tableName)
                    .keyConditionExpression(keyConditionExpression)
                    .expressionAttributeValues(expressionAttributeValues)
                    .filterExpression(filterExpression)
                    .build()
            }
            return QueryRequest.builder()
                .indexName("ReceiverIdLSI")
                .tableName(tableName)
                .keyConditionExpression(keyConditionExpression)
                .expressionAttributeValues(expressionAttributeValues)
                .build()
        }
        throw DynamoDbError.InvalidQuery()
    }

    override fun findAll(criteria: FeedbackCriteria): List<Feedback> {
        val feedbackDynamoCriteria = FeedbackDynamoCriteria.from(criteria)
        val query = buildQuery(
            feedbackDynamoCriteria.keyConditionExpression(),
            feedbackDynamoCriteria.filterExpression(),
            feedbackDynamoCriteria.expressionAttributeValues()
        )
        val queryResponse = dynamoDbClient.query(query)
        val feedbacks: MutableList<FeedbackDynamoEntity> = ArrayList()

        queryResponse.items().forEach { item ->
            feedbacks.add(FeedbackDynamoEntity.from(item))
        }

        return feedbacks.map { it.toDomain() }
    }
}