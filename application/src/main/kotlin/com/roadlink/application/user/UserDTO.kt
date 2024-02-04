package com.roadlink.application.user

import com.roadlink.core.infrastructure.ApplicationDateTime
import com.roadlink.core.domain.DefaultIdGenerator
import com.roadlink.core.domain.user.User
import java.time.format.DateTimeFormatter
import java.util.*

private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

data class UserDTO(
    val id: UUID = DefaultIdGenerator().next(),
    val email: String,
    val firstName: String,
    val lastName: String,
    val profilePhotoUrl: String,
    val gender: String,
    val birthDay: String,
    val friends: Set<UUID> = emptySet()
    // TODO ojo ver comoo manejar el registration date
) {
    fun toDomain(): User {
        return User(
            id = id,
            email = email,
            firstName = firstName,
            lastName = lastName,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl,
            birthDay = ApplicationDateTime.from(birthDay),
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
                gender = user.gender,
                profilePhotoUrl = user.profilePhotoUrl,
                birthDay = ApplicationDateTime.toString(user.birthDay),
                friends = user.friends
            )
        }
    }
}
