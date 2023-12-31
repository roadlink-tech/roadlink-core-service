package com.roadlink.core.infrastructure.dynamodb

import com.roadlink.core.infrastructure.feedback.FeedbackDynamoCriteria
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

private const val ATTRIBUTE_NAMES = "attributeNames"
private const val COMPANION = "Companion"

fun Any.isNumber(): Boolean {
    return this is Number
}

interface DynamoCriteria {
    var entityId: String
    fun fieldsInKeyCondition(): List<String>
    fun fieldsInFilterExpression(): List<String>
    fun keyConditionExpression(): String
    fun expressionAttributeValues(): Map<String, AttributeValue>
    fun filterExpression(): String
}

open abstract class BaseDynamoCriteria : DynamoCriteria {
    val attributeNames = this::class.java.declaredFields
        .filter { it.name != COMPANION && it.name != ATTRIBUTE_NAMES }
        .map { field ->
            val fieldName = field.name
            fieldName
        }

    override fun keyConditionExpression(): String {
        return fieldsInKeyCondition().joinToString(" AND ") { field -> "${field.replaceFirstChar { it.uppercase() }} = :$field" }
    }

    override fun filterExpression(): String {
        return fieldsInFilterExpression().joinToString(" AND ") { field -> "${field.replaceFirstChar { it.uppercase() }} = :$field" }
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
}

