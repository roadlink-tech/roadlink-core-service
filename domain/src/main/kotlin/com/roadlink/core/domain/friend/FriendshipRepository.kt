package com.roadlink.core.domain.friend

import java.util.*

interface FriendshipRepositoryPort {
    fun save(user: FriendshipRequest): FriendshipRequest
    fun findOrFail(criteria: FriendshipRequestCriteria): FriendshipRequest
}

class FriendshipRequestCriteria(
    val id: UUID? = null
)