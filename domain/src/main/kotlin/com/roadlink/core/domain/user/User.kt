package com.roadlink.core.domain.user

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.DomainException
import com.roadlink.core.domain.RepositoryPort
import java.util.*

sealed class UserException(override val message: String, cause: Throwable? = null) :
    DomainException(message, cause) {

    class UserAlreadyAreFriends(requesterId: UUID, addressedId: UUID) :
        UserException("Users $requesterId and $addressedId already are friends")

    class UserEmailAlreadyRegistered(email: String) : UserException("User $email is already registered")
}

/* TODO:
    1- photo url
    2- genero
    3- edad
* */
data class User(
    val id: UUID,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val creationDate: Date = Date(),
    val friends: MutableSet<UUID> = mutableSetOf()
) : DomainEntity {

    fun save(userRepository: RepositoryPort<User, UserCriteria>): User {
        return userRepository.save(this)
    }

    fun beFriendOf(user: User) {
        if (this.id != user.id && !this.friends.contains(user.id)) {
            this.friends.add(user.id)
            user.beFriendOf(this)
        }
    }

    fun removeFriend(user: User) {
        if (this.id != user.id && this.friends.contains(user.id)) {
            this.friends.remove(user.id)
            user.removeFriend(this)
        }
    }

    fun checkIfAlreadyAreFriends(user: User) {
        if (this.friends.contains(user.id)) {
            throw UserException.UserAlreadyAreFriends(this.id, user.id)
        }
    }

    companion object {

        fun checkIfUserCanBeCreated(
            userRepository: RepositoryPort<User, UserCriteria>,
            user: User
        ) {
            userRepository.findAll(UserCriteria(email = user.email)).also { users ->
                if (users.isNotEmpty()) {
                    throw UserException.UserEmailAlreadyRegistered(user.email)
                }
            }
        }

        fun checkIfEntitiesExist(
            userRepository: RepositoryPort<User, UserCriteria>,
            criteria: List<UserCriteria>
        ) {
            criteria.forEach {
                userRepository.findOrFail(it)
            }
        }
    }
}
