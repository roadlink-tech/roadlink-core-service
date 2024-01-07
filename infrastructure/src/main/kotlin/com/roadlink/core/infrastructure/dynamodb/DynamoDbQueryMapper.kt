package com.roadlink.core.infrastructure.dynamodb

import com.roadlink.core.domain.DomainCriteria

interface DynamoDbQueryMapper<C : DomainCriteria, Q : BaseDynamoDbQuery> {
    fun from(criteria: C): Q
}