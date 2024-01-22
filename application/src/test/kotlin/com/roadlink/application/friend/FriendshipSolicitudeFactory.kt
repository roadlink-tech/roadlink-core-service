package com.roadlink.application.friend

import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitude.Status
import com.roadlink.core.domain.friend.FriendshipSolicitude.Status.PENDING
import java.util.*

object FriendshipSolicitudeFactory {

    fun common(
        id: UUID = UUID.randomUUID(),
        requesterId: UUID = UUID.randomUUID(),
        addressedId: UUID = UUID.randomUUID(),
        createdDate: Date = Date(),
        solicitudeStatus: Status = PENDING
    ): FriendshipSolicitude {
        return FriendshipSolicitude(
            id = id,
            requesterId = requesterId,
            addressedId = addressedId,
            createdDate = createdDate,
            solicitudeStatus = solicitudeStatus
        )
    }
}