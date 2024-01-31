package com.roadlink.core.infrastructure.dynamodb

import com.roadlink.core.domain.DomainCriteria
import org.w3c.dom.Attr
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import software.amazon.awssdk.services.sts.endpoints.internal.Value.Str

private const val ATTRIBUTE_NAMES = "attributeNames"
private const val COMPANION = "Companion"

fun Any.isNumber(): Boolean {
    return this is Number
}

interface DynamoDbQuery {
    var entityId: String
    fun fieldsInKeyCondition(): List<String>
    fun fieldsInFilterExpression(): List<String>
    fun keyConditionExpression(): String
    fun expressionAttributeValues(): Map<String, AttributeValue>
    fun filterExpression(): String
    fun indexName(): String
    fun key(): Map<String, AttributeValue>

    class Builder(
        private var indexName: String = "",
        private var filterExpression: String = "",
        private var tableName: String = "",
        private var keyConditionExpression: String = "",
        private var expressionAttributeValues: Map<String, AttributeValue> = emptyMap()
    ) {

        fun withTableName(tableName: String): Builder {
            return apply { this.tableName = tableName }
        }

        fun withIndexName(indexName: String): Builder {
            return apply { this.indexName = indexName }
        }

        fun withFilterExpression(filterExpression: String): Builder {
            return apply { this.filterExpression = filterExpression }
        }

        fun withKeyConditionExpression(keyConditionExpression: String): Builder {
            return apply { this.keyConditionExpression = keyConditionExpression }
        }

        fun withExpressionAttributeValues(expressionAttributeValues: Map<String, AttributeValue>): Builder {
            return apply { this.expressionAttributeValues = expressionAttributeValues }
        }

        fun build(): QueryRequest {
            if (indexName.isNotEmpty()) {
                if (filterExpression.isNotEmpty()) {
                    return QueryRequest.builder()
                        .tableName(tableName)
                        .indexName(indexName)
                        .keyConditionExpression(keyConditionExpression)
                        .filterExpression(filterExpression)
                        .expressionAttributeValues(expressionAttributeValues)
                        .build()
                }
                return QueryRequest.builder()
                    .tableName(tableName)
                    .indexName(indexName)
                    .keyConditionExpression(keyConditionExpression)
                    .expressionAttributeValues(expressionAttributeValues)
                    .build()
            }

            if (filterExpression.isNotEmpty()) {
                return QueryRequest.builder()
                    .tableName(tableName)
                    .keyConditionExpression(keyConditionExpression)
                    .filterExpression(filterExpression)
                    .expressionAttributeValues(expressionAttributeValues)
                    .build()
            }
            return QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression(keyConditionExpression)
                .expressionAttributeValues(expressionAttributeValues)
                .build()
        }
    }
}

abstract class BaseDynamoDbQuery : DynamoDbQuery {
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

    override fun key(): Map<String, AttributeValue> {
        val key: MutableMap<String, AttributeValue> = mutableMapOf()
        fieldsInKeyCondition().forEach { field ->
            key[field.replaceFirstChar { it.uppercase() }] = expressionAttributeValues()[":$field"]!!
        }
        return key
    }

    override fun expressionAttributeValues(): Map<String, AttributeValue> {
        val expressionAttributeValues = mutableMapOf<String, AttributeValue>()
        val fields = fieldsInKeyCondition() + fieldsInFilterExpression()
        fields.forEach { fieldName ->
            val field = this.javaClass.getDeclaredField(fieldName)
            field.trySetAccessible()
            val value = field.get(this)
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

interface DynamoDbQueryMapper<C : DomainCriteria, Q : BaseDynamoDbQuery> {
    fun from(criteria: C): Q
}