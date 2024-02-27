package com.roadlink.application.feedback.solicitude

import com.roadlink.core.domain.DefaultIdGenerator
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import java.util.*

data class FeedbackSolicitudeDTO(
    val id: UUID = DefaultIdGenerator().next(),
    val reviewerId: UUID,
    val receiverId: UUID,
    val tripId: UUID,
    val status: FeedbackSolicitude.Status? = null
) {

    fun toDomain(): FeedbackSolicitude {
        return FeedbackSolicitude(
            id = id,
            receiverId = receiverId,
            reviewerId = reviewerId,
            tripId = tripId
        )
    }

    companion object {
        fun from(solicitude: FeedbackSolicitude): FeedbackSolicitudeDTO {
            return FeedbackSolicitudeDTO(
                id = solicitude.id,
                reviewerId = solicitude.reviewerId,
                receiverId = solicitude.receiverId,
                tripId = solicitude.tripId,
                status = solicitude.status
            )
        }
    }
}