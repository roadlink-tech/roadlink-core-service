package com.roadlink.core.domain.friend

import com.roadlink.core.domain.DomainException
import java.util.*

sealed class FriendshipSolicitudeException(override val message: String, val code: String, cause: Throwable? = null) :
    DomainException(message, code, cause) {

    class FriendshipSolicitudeAlreadySent(requesterId: UUID, addressedId: UUID) :
        FriendshipSolicitudeException(
            message = "User $requesterId has a pending friendship solicitude to $addressedId",
            code = "FRIENDSHIP_SOLICITUDE_ALREADY_SENT"
        )

    class FriendshipSolicitudeStatusCanNotChange(friendshipSolicitude: UUID, status: FriendshipSolicitude.Status) :
        FriendshipSolicitudeException(
            message = "Friendship solicitude $friendshipSolicitude status can not change, because it has raised an immutable status $status",
            code = "FRIENDSHIP_SOLICITUDE_STATUS_CAN_NOT_CHANGE"
        )


    class InvalidFriendshipSolicitudeStatusTransition(
        currentStatus: FriendshipSolicitude.Status,
        nextStatus: FriendshipSolicitude.Status
    ) :
        FriendshipSolicitudeException(
            message = "Entity could not move from $currentStatus to $nextStatus",
            code = "INVALID_FRIENDSHIP_SOLICITUDE_STATUS_TRANSITION"
        )
}