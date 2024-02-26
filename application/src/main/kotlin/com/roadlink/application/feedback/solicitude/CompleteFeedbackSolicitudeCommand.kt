package com.roadlink.application.feedback.solicitude

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.application.feedback.FeedbackDTO
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitudeCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import java.util.UUID

class CompleteFeedbackSolicitudeCommandResponse(val feedback: FeedbackDTO) : CommandResponse

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
        val solicitude =
            feedbackSolicitudeRepository.findOrFail(FeedbackSolicitudeCriteria(id = command.solicitudeCompletion.feedbackSolicitudeId))
        solicitude.complete(
            comment = command.solicitudeCompletion.comment,
            rating = command.solicitudeCompletion.rating,
            feedbackRepository = feedbackRepository,
            feedbackSolicitudeRepository = feedbackSolicitudeRepository
        ).also { feedback ->
            return CompleteFeedbackSolicitudeCommandResponse(feedback = FeedbackDTO.from(feedback))
        }
    }
}

data class FeedbackSolicitudeCompletion(
    val reviewerId: UUID,
    val feedbackSolicitudeId: UUID,
    val comment: String,
    val rating: Int
)
