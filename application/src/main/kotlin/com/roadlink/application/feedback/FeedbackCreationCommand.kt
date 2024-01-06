package com.roadlink.application.feedback

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria

class FeedbackCreationCommandResponse(val feedback: FeedbackDTO) : CommandResponse

class FeedbackCreationCommand(val feedback: FeedbackDTO) : Command


class FeedbackCreationCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>
) :
    CommandHandler<FeedbackCreationCommand, FeedbackCreationCommandResponse> {
    override fun handle(command: FeedbackCreationCommand): FeedbackCreationCommandResponse {
        userRepository.findOrFail(UserCriteria(id = command.feedback.receiverId))
        userRepository.findOrFail(UserCriteria(id = command.feedback.reviewerId))
        val feedback = feedbackRepository.save(command.feedback.toDomain())
        return FeedbackCreationCommandResponse(feedback = FeedbackDTO.from(feedback))
    }
}