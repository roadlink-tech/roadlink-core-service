package com.roadlink.core.domain.feedback.solicitude

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.validation.FeedbackSolicitudeException
import java.util.*

/**
 * This object will create a Feedback entity when the user complete it.
 */
data class FeedbackSolicitude(
    val id: UUID,
    val tripId: UUID,
    val reviewerId: UUID,
    val receiverId: UUID,
    val status: Status = Status.PENDING
) : DomainEntity {

    fun save(feedbackRepository: RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria>): FeedbackSolicitude {
        return feedbackRepository.save(this)
    }

    fun complete(
        comment: String,
        rating: Int,
        feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>,
        feedbackSolicitudeRepository: RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria>
    ): FeedbackSolicitude {
        if (status == Status.REJECTED) {
            throw FeedbackSolicitudeException.FeedbackSolicitudeAlreadyRejected(id)
        }
        if (status == Status.COMPLETED) {
            throw FeedbackSolicitudeException.FeedbackSolicitudeAlreadyCompleted(id)
        }

        val feedback = Feedback(
            id = UUID.randomUUID(),
            reviewerId = reviewerId,
            receiverId = receiverId,
            tripId = tripId,
            rating = rating,
            comment = comment
        )
        feedbackRepository.save(feedback)
        feedbackSolicitudeRepository.save(this.copy(status = Status.COMPLETED)).also { return it }
    }

    fun reject(
        feedbackSolicitudeRepository: RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria>
    ) {
        feedbackSolicitudeRepository.save(this.copy(status = Status.REJECTED))
    }

    enum class Status {
        PENDING,
        REJECTED,
        COMPLETED
    }

    companion object {

        fun findByIdAndStatusPending(
            feedbackSolicitudeRepository: RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria>,
            id: UUID
        ): FeedbackSolicitude {
            return feedbackSolicitudeRepository.findOrFail(
                FeedbackSolicitudeCriteria(
                    id = id,
                    status = Status.PENDING
                )
            )
        }
    }
}