package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoCriteria
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbError
import com.roadlink.core.infrastructure.dynamodb.isNumber
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*


class FeedbackDynamoCriteria(
    val id: UUID? = null,
    private val rating: Int = 0,
    private val receiverId: UUID? = null,
    private val reviewerId: UUID? = null,
) : BaseDynamoCriteria() {
    override var entityId: String = "EntityId#Feedback"

    override fun fieldsInFilterExpression(): List<String> {
        val candidates = attributeNames.subtract(fieldsInKeyCondition().toSet()).toMutableList()

        if (candidates.isEmpty()) {
            return emptyList()
        }
        if (id == null) {
            candidates.remove("id")
        }
        if (rating == 0) {
            candidates.remove("rating")
        }
        if (receiverId == null) {
            candidates.remove("receiverId")
        }
        if (reviewerId == null) {
            candidates.remove("reviewerId")
        }
        return candidates
    }

    override fun fieldsInKeyCondition(): List<String> {
        if (id != null) {
            return listOf("id", "entityId")
        }
        if (rating > 0) {
            return listOf("rating")
        }
        if (receiverId != null) {
            return listOf("receiverId", "entityId")
        }
        if (reviewerId != null) {
            return listOf("reviewerId", "entityId")
        }
        throw DynamoDbError.InvalidKeyConditionExpression()
    }

//    override fun keyConditionExpression(): String {
//        return fieldsInKeyCondition().joinToString(" AND ") { field -> "${field.replaceFirstChar { it.uppercase() }} = :$field" }
//    }
//
//    override fun filterExpression(): String {
//        return fieldsInFilterExpression().joinToString(" AND ") { field -> "${field.replaceFirstChar { it.uppercase() }} = :$field" }
//    }

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

    companion object {
        fun from(criteria: FeedbackCriteria): FeedbackDynamoCriteria {
            return FeedbackDynamoCriteria(
                id = criteria.id,
                rating = criteria.rating,
                reviewerId = criteria.reviewerId,
                receiverId = criteria.receiverId
            )
        }
    }
}