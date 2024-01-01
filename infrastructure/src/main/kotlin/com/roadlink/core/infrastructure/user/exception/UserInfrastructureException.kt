package com.roadlink.core.infrastructure.user.exception

import com.roadlink.core.infrastructure.InfrastructureException

sealed class UserInfrastructureException(override val message: String, cause: Throwable? = null) :
    InfrastructureException(message, cause) {

    class NotFound(conditionExpression: String) :
        UserInfrastructureException("None user entity found for the following condition: $conditionExpression")

    class CriteriaEmpty : UserInfrastructureException("You must specify at least a filter")
}