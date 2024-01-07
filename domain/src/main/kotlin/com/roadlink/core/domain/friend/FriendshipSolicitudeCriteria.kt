package com.roadlink.core.domain.friend

import com.roadlink.core.domain.DomainCriteria
import java.util.*


class FriendshipSolicitudeCriteria(
    val id: UUID? = null,
    val requesterId: UUID? = null,
    val addressedId: UUID? = null,
) : DomainCriteria