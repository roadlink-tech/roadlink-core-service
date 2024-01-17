package com.roadlink.core.infrastructure.dynamodb

import com.roadlink.core.domain.DomainCriteria
import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

class RepositoryAdapter<T : DomainEntity, E : BaseDynamoDbEntity, C : DomainCriteria, Q : BaseDynamoDbQuery>(
    dynamoDbClient: DynamoDbClient,
    tableName: String = "RoadlinkCore",
    private val entityMapper: DynamoDbEntityMapper<T, E>,
    private val queryMapper: DynamoDbQueryMapper<C, Q>,
) : RepositoryPort<T, C>, BaseDynamoRepository(dynamoDbClient, tableName) {

    override fun save(entity: T): T {
        save(entityMapper.toItem(entity)).also { return entity }
    }

    override fun saveAll(entities: List<T>): List<T> {
        val items: List<Map<String, AttributeValue>> = entities.map { entityMapper.toItem(it) }
        saveAll(items).also { return entities }
    }

    override fun findAll(criteria: C): List<T> {
        val query = queryMapper.from(criteria)
        val queryResponse = find(query)
        return entityMapper.mapAll(queryResponse)
    }

    override fun findOrFail(criteria: C): T {
        val result = this.findAll(criteria)
        if (result.isEmpty()) {
            throw DynamoDbException.EntityDoesNotExist(criteria.toString())
        }
        return result.first()
    }
}

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

    fun find(dynamoQuery: DynamoDbQuery): QueryResponse {
        val query = DynamoDbQuery.Builder()
            .withTableName(tableName)
            .withIndexName(dynamoQuery.indexName())
            .withKeyConditionExpression(dynamoQuery.keyConditionExpression())
            .withFilterExpression(dynamoQuery.filterExpression())
            .withExpressionAttributeValues(dynamoQuery.expressionAttributeValues())
            .build()

        return dynamoDbClient.query(query)
    }
}