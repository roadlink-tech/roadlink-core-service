package com.roadlink.core.infrastructure.dynamodb

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
}

abstract class BaseDynamoDbEntity(override var id: UUID, override var createdDate: Date) : DynamoDbEntity {
    override var entityId: String = "EntityId#${Regex("^[A-Z]{1}[a-z]+").find(this::class.java.simpleName)?.value}"
}