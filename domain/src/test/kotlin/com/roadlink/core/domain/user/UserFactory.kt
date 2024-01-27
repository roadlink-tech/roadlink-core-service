package com.roadlink.core.domain.user

import java.time.LocalDate
import java.time.ZoneId
import java.util.*


object UserFactory {
    fun common(
        id: UUID = UUID.randomUUID(),
        email: String = "cabrerajjorge@gmail.com",
        firstName: String = "Jorge",
        lastName: String = "Cabrera",
        profilePhotoUrl: String = "https://lh3.googleusercontent.com/a/ACg8ocJW5g-yavaNzKPZcF-U8-W5zGfIQdww2mOcyDq_48xfdHE=s96-c"
    ): User {
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            creationDate = Date(),
            profilePhotoUrl = profilePhotoUrl
        )
    }

    fun old(
        id: UUID = UUID.randomUUID(),
        email: String = "cabrerajjorge@gmail.com",
        firstName: String = "Jorge",
        lastName: String = "Cabrera",
        profilePhotoUrl: String = "https://lh3.googleusercontent.com/a/ACg8ocJW5g-yavaNzKPZcF-U8-W5zGfIQdww2mOcyDq_48xfdHE=s96-c"
    ): User {
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            creationDate = Date.from(
                LocalDate.now().minusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            ),
            profilePhotoUrl = profilePhotoUrl
        )
    }

    fun withFriends(
        id: UUID = UUID.randomUUID(),
        email: String = "cabrerajjorge@gmail.com",
        firstName: String = "Jorge",
        lastName: String = "Cabrera",
        profilePhotoUrl: String = "https://lh3.googleusercontent.com/a/ACg8ocJW5g-yavaNzKPZcF-U8-W5zGfIQdww2mOcyDq_48xfdHE=s96-c",
        amountOfFriends: Int = 100,
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
            friends = friendIds,
            profilePhotoUrl = profilePhotoUrl
        )
    }
}