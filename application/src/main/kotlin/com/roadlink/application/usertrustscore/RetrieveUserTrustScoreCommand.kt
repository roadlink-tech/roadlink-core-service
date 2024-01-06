package com.roadlink.application.usertrustscore

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.feedback.FeedbackRepositoryPort
import com.roadlink.core.domain.user.UserRepositoryPort
import com.roadlink.core.domain.usertrustscore.UserTrustScore
import java.util.*

class RetrieveUserTrustScoreCommandResponse(val userTrustScore: UserTrustScoreDTO) : CommandResponse

class RetrieveUserTrustScoreCommand(val userId: String) : Command

class RetrieveUserTrustScoreCommandHandler(
    private val userRepository: UserRepositoryPort,
    private val feedbackRepository: FeedbackRepositoryPort
) :
    CommandHandler<RetrieveUserTrustScoreCommand, RetrieveUserTrustScoreCommandResponse> {

    override fun handle(command: RetrieveUserTrustScoreCommand): RetrieveUserTrustScoreCommandResponse {
        val userTrustScore =
            UserTrustScore.get(UUID.fromString(command.userId), userRepository, feedbackRepository)
        return RetrieveUserTrustScoreCommandResponse(userTrustScore = UserTrustScoreDTO.from(userTrustScore))
    }
}