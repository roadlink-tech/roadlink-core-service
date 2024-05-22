package com.roadlink.core.domain.friend

import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import java.util.UUID

class FriendshipStatusCalculator(
    private val friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>,
) {
    fun of(user: User, otherUserId: UUID): FriendshipStatus {
        val areFriends = user.friends.contains(otherUserId)

        // TODO: @jorge agregar indice por addressedId y solicitudeStatus
        val pendingFriendshipSolicitudesReceived = friendshipSolicitudeRepository.findAll(FriendshipSolicitudeCriteria(
            addressedId = user.id,
            solicitudeStatus = FriendshipSolicitude.Status.PENDING,
        ))
            .any { it.requesterId == otherUserId }
        val pendingFriendshipSolicitudesSent = friendshipSolicitudeRepository.findAll(FriendshipSolicitudeCriteria(
            addressedId = otherUserId,
            solicitudeStatus = FriendshipSolicitude.Status.PENDING,
        ))
            .any { it.requesterId == user.id }

        return when {
            otherUserId == user.id -> FriendshipStatus.YOURSELF
            areFriends -> FriendshipStatus.FRIEND
            pendingFriendshipSolicitudesReceived -> FriendshipStatus.PENDING_FRIENDSHIP_SOLICITUDE_RECEIVED
            pendingFriendshipSolicitudesSent -> FriendshipStatus.PENDING_FRIENDSHIP_SOLICITUDE_SENT
            else -> FriendshipStatus.NOT_FRIEND
        }
    }
}