package com.roadlink.application.feedback

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.FeedbackRepositoryPort
import java.util.UUID

class RetrieveFeedbacksCommandResponse(val feedbacks: List<FeedbackDTO>) : CommandResponse

class RetrieveFeedbacksCommand(val receiverId: String) : Command

class RetrieveFeedbacksCommandHandler(private val repository: FeedbackRepositoryPort) :
    CommandHandler<RetrieveFeedbacksCommand, RetrieveFeedbacksCommandResponse> {

    override fun handle(command: RetrieveFeedbacksCommand): RetrieveFeedbacksCommandResponse {
        val response =
            repository.findAll(FeedbackCriteria(receiverId = UUID.fromString(command.receiverId)))
        return RetrieveFeedbacksCommandResponse(feedbacks = response.map { FeedbackDTO.from(it) })
    }
}