package com.roadlink.application.user

import com.roadlink.core.domain.user.User
import java.util.UUID

object UserFactory {

    fun common(
        id: UUID = UUID.randomUUID(),
        firstName: String = "jorge",
        lastName: String = "cabrera",
        email: String = "cabrerajjorge@gmail.com"
    ): User {
        return User(
            id = id,
            firstName = firstName,
            lastName = lastName,
            email = email
        )
    }
}