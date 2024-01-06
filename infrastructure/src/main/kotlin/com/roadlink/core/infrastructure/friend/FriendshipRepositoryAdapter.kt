package com.roadlink.core.infrastructure.friend

import com.roadlink.core.domain.friend.FriendshipRepositoryPort
import com.roadlink.core.domain.friend.FriendshipRequest
import com.roadlink.core.domain.friend.FriendshipRequestCriteria

class FriendshipRepositoryAdapter : FriendshipRepositoryPort {
    override fun save(user: FriendshipRequest): FriendshipRequest {
        TODO("Not yet implemented")
    }

    override fun findOrFail(criteria: FriendshipRequestCriteria): FriendshipRequest {
        TODO("Not yet implemented")
    }
}