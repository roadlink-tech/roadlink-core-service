package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbQuery
import com.roadlink.core.infrastructure.user.exception.UserInfrastructureError
import java.util.*

class UserDynamoDbQuery(
    val id: UUID? = null,
    private val email: String = ""
) : BaseDynamoDbQuery() {

    override var entityId: String = "EntityId#User"

    init {
        if (id == null && email == "") {
            throw UserInfrastructureError.CriteriaEmpty()
        }
    }

    override fun fieldsInKeyCondition(): List<String> {
        if (email != "") {
            return listOf("email", "entityId")
        }
        return listOf("id", "entityId")
    }

    override fun fieldsInFilterExpression(): List<String> {
        return emptyList()
    }

    override fun indexName(): String {
        if (email != "") {
            return "EmailLSI"
        }
        return ""
    }

    companion object {
        fun from(criteria: UserCriteria): UserDynamoDbQuery {
            return UserDynamoDbQuery(
                id = criteria.id,
                email = criteria.email
            )
        }
    }
}