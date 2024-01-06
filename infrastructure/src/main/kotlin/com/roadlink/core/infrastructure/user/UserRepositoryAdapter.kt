package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoRepository
import com.roadlink.core.infrastructure.user.exception.UserInfrastructureException
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class UserRepositoryAdapter(
    dynamoDbClient: DynamoDbClient,
    tableName: String = "RoadlinkCore"
) : RepositoryPort<User, UserCriteria>, BaseDynamoRepository(dynamoDbClient, tableName) {

    override fun save(entity: User): User {
        save(UserDynamoDbEntity.toItem(entity)).also { return entity }
    }

    override fun saveAll(entities: List<User>): List<User> {
        val items: List<Map<String, AttributeValue>> = entities.map { UserDynamoDbEntity.toItem(it) }
        saveAll(items).also { return entities }
    }

    override fun findAll(criteria: UserCriteria): List<User> {
        val dynamoCriteria = UserDynamoDbQuery.from(criteria)
        val queryResponse = find(dynamoCriteria)

        val entities: MutableList<UserDynamoDbEntity> = ArrayList()
        queryResponse.items().forEach { item ->
            entities.add(UserDynamoDbEntity.from(item))
        }

        return entities.map { it.toDomain() }
    }

    override fun findOrFail(criteria: UserCriteria): User {
        val result = this.findAll(criteria)
        if (result.isEmpty()) {
            throw UserInfrastructureException.NotFound(criteria.id)
        }

        return result.first()
    }
}

