package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.User
import java.time.LocalDate
import java.util.*

object UserFactory {

    fun custom(
        id: UUID = UUID.randomUUID(),
        firstName: String = "Jorge Javier",
        lastName: String = "Cabrera Vera",
        email: String = "cabrerajjorge@gmail.com",
        friends: Set<UUID> = setOf(),
        profilePhotoUrl: String = "https://lh3.googleusercontent.com/a/ACg8ocJW5g-yavaNzKPZcF-U8-W5zGfIQdww2mOcyDq_48xfdHE=s96-c",
        birthDay: LocalDate? = null,
        gender: String = "male",
        userName: String = "jorge.cabrera"
    ): User {
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            friends = friends.toMutableSet(),
            profilePhotoUrl = profilePhotoUrl,
            birthDay = birthDay,
            gender = gender,
            userName = userName
        )
    }
}