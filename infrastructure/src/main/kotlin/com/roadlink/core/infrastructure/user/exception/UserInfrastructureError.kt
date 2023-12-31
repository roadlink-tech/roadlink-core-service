package com.roadlink.core.infrastructure.user.exception

import com.roadlink.core.infrastructure.InfrastructureException

sealed class UserInfrastructureError(override val message: String, cause: Throwable? = null) :
    InfrastructureException(message, cause) {

    class NotFound(conditionExpression: String) :
        UserInfrastructureError("None user entity found for the following condition: $conditionExpression")

    class CriteriaEmpty : UserInfrastructureError("You must specify at least a filter")
}