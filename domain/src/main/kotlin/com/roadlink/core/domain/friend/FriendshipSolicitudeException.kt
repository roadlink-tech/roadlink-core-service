package com.roadlink.core.domain.friend

import com.roadlink.core.domain.DomainException
import java.util.*

sealed class FriendshipSolicitudeException(override val message: String, cause: Throwable? = null) :
    DomainException(message, cause) {

    class FriendshipSolicitudeAlreadySent(requesterId: UUID, addressedId: UUID) :
        FriendshipSolicitudeException("User $requesterId has a pending friendship solicitude to $addressedId")

    class FriendshipSolicitudeAlreadyAccepted(friendshipSolicitude: UUID) :
        FriendshipSolicitudeException("Friendship solicitude $friendshipSolicitude already accepted")

    class InvalidFriendshipSolicitudeStatusTransition(
        currentStatus: FriendshipSolicitude.Status,
        nextStatus: FriendshipSolicitude.Status
    ) :
        FriendshipSolicitudeException("Entity could not move from $currentStatus to $nextStatus")
}