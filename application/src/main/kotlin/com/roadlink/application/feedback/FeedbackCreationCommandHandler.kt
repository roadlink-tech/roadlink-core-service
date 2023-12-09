package com.roadlink.application.feedback

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.feedback.FeedbackRepositoryPort

class FeedbackCreationCommandResponse(val feedback: FeedbackDTO) : CommandResponse

class FeedbackCreationCommand(val feedback: FeedbackDTO) : Command


class FeedbackCreationCommandHandler(private val repository: FeedbackRepositoryPort) :
    CommandHandler<FeedbackCreationCommand, FeedbackCreationCommandResponse> {
    override fun handle(command: FeedbackCreationCommand): FeedbackCreationCommandResponse {
        val feedback = repository.save(command.feedback.toDomain())
        return FeedbackCreationCommandResponse(feedback = FeedbackDTO.from(feedback))
    }
}