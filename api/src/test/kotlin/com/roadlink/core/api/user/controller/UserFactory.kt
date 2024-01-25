package com.roadlink.core.api.user.controller

import com.roadlink.core.domain.user.User
import java.util.*

object UserFactory {

    fun common(
        id: UUID = UUID.randomUUID(),
        email: String = "cabrerajjorge@gmail.com",
        firstName: String = "jorge",
        lastName: String = "cabrera"
    ): User {
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName
        )
    }

}