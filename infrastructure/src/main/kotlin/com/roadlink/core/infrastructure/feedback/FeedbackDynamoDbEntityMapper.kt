package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.infrastructure.dynamodb.DynamoDbDateFormatter
import com.roadlink.core.infrastructure.dynamodb.DynamoDbEntityMapper
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.QueryResponse
import java.util.*

class FeedbackDynamoDbEntityMapper : DynamoDbEntityMapper<Feedback, FeedbackDynamoDbEntity> {

    override fun from(item: Map<String, AttributeValue>): FeedbackDynamoDbEntity {
        return FeedbackDynamoDbEntity.from(item)
    }

    override fun mapAll(response: QueryResponse): List<Feedback> {
        val entities: MutableList<FeedbackDynamoDbEntity> = ArrayList()
        response.items().forEach { item ->
            entities.add(FeedbackDynamoDbEntity.from(item))
        }
        return entities.map { it.toDomain() as Feedback }
    }

    override fun toItem(entity: Feedback): Map<String, AttributeValue> {
        return mapOf(
            "EntityId" to AttributeValue.builder().s("EntityId#Feedback").build(),
            "Id" to AttributeValue.builder().s(entity.id.toString()).build(),
            "CreatedDate" to AttributeValue.builder().s(DynamoDbDateFormatter.instance().format(Date())).build(),
            "Rating" to AttributeValue.builder().n(entity.rating.toString()).build(),
            "ReceiverId" to AttributeValue.builder().s(entity.receiverId.toString()).build(),
            "ReviewerId" to AttributeValue.builder().s(entity.reviewerId.toString()).build(),
            "Comment" to AttributeValue.builder().s(entity.comment).build()
        )
    }
}