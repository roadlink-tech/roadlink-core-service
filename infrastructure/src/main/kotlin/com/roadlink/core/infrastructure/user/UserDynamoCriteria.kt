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

//    init {
//        if (id == null && email == "") {
//            throw UserInfrastructureError.CriteriaEmpty()
//        }
//    }

    override fun fieldsInKeyCondition(): List<String> {
        if (email != "") {
            return listOf("email", "entityId")
        }
        return listOf("id", "entityId")
    }

    override fun fieldsInFilterExpression(): List<String> {
        return emptyList()
    }

    override fun expressionAttributeValues(): Map<String, AttributeValue> {
        val expressionAttributeValues = mutableMapOf<String, AttributeValue>()
        val fields = fieldsInKeyCondition() + fieldsInFilterExpression()
        fields.forEach { fieldName ->
            val value = this.javaClass.getDeclaredField(fieldName).get(this)
            if (value.isNumber()) {
                expressionAttributeValues[":$fieldName"] =
                    AttributeValue.builder().n(value.toString()).build()
            } else {
                expressionAttributeValues[":$fieldName"] =
                    AttributeValue.builder().s(value.toString()).build()
            }
        }
        return expressionAttributeValues
    }

//    override fun keyConditionExpression(): String {
//        val keyConditionExpression = "EntityId = :entityId"
//        return if (email.isNotEmpty()) {
//            "$keyConditionExpression AND Email = :email"
//        } else {
//            return "$keyConditionExpression AND Id = :id"
//        }
//    }

//    override fun expressionAttributeValues(): Map<String, AttributeValue> {
//        val expressionAttributeValues = mutableMapOf(
//            ":entityId" to AttributeValue.builder().s("EntityId#User").build(),
//        )
//        if (id != null) {
//            expressionAttributeValues[":id"] = AttributeValue.builder().s(id.toString()).build()
//        }
//        if (email != "") {
//            expressionAttributeValues[":email"] = AttributeValue.builder().s(email).build()
//        }
//        return expressionAttributeValues
//    }

    companion object {
        fun from(criteria: UserCriteria): UserDynamoCriteria {
            return UserDynamoCriteria(
                id = criteria.id,
                email = criteria.email
            )
        }
    }
}