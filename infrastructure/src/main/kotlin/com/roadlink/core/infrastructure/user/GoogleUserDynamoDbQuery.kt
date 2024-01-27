package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.google.GoogleUserCriteria
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbQuery
import com.roadlink.core.infrastructure.dynamodb.DynamoDbQueryMapper
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException

class GoogleUserDynamoDbQuery(
    val id: String? = null,
) : BaseDynamoDbQuery() {
    override var entityId: String = "EntityId#GoogleUser"

    override fun fieldsInKeyCondition(): List<String> {
        if (id != null) {
            return listOf("id", "entityId")
        }
        throw DynamoDbException.InvalidKeyConditionExpression()
    }

    override fun fieldsInFilterExpression(): List<String> {
        return emptyList()
    }

    override fun indexName(): String {
        if (id != null) {
            return ""
        }
        throw DynamoDbException.InvalidQuery()
    }

}

class GoogleUserDynamoDbQueryMapper : DynamoDbQueryMapper<GoogleUserCriteria, GoogleUserDynamoDbQuery> {
    override fun from(criteria: GoogleUserCriteria): GoogleUserDynamoDbQuery {
        return GoogleUserDynamoDbQuery(
            id = criteria.googleId,
        )
    }
}
