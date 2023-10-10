package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.User
import java.util.*

object UserFactory {

    fun common(id: UUID = UUID.randomUUID()): User {
        return User(
            id = id,
            email = "cabrerajjorge@gmail.com",
            firstName = "Jorge Javier",
            lastName = "Cabrera Vera"
        )
    }
}