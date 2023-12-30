package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.FeedbackRepositoryPort
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest

class FeedbackRepositoryAdapter(
    private val mapper: DynamoDbClient,
    private val tableName: String = "RoadlinkCore"
) : FeedbackRepositoryPort {
    override fun save(feedback: Feedback): Feedback {
        val item = FeedbackDynamoEntity.toItem(feedback)

        val request = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build()

        mapper.putItem(request).also { return feedback }
    }

    override fun findOrFail(criteria: FeedbackCriteria): Feedback {
        TODO("Not yet implemented")
    }

    override fun findAll(criteria: FeedbackCriteria): List<Feedback> {
        TODO()
    //        val expressionAttributeValues = mutableMapOf(
//            ":entityId" to AttributeValue().withS("EntityId#Feedback"),
//        )
//        var conditionExpression = "EntityId = :entityId"
//
//        if (criteria.id != null) {
//            expressionAttributeValues[":id"] = AttributeValue().withS(criteria.id.toString())
//            conditionExpression = "$conditionExpression AND Id = :id"
//        }
//
//        if (criteria.receiverId != null) {
//            expressionAttributeValues[":receiverId"] =
//                AttributeValue().withS(criteria.receiverId.toString())
//            conditionExpression = "$conditionExpression AND ReceiverId = :receiverId"
//        }
//
//        val q = buildQuery(conditionExpression, expressionAttributeValues)
//        return mapper.query(FeedbackDynamoEntity::class.java, q).map { it.toDomain() }
    }

//    private fun buildQuery(
//        conditionExpression: String,
//        expressionAttributeValues: MutableMap<String, AttributeValue>
//    ): DynamoDBQueryExpression<FeedbackDynamoEntity>? {
//        return DynamoDBQueryExpression<FeedbackDynamoEntity>()
//            .withIndexName("ReceiverIdLSI")
//            .withKeyConditionExpression(conditionExpression)
//            .withExpressionAttributeValues(expressionAttributeValues)
//
//    }
}