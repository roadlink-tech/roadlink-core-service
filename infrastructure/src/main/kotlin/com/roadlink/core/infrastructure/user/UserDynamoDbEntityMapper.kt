package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.User
import com.roadlink.core.infrastructure.dynamodb.DynamoDbDateFormatter
import com.roadlink.core.infrastructure.dynamodb.DynamoDbEntityMapper
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.QueryResponse
import java.util.*


class UserDynamoDbEntityMapper : DynamoDbEntityMapper<User, UserDynamoDbEntity> {
    override fun from(item: Map<String, AttributeValue>): UserDynamoDbEntity {
        return UserDynamoDbEntity.from(item)
    }

    override fun mapAll(response: QueryResponse): List<User> {
        val entities: MutableList<UserDynamoDbEntity> = ArrayList()
        response.items().forEach { item ->
            entities.add(UserDynamoDbEntity.from(item))
        }

        return entities.map { it.toDomain() as User }
    }

    override fun toItem(entity: User): Map<String, AttributeValue> {
        return mapOf(
            "EntityId" to AttributeValue.builder().s("EntityId#User").build(),
            "Id" to AttributeValue.builder().s(entity.id.toString()).build(),
            "CreatedDate" to AttributeValue.builder().s(DynamoDbDateFormatter.instance().format(Date())).build(),
            "Email" to AttributeValue.builder().s(entity.email).build(),
            "FirstName" to AttributeValue.builder().s(entity.firstName).build(),
            "LastName" to AttributeValue.builder().s(entity.lastName).build()
        )
    }
}