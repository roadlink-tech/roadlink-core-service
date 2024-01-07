package com.roadlink.core.domain.friend


import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import java.util.*

data class FriendshipSolicitude(
    val id: UUID,
    val requesterId: UUID,
    val addressedId: UUID,
    val createdDate: Date = Date(),
    var solicitudeStatus: Status = Status.PENDING
) : DomainEntity {
    fun accept(userRepository: RepositoryPort<User, UserCriteria>): FriendshipSolicitude {
        val requester = userRepository.findOrFail(UserCriteria(requesterId))
        val addressed = userRepository.findOrFail(UserCriteria(addressedId))
        requester.beFriends(addressed)
        userRepository.saveAll(listOf(requester, addressed))
        return this.apply { this.solicitudeStatus = Status.ACCEPTED }
    }

    fun reject(): FriendshipSolicitude {
        return this.apply { this.solicitudeStatus = Status.REJECTED }
    }

    fun checkIfItHasBeenAccepted(friendshipRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>) {
        val solicitudes =
            friendshipRepository.findAll(
                FriendshipSolicitudeCriteria(
                    id = this.requesterId,
                    solicitudeStatus = Status.ACCEPTED
                )
            )
        if (solicitudes.isNotEmpty()) {
            throw FriendshipSolicitudeException.FriendshipSolicitudeAlreadyAccepted(this.id)
        }
    }

    fun checkIfExistsAPendingSolicitude(friendshipRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>) {
        val pendingSolicitudes =
            friendshipRepository.findAll(
                FriendshipSolicitudeCriteria(
                    requesterId = this.requesterId,
                    solicitudeStatus = Status.PENDING
                )
            )
        if (pendingSolicitudes.isNotEmpty()) {
            throw FriendshipSolicitudeException.FriendshipSolicitudeAlreadySent(this.requesterId, this.addressedId)
        }
    }

    fun save(friendshipRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>): FriendshipSolicitude {
        return friendshipRepository.save(this)
    }

    enum class Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}