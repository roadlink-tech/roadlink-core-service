package com.roadlink.core.infrastructure.friend

import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbQuery
import com.roadlink.core.infrastructure.dynamodb.DynamoDbQueryMapper
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbError
import java.util.*

class FriendshipSolicitudeDynamoDbQuery(
    val id: UUID? = null,
    val requesterId: UUID? = null,
    val addressedId: UUID? = null,
    val solicitudeStatus: FriendshipSolicitude.Status? = null,
) : BaseDynamoDbQuery() {
    override var entityId: String = "EntityId#FriendshipSolicitude"

    override fun fieldsInFilterExpression(): List<String> {
        val candidates = attributeNames.subtract(fieldsInKeyCondition().toSet()).toMutableList()

        if (candidates.isEmpty()) {
            return emptyList()
        }
        if (id == null) {
            candidates.remove("id")
        }
        if (solicitudeStatus == null) {
            candidates.remove("solicitudeStatus")
        }
        if (requesterId == null) {
            candidates.remove("requesterId")
        }
        if (addressedId == null) {
            candidates.remove("addressedId")
        }
        return candidates
    }

    override fun indexName(): String {
        if (this.id != null) {
            return ""
        }
        if (this.requesterId != null) {
            return "RequesterIdGSI"
        }
        if (this.addressedId != null) {
            return "AddressedIdGSI"
        }
        throw DynamoDbError.InvalidQuery()
    }

    override fun fieldsInKeyCondition(): List<String> {
        if (id != null) {
            return listOf("id", "entityId")
        }
        if (requesterId != null) {
            return listOf("requesterId")
        }
        if (addressedId != null) {
            return listOf("addressedId")
        }
        throw DynamoDbError.InvalidKeyConditionExpression()
    }
}

class FriendshipSolicitudeDynamoDbQueryMapper :
    DynamoDbQueryMapper<FriendshipSolicitudeCriteria, FriendshipSolicitudeDynamoDbQuery> {
    override fun from(criteria: FriendshipSolicitudeCriteria): FriendshipSolicitudeDynamoDbQuery {
        return FriendshipSolicitudeDynamoDbQuery(
            id = criteria.id,
            requesterId = criteria.requesterId,
            addressedId = criteria.addressedId,
            solicitudeStatus = criteria.solicitudeStatus
        )
    }
}