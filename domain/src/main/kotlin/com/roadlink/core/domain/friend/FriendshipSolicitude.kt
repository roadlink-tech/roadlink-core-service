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
    var status: Status = Status.PENDING
) : DomainEntity {
    fun accept(userRepository: RepositoryPort<User, UserCriteria>): FriendshipSolicitude {
        val requester = userRepository.findOrFail(UserCriteria(requesterId))
        val addressed = userRepository.findOrFail(UserCriteria(addressedId))
        requester.beFriends(addressed)
        userRepository.saveAll(listOf(requester, addressed))
        return this.apply { this.status = Status.ACCEPTED }
    }

    fun reject(): FriendshipSolicitude {
        return this.apply { this.status = Status.REJECTED }
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