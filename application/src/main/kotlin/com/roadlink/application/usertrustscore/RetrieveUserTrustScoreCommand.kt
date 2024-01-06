package com.roadlink.application.usertrustscore

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.usertrustscore.UserTrustScoreService
import java.util.*

class RetrieveUserTrustScoreCommandResponse(val userTrustScore: UserTrustScoreDTO) : CommandResponse

class RetrieveUserTrustScoreCommand(val userId: String) : Command

class RetrieveUserTrustScoreCommandHandler(private val userTrustScoreService: UserTrustScoreService) :
    CommandHandler<RetrieveUserTrustScoreCommand, RetrieveUserTrustScoreCommandResponse> {

    override fun handle(command: RetrieveUserTrustScoreCommand): RetrieveUserTrustScoreCommandResponse {
        val userTrustScore = userTrustScoreService.findById(UUID.fromString(command.userId))
        return RetrieveUserTrustScoreCommandResponse(userTrustScore = UserTrustScoreDTO.from(userTrustScore))
    }
}