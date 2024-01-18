package com.roadlink.core.infrastructure.dynamodb.error

import com.roadlink.core.infrastructure.InfrastructureException

sealed class DynamoDbException(override val message: String, cause: Throwable? = null) :
    InfrastructureException(message, cause) {

    class InvalidKeyConditionExpression : DynamoDbException("Could not create a key condition expression")

    class InvalidQuery : DynamoDbException("Could not create dynamodb query")

    class EntityDoesNotExist(id: String) : DynamoDbException("Entity $id does not exist")

}
