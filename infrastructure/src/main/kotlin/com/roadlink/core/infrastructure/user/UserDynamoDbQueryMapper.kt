package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.DynamoDbQueryMapper

class UserDynamoDbQueryMapper : DynamoDbQueryMapper<UserCriteria, UserDynamoDbQuery> {
    override fun from(criteria: UserCriteria): UserDynamoDbQuery {
        return UserDynamoDbQuery(
            id = criteria.id,
            email = criteria.email
        )
    }
}