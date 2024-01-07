package com.roadlink.application.friend

import com.roadlink.application.DefaultIdGenerator
import com.roadlink.core.domain.friend.FriendshipSolicitude
import java.util.*

data class FriendshipSolicitudeDTO(
    val id: UUID = DefaultIdGenerator().next(),
    val requesterId: UUID,
    val addressedId: UUID,
    val status: FriendshipSolicitude.Status = FriendshipSolicitude.Status.PENDING
) {
    fun toDomain(): FriendshipSolicitude {
        return FriendshipSolicitude(
            id = id,
            requesterId = requesterId,
            addressedId = addressedId,
            status = status
        )
    }

    companion object {
        fun from(solicitude: FriendshipSolicitude): FriendshipSolicitudeDTO {
            return FriendshipSolicitudeDTO(
                id = solicitude.id,
                addressedId = solicitude.addressedId,
                requesterId = solicitude.requesterId,
                status = solicitude.status
            )
        }
    }
}