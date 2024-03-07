package com.roadlink.core.infrastructure.feedback.solicitude

import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitudeCriteria
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbQuery
import com.roadlink.core.infrastructure.dynamodb.DynamoDbQueryMapper
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import java.util.*

class FeedbackSolicitudeDynamoDbQuery(
    val id: UUID? = null,
    private val feedbackSolicitudeStatus: String? = null,
    private val receiverId: UUID? = null,
    private val reviewerId: UUID? = null,
    private val tripLegId: UUID? = null
) : BaseDynamoDbQuery() {
    override var entityId: String = "EntityId#FeedbackSolicitude"

    override fun fieldsInFilterExpression(): List<String> {
        val candidates = attributeNames.subtract(fieldsInKeyCondition().toSet()).toMutableList()
        if (candidates.isEmpty()) {
            return emptyList()
        }
        if (id == null) {
            candidates.remove("id")
        }
        if (receiverId == null) {
            candidates.remove("receiverId")
        }
        if (reviewerId == null) {
            candidates.remove("reviewerId")
        }
        if (feedbackSolicitudeStatus == null) {
            candidates.remove("feedbackSolicitudeStatus")
        }
        if (tripLegId == null) {
            candidates.remove("tripLegId")
        }
        return candidates
    }

    override fun indexName(): String {
        if (this.id != null) {
            return ""
        }
        if (this.reviewerId != null) {
            return "ReviewerIdLSI"
        }
        throw DynamoDbException.InvalidQuery()
    }

    override fun fieldsInKeyCondition(): List<String> {
        if (id != null) {
            return listOf("id", "entityId")
        }
        if (reviewerId != null) {
            return listOf("reviewerId", "entityId")
        }
        throw DynamoDbException.InvalidKeyConditionExpression()
    }
}

class FeedbackSolicitudeDynamoDbQueryMapper :
    DynamoDbQueryMapper<FeedbackSolicitudeCriteria, FeedbackSolicitudeDynamoDbQuery> {
    override fun from(criteria: FeedbackSolicitudeCriteria): FeedbackSolicitudeDynamoDbQuery {
        return FeedbackSolicitudeDynamoDbQuery(
            id = criteria.id,
            feedbackSolicitudeStatus = criteria.status?.toString(),
            reviewerId = criteria.reviewerId,
            receiverId = criteria.receiverId,
            tripLegId = criteria.tripLegId
        )
    }
}