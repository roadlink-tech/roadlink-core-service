package com.roadlink.core.domain.user

import java.time.LocalDate
import java.time.ZoneId
import java.util.*


object UserFactory {

    fun common(
        id: UUID = UUID.randomUUID(),
        email: String = "cabrerajjorge@gmail.com",
        firstName: String = "Jorge",
        lastName: String = "Cabrera"
    ): User {
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            creationDate = Date()
        )
    }

    fun old(
        id: UUID = UUID.randomUUID(),
        email: String = "cabrerajjorge@gmail.com",
        firstName: String = "Jorge",
        lastName: String = "Cabrera"
    ): User {
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            creationDate = Date.from(
                LocalDate.now().minusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            )
        )
    }

    fun withFriends(
        id: UUID = UUID.randomUUID(),
        email: String = "cabrerajjorge@gmail.com",
        firstName: String = "Jorge",
        lastName: String = "Cabrera",
        amountOfFriends: Int = 100
    ): User {
        val friendIds = mutableSetOf<UUID>()
        repeat(amountOfFriends) {
            friendIds.add(UUID.randomUUID())
        }
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            creationDate = Date(),
            friends = friendIds
        )
    }
}