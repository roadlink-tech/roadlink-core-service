package com.roadlink.application.feedback

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.feedback.FeedbackRepositoryPort
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.UserRepositoryPort

class FeedbackCreationCommandResponse(val feedback: FeedbackDTO) : CommandResponse

class FeedbackCreationCommand(val feedback: FeedbackDTO) : Command


class FeedbackCreationCommandHandler(
    private val userRepository: UserRepositoryPort,
    private val feedbackRepository: FeedbackRepositoryPort
) :
    CommandHandler<FeedbackCreationCommand, FeedbackCreationCommandResponse> {
    override fun handle(command: FeedbackCreationCommand): FeedbackCreationCommandResponse {
        val user = userRepository.findOrFail(UserCriteria(id = command.feedback.reviewerId))
        val feedback = feedbackRepository.save(command.feedback.toDomain())
        return FeedbackCreationCommandResponse(feedback = FeedbackDTO.from(feedback))
    }
}