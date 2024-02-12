package com.roadlink.core.domain.user

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.DomainException
import com.roadlink.core.domain.IdGenerator
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.google.GoogleIdTokenPayload
import java.time.LocalDate
import java.util.*
import javax.xml.stream.events.DTD

sealed class UserException(override val message: String, cause: Throwable? = null) :
    DomainException(message, cause) {

    class UserAlreadyAreFriends(requesterId: UUID, addressedId: UUID) :
        UserException("Users $requesterId and $addressedId already are friends")

    class UserEmailAlreadyRegistered(email: String) :
        UserException("User $email is already registered")
}

data class User(
    val id: UUID,
    var email: String = "",
    var firstName: String = "",
    var lastName: String = "",
    val creationDate: Date = Date(),
    var gender: String = "",
    val friends: MutableSet<UUID> = mutableSetOf(),
    var profilePhotoUrl: String = "",
    var birthDay: LocalDate? = null,
    val userName: String,
) : DomainEntity {

    fun save(userRepository: RepositoryPort<User, UserCriteria>): User {
        return userRepository.save(this)
    }

    fun merge(user: User): User {
        return apply {
            if (user.email != "") {
                this.email = user.email
            }
            if (user.firstName != "") {
                this.firstName = user.firstName
            }
            if (user.lastName != "") {
                this.lastName = user.lastName
            }
            if (user.gender != "") {
                this.gender = user.gender
            }
            if (user.profilePhotoUrl != "") {
                this.profilePhotoUrl = user.profilePhotoUrl
            }
            if (user.birthDay != null) {
                this.birthDay = user.birthDay
            }
        }
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

        fun from(
            googleIdTokenPayload: GoogleIdTokenPayload,
            idGenerator: IdGenerator,
            userNameGenerator: UserNameGenerator
        ): User {
            val userName = userNameGenerator.from(
                firstName = googleIdTokenPayload.givenName,
                lastName = googleIdTokenPayload.familyName
            )
            return User(
                id = idGenerator.next(),
                email = googleIdTokenPayload.email,
                firstName = googleIdTokenPayload.givenName,
                lastName = googleIdTokenPayload.familyName,
                profilePhotoUrl = googleIdTokenPayload.profilePhotoUrl,
                userName = userName
            )
        }

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

        fun checkIfEmailIsBeingUsed(
            userRepository: RepositoryPort<User, UserCriteria>,
            email: String
        ) {
            userRepository.findOrNull(UserCriteria(email = email))
                .takeIf { user: User? -> user != null }
                ?.let { throw UserException.UserEmailAlreadyRegistered(email) }
        }
    }
}
