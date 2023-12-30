package com.roadlink.core.infrastructure.dynamodb

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

interface DynamoCriteria {
    fun keyConditionExpression(): String
    fun expressionAttributeValues(): Map<String, AttributeValue>
}