package com.roadlink.core.domain.friend

import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.*

class FriendshipStatusCalculator(
    private val friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>,
) {
    fun of(user: User, otherUserId: UUID): FriendshipStatus {
        return runBlocking {
            val areFriends = user.friends.contains(otherUserId)

            val pendingFriendshipSolicitudesReceivedDeferred = async {
                friendshipSolicitudeRepository.findAll(
                    FriendshipSolicitudeCriteria(
                        addressedId = user.id,
                        solicitudeStatus = FriendshipSolicitude.Status.PENDING,
                    )
                )
            }
            val pendingFriendshipSolicitudesSentDeferred = async {
                friendshipSolicitudeRepository.findAll(
                    FriendshipSolicitudeCriteria(
                        addressedId = otherUserId,
                        solicitudeStatus = FriendshipSolicitude.Status.PENDING,
                    )
                )
            }
            val pendingFriendshipSolicitudesReceived =
                pendingFriendshipSolicitudesReceivedDeferred.await().any { it.requesterId == otherUserId }
            val pendingFriendshipSolicitudesSent =
                pendingFriendshipSolicitudesSentDeferred.await().any { it.requesterId == user.id }

            return@runBlocking when {
                otherUserId == user.id -> FriendshipStatus.YOURSELF
                areFriends -> FriendshipStatus.FRIEND
                pendingFriendshipSolicitudesReceived -> FriendshipStatus.PENDING_FRIENDSHIP_SOLICITUDE_RECEIVED
                pendingFriendshipSolicitudesSent -> FriendshipStatus.PENDING_FRIENDSHIP_SOLICITUDE_SENT
                else -> FriendshipStatus.NOT_FRIEND
            }
        }
    }
}