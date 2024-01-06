package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.UserRepositoryPort
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoRepository
import com.roadlink.core.infrastructure.user.exception.UserInfrastructureException
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class UserRepositoryAdapter(
    dynamoDbClient: DynamoDbClient,
    tableName: String = "RoadlinkCore"
) : UserRepositoryPort, BaseDynamoRepository(dynamoDbClient, tableName) {

    override fun save(user: User): User {
        save(UserDynamoDbEntity.toItem(user)).also { return user }
    }

    override fun saveAll(users: List<User>): List<User> {
        val items: List<Map<String, AttributeValue>> = users.map { UserDynamoDbEntity.toItem(it) }
        saveAll(items).also { return users }
    }

    override fun findOrFail(criteria: UserCriteria): User {
        val userDynamoQuery = UserDynamoDbQuery.from(criteria)
        val queryResponse = find(userDynamoQuery)
        if (queryResponse.items().isEmpty()) {
            throw UserInfrastructureException.NotFound(criteria.id)
        }

        val users: MutableList<UserDynamoDbEntity> = ArrayList()
        queryResponse.items().forEach { item ->
            users.add(UserDynamoDbEntity.from(item))
        }

        return users.first().toDomain()
    }
}

