package com.roadlink.core.domain.user

import java.util.UUID

interface UserRepositoryPort {
    fun save(user: User): User
    fun findOrFail(criteria: UserCriteria): User
}

class UserCriteria(
    val id: UUID? = null,
    val email: String = ""
)