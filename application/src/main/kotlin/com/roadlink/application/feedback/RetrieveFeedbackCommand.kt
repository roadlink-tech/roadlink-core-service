package com.roadlink.application.feedback

import com.roadlink.application.command.CommandHandler
import com.roadlink.core.domain.feedback.FeedbackRepositoryPort

class RetrieveFeedbackCommand {
}

class RetrieveFeedbackCommandHandler(private val repository: FeedbackRepositoryPort) :
    CommandHandler<FeedbackCreationCommand, FeedbackCreationCommandResponse> {
    override fun handle(command: FeedbackCreationCommand): FeedbackCreationCommandResponse {
        val feedback = repository.save(command.feedback.toDomain())
        return FeedbackCreationCommandResponse(feedback = FeedbackDTO.from(feedback))
    }
}