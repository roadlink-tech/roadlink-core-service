package com.roadlink.core.domain.friend


import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.UserRepositoryPort
import java.util.*


data class FriendshipRequest(
    val requesterId: UUID,
    val addressedId: UUID,
    val creationDate: Date,
    var status: Status = Status.PENDING
) {
    fun accept(userRepository: UserRepositoryPort) {
        val requester = userRepository.findOrFail(UserCriteria(requesterId))
        val addressed = userRepository.findOrFail(UserCriteria(addressedId))
        requester.beFriends(addressed)
        userRepository.saveAll(listOf(requester, addressed))
        this.status = Status.ACCEPTED
    }

    fun reject() {
        this.status = Status.REJECTED
    }

    enum class Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}