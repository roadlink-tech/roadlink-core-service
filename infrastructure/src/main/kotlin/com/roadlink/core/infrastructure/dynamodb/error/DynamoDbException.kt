package com.roadlink.core.infrastructure.dynamodb.error

import com.roadlink.core.infrastructure.InfrastructureException

sealed class DynamoDbException(override val message: String, val code: String, cause: Throwable? = null) :
    InfrastructureException(message, code, cause) {

    class InvalidKeyConditionExpression :
        DynamoDbException("Could not create a key condition expression", "INVALID_KEY_CONDITION")

    class InvalidQuery : DynamoDbException("Could not create dynamodb query", "INVALID_QUERY")

    class EntityDoesNotExist(id: String) :
        DynamoDbException("Entity $id does not exist", "ENTITY_NOT_EXIST".uppercase())

}
