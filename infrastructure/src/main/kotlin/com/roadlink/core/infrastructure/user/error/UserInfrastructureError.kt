package com.roadlink.core.infrastructure.user.error

import com.roadlink.core.infrastructure.InfrastructureException

sealed class UserInfrastructureError(override val message: String, cause: Throwable? = null) :
    InfrastructureException(message, cause) {

    class UserNotFound(conditionExpression: String) :
        UserInfrastructureError("None user entity found for the following condition: $conditionExpression")
}