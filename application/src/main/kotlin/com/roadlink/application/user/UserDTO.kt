package com.roadlink.application.user

import com.roadlink.application.DefaultIdGenerator
import com.roadlink.core.domain.user.User
import java.util.*

data class UserDTO(
    val id: UUID = DefaultIdGenerator().next(),
    val email: String
) {
    fun toModel(): User {
        return User(id = id, email = email)
    }

    companion object {
        fun from(user: User): UserDTO {
            return UserDTO(id = user.id, email = user.email)
        }
    }
}
