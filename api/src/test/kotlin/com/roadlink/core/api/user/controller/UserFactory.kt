package com.roadlink.core.api.user.controller

import com.roadlink.core.infrastructure.DefaultLocalDateTimeHandler
import com.roadlink.core.domain.user.User
import java.util.*

object UserFactory {

    fun common(
        id: UUID = UUID.randomUUID(),
        email: String = "cabrerajjorge@gmail.com",
        firstName: String = "jorge",
        lastName: String = "cabrera",
        gender: String = "male",
        profilePhotoUrl: String = "https://profile.photo.com",
        birthDay: String = "06/12/1991",
        userName: String = "jorgecabrera",
        friends: MutableSet<UUID> = mutableSetOf(),
    ): User {
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl,
            birthDay = DefaultLocalDateTimeHandler.from(birthDay),
            userName = userName,
            friends = friends,
        )
    }

    fun withTooManyFriend(
        id: UUID = UUID.randomUUID(),
        email: String = "cabrerajjorge@gmail.com",
        firstName: String = "jorge",
        lastName: String = "cabrera",
        gender: String = "male",
        birthDay: String = "06/12/1991",
        userName: String = "jorge.cabrera",
        amountOfFriends: Int = 50
    ): User {
        val friends = mutableSetOf<UUID>()
        repeat(amountOfFriends) {
            friends.add(UUID.randomUUID())
        }
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            friends = friends,
            gender = gender,
            birthDay = DefaultLocalDateTimeHandler.from(birthDay),
            userName = userName
        )

    }

    fun martin(
        friends: Set<UUID> = emptySet(),
    ): User {
        return common(
            email = "martinbosch@gmail.com",
            firstName = "martin",
            lastName = "bosch",
            userName = "martinbosch",
            friends = friends.toMutableSet(),
        )
    }

    fun felix(): User {
        return common(
            email = "felixreyero@gmail.com",
            firstName = "felix",
            lastName = "reyero",
            userName = "felixreyero"
        )
    }

}