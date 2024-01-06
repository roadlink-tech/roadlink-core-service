package com.roadlink.core.api.usertrustscore.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.usertrustscore.RetrieveUserTrustScoreCommand
import com.roadlink.application.usertrustscore.RetrieveUserTrustScoreCommandResponse
import com.roadlink.application.usertrustscore.UserTrustScoreDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/{userId}/user_trust_score")
class RestUserTrustScoreController(private val commandBus: CommandBus) {

    @GetMapping
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    fun retrieve(@PathVariable("userId") userId: String): UserTrustScoreResponse {
        val response = commandBus.publish<RetrieveUserTrustScoreCommand, RetrieveUserTrustScoreCommandResponse>(
            RetrieveUserTrustScoreCommand(userId)
        )
        return UserTrustScoreResponse.from(response.userTrustScore)
    }

}

data class UserTrustScoreResponse(
    @JsonProperty("score")
    val score: Double
) {

    companion object {
        fun from(response: UserTrustScoreDTO): UserTrustScoreResponse {
            return UserTrustScoreResponse(
                score = response.score
            )
        }
    }
}