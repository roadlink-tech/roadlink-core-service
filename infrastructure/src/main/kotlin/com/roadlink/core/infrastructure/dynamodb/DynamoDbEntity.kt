package com.roadlink.core.infrastructure.dynamodb

import com.roadlink.core.domain.DomainEntity
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.QueryResponse
import java.util.*

interface DynamoDbEntity {
    /**
     * Partition key: DynamoDB uses the partition key's value as input to an internal hash function.
     * The output from the hash function determines the partition (physical storage internal to DynamoDB) in which the item will be stored.
     */
    var entityId: String

    /**
     *  Sorting key: The main purpose of a sorting key in Amazon DynamoDB is to allow for efficient querying and sorting of data within a DynamoDB table.
     *  Sorting keys are a fundamental component of DynamoDB's data model, which uses a composite primary key consisting of a partition key
     *  (also known as a hash key) and a sorting key (also known as a range key).
     */
    var id: UUID
    var createdDate: Date

    fun toDomain(): DomainEntity

}

abstract class BaseDynamoDbEntity(override var id: UUID, override var createdDate: Date) : DynamoDbEntity {
    override var entityId: String = "EntityId#${Regex("^[A-Z]{1}[a-z]+").find(this::class.java.simpleName)?.value}"

}

interface DynamoDbEntityMapper<T : DomainEntity, E : BaseDynamoDbEntity> {
    fun toItem(entity: T): Map<String, AttributeValue>
    fun from(item: Map<String, AttributeValue>): E
    fun mapAll(response: QueryResponse): List<T>
}

abstract class BaseDynamoDbEntityMapper<T : DomainEntity, E : BaseDynamoDbEntity> : DynamoDbEntityMapper<T, E> {
    override fun mapAll(response: QueryResponse): List<T> {
        val entities: MutableList<E> = ArrayList()
        response.items().forEach { item ->
            entities.add(this.from(item))
        }
        return entities.map { it.toDomain() as T }
    }
}