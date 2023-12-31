package com.roadlink.core.infrastructure.dynamodb.error

import com.roadlink.core.infrastructure.InfrastructureException

sealed class DynamoDbError(override val message: String, cause: Throwable? = null) :
    InfrastructureException(message, cause) {

    class InvalidKeyConditionExpression : DynamoDbError("Could not create a key condition expression")

    class InvalidQuery : DynamoDbError("Could not create dynamodb query")
}
