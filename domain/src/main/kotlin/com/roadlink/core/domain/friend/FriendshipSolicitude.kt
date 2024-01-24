package com.roadlink.core.domain.friend


import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.friend.FriendshipSolicitude.Status.*
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import java.util.*

data class FriendshipSolicitude(
    val id: UUID,
    val requesterId: UUID,
    val addressedId: UUID,
    val createdDate: Date = Date(),
    var solicitudeStatus: Status = PENDING
) : DomainEntity {
    fun accept(userRepository: RepositoryPort<User, UserCriteria>): FriendshipSolicitude {
        val requester = userRepository.findOrFail(UserCriteria(requesterId))
        val addressed = userRepository.findOrFail(UserCriteria(addressedId))
        requester.beFriendOf(addressed)
        userRepository.saveAll(listOf(requester, addressed))
        return this.apply { this.solicitudeStatus = ACCEPTED }
    }

    fun reject(): FriendshipSolicitude {
        return this.apply { this.solicitudeStatus = REJECTED }
    }

    fun checkIfStatusCanChange() {
        if (!statusCanChange(this.solicitudeStatus)) {
            throw FriendshipSolicitudeException.FriendshipSolicitudeStatusCanNotChange(this.id, this.solicitudeStatus)
        }
    }

    fun checkIfExistsAPendingSolicitude(friendshipRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>) {
        val pendingSolicitudes =
            friendshipRepository.findAll(
                FriendshipSolicitudeCriteria(
                    requesterId = this.requesterId,
                    addressedId = this.addressedId,
                    solicitudeStatus = PENDING
                )
            ) + friendshipRepository.findAll(
                FriendshipSolicitudeCriteria(
                    requesterId = this.addressedId,
                    addressedId = this.requesterId,
                    solicitudeStatus = PENDING
                )
            )
        if (pendingSolicitudes.isNotEmpty()) {
            throw FriendshipSolicitudeException.FriendshipSolicitudeAlreadySent(
                this.requesterId,
                this.addressedId
            )
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

    companion object {
        fun statusCanChange(status: Status): Boolean {
            return when (status) {
                PENDING -> true
                ACCEPTED, REJECTED -> false
            }
        }
    }
}