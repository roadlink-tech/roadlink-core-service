package com.roadlink.core.domain.friend

import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import java.util.*

class FriendshipStatusCalculator(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>,
) {
    fun of(userId: UUID, otherUserId: UUID): FriendshipStatus {
        val user = userRepository.findOrFail(UserCriteria(id = userId))
        val areFriends = user.friends.contains(otherUserId)

        val pendingFriendshipSolicitudesReceived = friendshipSolicitudeRepository.findAll(FriendshipSolicitudeCriteria(
            addressedId = userId,
            solicitudeStatus = FriendshipSolicitude.Status.PENDING,
        ))
            .any { it.requesterId == otherUserId }
        val pendingFriendshipSolicitudesSent = friendshipSolicitudeRepository.findAll(FriendshipSolicitudeCriteria(
            addressedId = otherUserId,
            solicitudeStatus = FriendshipSolicitude.Status.PENDING,
        ))
            .any { it.requesterId == userId }

        return when {
            otherUserId == userId -> FriendshipStatus.YOURSELF
            areFriends -> FriendshipStatus.FRIEND
            pendingFriendshipSolicitudesReceived -> FriendshipStatus.PENDING_FRIENDSHIP_SOLICITUDE_RECEIVED
            pendingFriendshipSolicitudesSent -> FriendshipStatus.PENDING_FRIENDSHIP_SOLICITUDE_SENT
            else -> FriendshipStatus.NOT_FRIEND
        }
    }
}