package com.roadlink.core.infrastructure.user.exception

import com.roadlink.core.infrastructure.InfrastructureException
import java.util.*

sealed class UserInfrastructureException(override val message: String, cause: Throwable? = null) :
    InfrastructureException(message, cause) {

    class NotFound(entityId: UUID? = null) :
        UserInfrastructureException("User $entityId not found")

    class CriteriaEmpty : UserInfrastructureException("You must specify at least a filter")
}