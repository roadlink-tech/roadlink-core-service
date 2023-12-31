package com.roadlink.core.infrastructure.dynamodb

private const val ATTRIBUTE_NAMES = "attributeNames"
private const val COMPANION = "Companion"

interface DynamoDbEntity {
}

abstract class BaseDynamoDbEntity : DynamoDbEntity {
    val attributeNames = this::class.java.declaredFields
        .filter { it.name != COMPANION && it.name != ATTRIBUTE_NAMES }
        .map { field ->
            val fieldName = field.name
            fieldName
        }
}