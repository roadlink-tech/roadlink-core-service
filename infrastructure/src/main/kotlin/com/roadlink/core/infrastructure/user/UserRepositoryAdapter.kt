package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.UserRepositoryPort
import com.roadlink.core.infrastructure.dynamodb.DynamoDbQuery
import com.roadlink.core.infrastructure.user.error.UserInfrastructureError
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest

class UserRepositoryAdapter(
    private val dynamoDbClient: DynamoDbClient,
    private val tableName: String = "RoadlinkCore"
) : UserRepositoryPort {

    override fun save(user: User): User {
        val item = UserDynamoDbEntity.toItem(user)

        val putItemRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build()
        dynamoDbClient.putItem(putItemRequest).also { return user }
    }

    override fun findOrFail(criteria: UserCriteria): User {
        val userDynamoCriteria = UserDynamoDbQuery.from(criteria)
        val query = DynamoDbQuery.Builder()
            .withTableName(tableName)
            .withIndexName(userDynamoCriteria.indexName())
            .withKeyConditionExpression(userDynamoCriteria.keyConditionExpression())
            .withExpressionAttributeValues(userDynamoCriteria.expressionAttributeValues())
            .build()

        val queryResponse = dynamoDbClient.query(query)
        if (queryResponse.items().isEmpty()) {
            throw UserInfrastructureError.NotFound(userDynamoCriteria.keyConditionExpression())
        }

        val users: MutableList<UserDynamoDbEntity> = ArrayList()

        queryResponse.items().forEach { item ->
            users.add(UserDynamoDbEntity.from(item))

        }

        return users.first().toDomain()
    }
}

