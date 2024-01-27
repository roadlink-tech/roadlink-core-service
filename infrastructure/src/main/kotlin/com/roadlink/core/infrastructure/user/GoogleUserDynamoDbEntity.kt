package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.user.google.GoogleUser
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntity
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntityMapper
import com.roadlink.core.infrastructure.dynamodb.DynamoDbDateFormatter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*

class GoogleUserDynamoDbEntity(
    createdDate: Date,
    val googleId: String,
    val userId: UUID
) : BaseDynamoDbEntity(googleId, createdDate) {
    override fun toDomain(): DomainEntity {
        return GoogleUser(
            googleId = googleId,
            userId = userId
        )
    }

    companion object {
        fun from(item: Map<String, AttributeValue>): GoogleUserDynamoDbEntity {
            return GoogleUserDynamoDbEntity(
                createdDate = DynamoDbDateFormatter.instance().parse(item["CreatedDate"]!!.s()),
                googleId = item["Id"]!!.s(),
                userId = UUID.fromString(item["UserId"]!!.s()),
            )
        }
    }
}

class GoogleUserDynamoDbEntityMapper : BaseDynamoDbEntityMapper<GoogleUser, GoogleUserDynamoDbEntity>() {
    override fun from(item: Map<String, AttributeValue>): GoogleUserDynamoDbEntity {
        return GoogleUserDynamoDbEntity.from(item)
    }

    override fun toItem(entity: GoogleUser): Map<String, AttributeValue> {
        return mapOf(
            "EntityId" to AttributeValue.builder().s("EntityId#GoogleUser").build(),
            "Id" to AttributeValue.builder().s(entity.googleId).build(),
            "CreatedDate" to AttributeValue.builder().s(DynamoDbDateFormatter.instance().format(Date())).build(),
            "UserId" to AttributeValue.builder().s(entity.userId.toString()).build(),
        )
    }
}
