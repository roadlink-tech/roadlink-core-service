package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoCriteria
import com.roadlink.core.infrastructure.dynamodb.DynamoCriteria
import com.roadlink.core.infrastructure.dynamodb.isNumber
import com.roadlink.core.infrastructure.user.error.UserInfrastructureError
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*

class UserDynamoCriteria(
    val id: UUID? = null,
    private val email: String = ""
) : BaseDynamoCriteria() {

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

    companion object {
        fun from(criteria: UserCriteria): UserDynamoCriteria {
            return UserDynamoCriteria(
                id = criteria.id,
                email = criteria.email
            )
        }
    }
}