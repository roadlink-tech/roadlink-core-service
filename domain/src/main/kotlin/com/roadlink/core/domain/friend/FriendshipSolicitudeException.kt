package com.roadlink.core.domain.friend

import com.roadlink.core.domain.DomainException
import java.util.*

sealed class FriendshipSolicitudeException(override val message: String, cause: Throwable? = null) :
    DomainException(message, cause) {

    class FriendshipSolicitudeAlreadySent(requesterId: UUID, addressedId: UUID) :
        FriendshipSolicitudeException("User $requesterId has a pending friendship solicitude to $addressedId")
}