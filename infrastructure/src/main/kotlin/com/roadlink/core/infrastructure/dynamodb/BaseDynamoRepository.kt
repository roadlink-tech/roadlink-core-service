package com.roadlink.core.infrastructure.dynamodb

import com.roadlink.core.infrastructure.user.UserDynamoDbQuery
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

abstract class BaseDynamoRepository(
    private val dynamoDbClient: DynamoDbClient,
    private val tableName: String,
) {
    fun save(entity: Map<String, AttributeValue>) {
        val putItemRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(entity)
            .build()
        dynamoDbClient.putItem(putItemRequest)
    }

    fun saveAll(entities: List<Map<String, AttributeValue>>) {
        val writeRequests = entities.map {
            WriteRequest.builder().putRequest(
                PutRequest.builder()
                    .item(it).build()
            ).build()
        }

        val batchWriteItemRequest = BatchWriteItemRequest.builder()
            .requestItems(mapOf(tableName to writeRequests))
            .build()

        dynamoDbClient.batchWriteItem(batchWriteItemRequest)
    }

    fun find(dynamoQuery: UserDynamoDbQuery): QueryResponse {
        val query = DynamoDbQuery.Builder()
            .withTableName(tableName)
            .withIndexName(dynamoQuery.indexName())
            .withKeyConditionExpression(dynamoQuery.keyConditionExpression())
            .withExpressionAttributeValues(dynamoQuery.expressionAttributeValues())
            .build()

        return dynamoDbClient.query(query)
    }
}