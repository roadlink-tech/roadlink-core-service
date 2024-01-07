package com.roadlink.core.domain.user

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.DomainException
import com.roadlink.core.domain.RepositoryPort
import java.util.*
import kotlin.collections.List

sealed class UserException(override val message: String, cause: Throwable? = null) :
    DomainException(message, cause) {

    class UserAlreadyAreFriends(requesterId: UUID, addressedId: UUID) :
        UserException("Users $requesterId and $addressedId already are friends")
}

/* TODO:
    1- photo url
    2- genero
    3- edad
    4- cantidad de amigos devolverlo
* */
data class User(
    val id: UUID,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val creationDate: Date = Date(),
    internal val friends: MutableSet<UUID> = mutableSetOf()
) : DomainEntity {

    fun save(userRepository: RepositoryPort<User, UserCriteria>): User {
        return userRepository.save(this)
    }

    fun beFriends(user: User) {
        if (this.id != user.id && !this.friends.contains(user.id)) {
            this.friends.add(user.id)
            user.beFriends(this)
        }
    }

    fun checkIfAlreadyAreFriends(user: User) {
        if (this.friends.contains(user.id)) {
            throw UserException.UserAlreadyAreFriends(this.id, user.id)
        }
    }

    companion object {

        fun checkIfEntitiesExist(userRepository: RepositoryPort<User, UserCriteria>, criteria: List<UserCriteria>) {
            criteria.forEach {
                userRepository.findOrFail(it)
            }
        }
    }
}