package com.roadlink.core.infrastructure.dynamodb

import com.roadlink.core.domain.DomainCriteria
import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.infrastructure.user.exception.UserInfrastructureException
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class RepositoryAdapter<T : DomainEntity, E : BaseDynamoDbEntity, C : DomainCriteria, Q : BaseDynamoDbQuery>(
    dynamoDbClient: DynamoDbClient,
    tableName: String = "RoadlinkCore",
    private val dynamoEntityMapper: DynamoDbEntityMapper<T, E>,
    private val dynamoQueryMapper: DynamoDbQueryMapper<C, Q>,
) : RepositoryPort<T, C>, BaseDynamoRepository(dynamoDbClient, tableName) {

    override fun save(entity: T): T {
        save(dynamoEntityMapper.toItem(entity)).also { return entity }
    }

    override fun saveAll(entities: List<T>): List<T> {
        val items: List<Map<String, AttributeValue>> = entities.map { dynamoEntityMapper.toItem(it) }
        saveAll(items).also { return entities }
    }

    override fun findAll(criteria: C): List<T> {
        val dynamoCriteria = dynamoQueryMapper.from(criteria)
        val queryResponse = find(dynamoCriteria)
        return dynamoEntityMapper.mapAll(queryResponse)
    }

    override fun findOrFail(criteria: C): T {
        val result = this.findAll(criteria)
        if (result.isEmpty()) {
            throw UserInfrastructureException.NotFound()
        }
        return result.first()
    }
}