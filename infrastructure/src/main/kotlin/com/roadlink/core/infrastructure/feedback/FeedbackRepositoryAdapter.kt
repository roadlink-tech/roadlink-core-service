package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoRepository
import com.roadlink.core.infrastructure.user.exception.UserInfrastructureException
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue


class FeedbackRepositoryAdapter(
    dynamoDbClient: DynamoDbClient,
    tableName: String = "RoadlinkCore"
) : RepositoryPort<Feedback, FeedbackCriteria>, BaseDynamoRepository(dynamoDbClient, tableName) {
    override fun save(entity: Feedback): Feedback {
        val item = FeedbackDynamoDbEntity.toItem(entity)
        save(item).also { return entity }
    }

    override fun saveAll(entities: List<Feedback>): List<Feedback> {
        val items: List<Map<String, AttributeValue>> = entities.map { FeedbackDynamoDbEntity.toItem(it) }
        saveAll(items).also { return entities }
    }

    override fun findOrFail(criteria: FeedbackCriteria): Feedback {
        val result = this.findAll(criteria)
        if (result.isEmpty()) {
            throw UserInfrastructureException.NotFound(criteria.id)
        }

        return result.first()
    }

    override fun findAll(criteria: FeedbackCriteria): List<Feedback> {
        val dynamoQuery = FeedbackDynamoDbQuery.from(criteria)
        val queryResponse = find(dynamoQuery)

        val feedbacks: MutableList<FeedbackDynamoDbEntity> = ArrayList()
        queryResponse.items().forEach { item ->
            feedbacks.add(FeedbackDynamoDbEntity.from(item))
        }

        return feedbacks.map { it.toDomain() }
    }

}