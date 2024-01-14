package com.roadlink.application.feedback

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria

class CreateFeedbackCommandResponse(val feedback: FeedbackDTO) : CommandResponse

class CreateFeedbackCommand(val feedback: FeedbackDTO) : Command


class CreateFeedbackCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>
) :
    CommandHandler<CreateFeedbackCommand, CreateFeedbackCommandResponse> {
    override fun handle(command: CreateFeedbackCommand): CreateFeedbackCommandResponse {
        User.checkIfEntitiesExist(
            userRepository,
            listOf(
                UserCriteria(id = command.feedback.reviewerId),
                UserCriteria(id = command.feedback.receiverId)
            )
        )
        command.feedback.toDomain().save(feedbackRepository).also { feedback ->
            return CreateFeedbackCommandResponse(feedback = FeedbackDTO.from(feedback))
        }
    }
}