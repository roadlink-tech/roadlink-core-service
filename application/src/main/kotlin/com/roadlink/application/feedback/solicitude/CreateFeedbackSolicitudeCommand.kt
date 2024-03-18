package com.roadlink.application.feedback.solicitude

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitudeCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria

class CreateFeedbackSolicitudeCommandResponse(val solicitude: FeedbackSolicitudeDTO) : CommandResponse

class CreateFeedbackSolicitudeCommand(val solicitude: FeedbackSolicitudeDTO) : Command

class CreateFeedbackSolicitudeCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val feedbackSolicitudeRepository: RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria>
) :
    CommandHandler<CreateFeedbackSolicitudeCommand, CreateFeedbackSolicitudeCommandResponse> {
    override fun handle(command: CreateFeedbackSolicitudeCommand): CreateFeedbackSolicitudeCommandResponse {
        User.checkIfEntitiesExist(
            userRepository,
            listOf(
                UserCriteria(id = command.solicitude.reviewerId),
                UserCriteria(id = command.solicitude.receiverId)
            )
        )
        command.solicitude.toDomain().save(feedbackSolicitudeRepository).also { solicitude ->
            return CreateFeedbackSolicitudeCommandResponse(solicitude = FeedbackSolicitudeDTO.from(solicitude))
        }
    }
}

