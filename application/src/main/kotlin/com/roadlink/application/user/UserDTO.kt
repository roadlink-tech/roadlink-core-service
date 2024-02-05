package com.roadlink.application.user

import com.roadlink.core.domain.DefaultIdGenerator
import com.roadlink.core.domain.user.User
import com.roadlink.core.infrastructure.DefaultLocalDateTimeHandler
import java.util.*

data class UserDTO(
    val id: UUID = DefaultIdGenerator().next(),
    val email: String,
    val firstName: String,
    val lastName: String,
    val profilePhotoUrl: String,
    val gender: String,
    val birthDay: String,
    val userName: String = "",
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
            birthDay = DefaultLocalDateTimeHandler.from(birthDay),
            userName = userName,
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
                birthDay = DefaultLocalDateTimeHandler.toString(user.birthDay),
                friends = user.friends,
                userName = user.userName
            )
        }
    }
}
