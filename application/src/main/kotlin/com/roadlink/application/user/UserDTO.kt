package com.roadlink.application.user

import com.roadlink.application.DefaultIdGenerator
import com.roadlink.core.domain.user.User
import java.util.*

data class UserDTO(
    val id: UUID = DefaultIdGenerator().next(),
    val email: String,
    val firstName: String,
    val lastName: String,
    val friends: Set<UUID> = emptySet()
    // TODO ojo ver comoo manejar el registration date
) {
    fun toDomain(): User {
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            friends = friends.toMutableSet()
        )
    }

    companion object {
        fun from(user: User): UserDTO {
            return UserDTO(
                id = user.id,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                friends = user.friends
            )
        }
    }
}
