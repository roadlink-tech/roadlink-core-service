package com.roadlink.application.usertrustscore

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.application.user.UserDTO
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.FeedbackRepositoryPort
import java.util.*

class RetrieveUserTrustScoreCommandResponse(val user: UserDTO) : CommandResponse

class RetrieveUserTrustScoreCommand(val userId: String) : Command

class RetrieveUserTrustScoreCommandHandler(private val feedbackRepositoryPort: FeedbackRepositoryPort) :
    CommandHandler<RetrieveUserTrustScoreCommand, RetrieveUserTrustScoreCommandResponse> {

    override fun handle(command: RetrieveUserTrustScoreCommand): RetrieveUserTrustScoreCommandResponse {
        val feedbacks = feedbackRepositoryPort.findAll(
            criteria = FeedbackCriteria(
                receiverId = UUID.fromString(command.userId)
            )
        )

        TODO()
        //return RetrieveUserTrustScoreCommandResponse(user = UserDTO.from(user))
    }
}