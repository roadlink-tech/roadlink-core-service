package com.roadlink.core.infrastructure.friend

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntity
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoDbEntityMapper
import com.roadlink.core.infrastructure.dynamodb.DynamoDbDateFormatter
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*


class FriendshipSolicitudeDynamoDbEntity constructor(
    id: String,
    createdDate: Date = Date(),
    val requesterId: UUID,
    val addressedId: UUID,
    val solicitudeStatus: FriendshipSolicitude.Status
) : BaseDynamoDbEntity(id, createdDate) {

    override fun toDomain(): DomainEntity {
        return FriendshipSolicitude(
            id = UUID.fromString(this.id),
            createdDate = this.createdDate,
            requesterId = this.requesterId,
            addressedId = this.addressedId,
            solicitudeStatus = this.solicitudeStatus
        )
    }

    companion object {

        fun from(item: Map<String, AttributeValue>): FriendshipSolicitudeDynamoDbEntity {
            return FriendshipSolicitudeDynamoDbEntity(
                id = item["Id"]!!.s(),
                requesterId = UUID.fromString(item["RequesterId"]!!.s()),
                addressedId = UUID.fromString(item["AddressedId"]!!.s()),
                solicitudeStatus = FriendshipSolicitude.Status.valueOf(item["SolicitudeStatus"]!!.s()),
                createdDate = DynamoDbDateFormatter.instance().parse(item["CreatedDate"]!!.s()),
            )
        }
    }
}

class FriendshipSolicitudeDynamoDbEntityMapper :
    BaseDynamoDbEntityMapper<FriendshipSolicitude, FriendshipSolicitudeDynamoDbEntity>() {

    override fun from(item: Map<String, AttributeValue>): FriendshipSolicitudeDynamoDbEntity {
        return FriendshipSolicitudeDynamoDbEntity.from(item)
    }

    override fun toItem(entity: FriendshipSolicitude): Map<String, AttributeValue> {
        return mapOf(
            "EntityId" to AttributeValue.builder().s("EntityId#FriendshipSolicitude").build(),
            "Id" to AttributeValue.builder().s(entity.id.toString()).build(),
            "CreatedDate" to AttributeValue.builder().s(DynamoDbDateFormatter.instance().format(Date())).build(),
            "AddressedId" to AttributeValue.builder().s(entity.addressedId.toString()).build(),
            "RequesterId" to AttributeValue.builder().s(entity.requesterId.toString()).build(),
            "SolicitudeStatus" to AttributeValue.builder().s(entity.solicitudeStatus.toString()).build()
        )
    }
}