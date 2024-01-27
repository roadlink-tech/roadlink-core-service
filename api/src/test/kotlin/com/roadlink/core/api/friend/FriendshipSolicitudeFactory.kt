package com.roadlink.core.api.friend

import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitude.*
import com.roadlink.core.domain.friend.FriendshipSolicitude.Status.*
import java.util.Date
import java.util.UUID

object FriendshipSolicitudeFactory {

    fun common(
        id: UUID = UUID.randomUUID(),
        requesterId: UUID = UUID.randomUUID(),
        addressedId: UUID = UUID.randomUUID(),
        solicitudeStatus: Status = PENDING
    ): FriendshipSolicitude {
        return FriendshipSolicitude(
            id = id,
            requesterId = requesterId,
            addressedId = addressedId,
            createdDate = Date(),
            solicitudeStatus = solicitudeStatus
        )
    }
}