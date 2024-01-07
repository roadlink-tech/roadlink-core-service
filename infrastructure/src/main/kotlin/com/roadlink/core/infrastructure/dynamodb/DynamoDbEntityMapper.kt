package com.roadlink.core.infrastructure.dynamodb

import com.roadlink.core.domain.DomainEntity
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.QueryResponse

interface DynamoDbEntityMapper<T : DomainEntity, E : BaseDynamoDbEntity> {
    fun toItem(entity: T): Map<String, AttributeValue>
    fun from(item: Map<String, AttributeValue>): E
    fun mapAll(response: QueryResponse): List<T>
}