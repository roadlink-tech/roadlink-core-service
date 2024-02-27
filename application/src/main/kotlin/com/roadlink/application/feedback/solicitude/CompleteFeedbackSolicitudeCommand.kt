package com.roadlink.application.feedback.solicitude

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude.*
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitudeCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import java.util.UUID

class CompleteFeedbackSolicitudeCommandResponse(val solicitude: FeedbackSolicitudeDTO) : CommandResponse

class CompleteFeedbackSolicitudeCommand(val solicitudeCompletion: FeedbackSolicitudeCompletion) : Command

class CompleteFeedbackSolicitudeCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>,
    private val feedbackSolicitudeRepository: RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria>
) :
    CommandHandler<CompleteFeedbackSolicitudeCommand, CompleteFeedbackSolicitudeCommandResponse> {
    override fun handle(command: CompleteFeedbackSolicitudeCommand): CompleteFeedbackSolicitudeCommandResponse {
        User.checkIfEntitiesExist(
            userRepository,
            listOf(
                UserCriteria(id = command.solicitudeCompletion.reviewerId),
            )
        )
        val solicitude = FeedbackSolicitude.findByIdAndStatusPending(
            feedbackSolicitudeRepository,
            id = command.solicitudeCompletion.feedbackSolicitudeId
        )

        solicitude.complete(
            comment = command.solicitudeCompletion.comment,
            rating = command.solicitudeCompletion.rating,
            feedbackRepository = feedbackRepository,
            feedbackSolicitudeRepository = feedbackSolicitudeRepository
        ).also { feedbackSolicitude ->
            return CompleteFeedbackSolicitudeCommandResponse(solicitude = FeedbackSolicitudeDTO.from(feedbackSolicitude))
        }
    }
}

data class FeedbackSolicitudeCompletion(
    val reviewerId: UUID,
    val feedbackSolicitudeId: UUID,
    val comment: String,
    val rating: Int
)
