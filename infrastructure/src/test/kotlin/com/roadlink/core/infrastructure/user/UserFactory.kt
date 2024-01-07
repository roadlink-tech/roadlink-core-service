package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.User
import java.util.*

object UserFactory {

    fun custom(
        id: UUID = UUID.randomUUID(),
        email: String = "cabrerajjorge@gmail.com",
        friends: Set<UUID> = setOf()
    ): User {
        return User(
            id = id,
            email = email,
            firstName = "Jorge Javier",
            lastName = "Cabrera Vera",
            friends = friends.toMutableSet()
        )
    }
}