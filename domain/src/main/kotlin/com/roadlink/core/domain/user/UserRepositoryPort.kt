package com.roadlink.core.domain.user

import java.util.UUID

interface UserRepositoryPort {
//    fun findOrFail(criteria: UserCriteria): User
    fun save(user: User): User
}

class UserCriteria(
    val id: UUID? = null,
    val email: String? = null
)