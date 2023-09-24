package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.UserRepositoryPort
import java.util.UUID

class UserRepositoryAdapter : UserRepositoryPort {
    override fun findOrFail(criteria: UserCriteria): User {
        // TODO change it when the infrastructure entity was done
        return User(id = UUID.randomUUID(), email = "cabrerajjorge@gmail.com")
    }
}