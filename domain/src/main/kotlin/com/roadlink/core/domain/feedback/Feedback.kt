package com.roadlink.core.domain.feedback

import java.util.UUID

data class Feedback(
    val id: UUID,
    val reviewerId: UUID,
    val receiverId: UUID,
    val rating: Int,
    val comment: String
)