package com.roadlink.application.friend

import com.roadlink.core.domain.friend.FriendshipSolicitude
import java.util.*

data class FriendshipSolicitudeDecisionDTO(
    val id: UUID,
    val addressedId: UUID,
    val status: FriendshipSolicitude.Status
)