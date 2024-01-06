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

data class FeedbacksInfo(
    @JsonProperty("given")
    val given: Int,
    @JsonProperty("received")
    val received: Int
)

data class UserTrustScoreResponse(
    @JsonProperty("score")
    val score: Double,
    @JsonProperty("feedbacks")
    val feedbacksInfo: FeedbacksInfo,
    @JsonProperty("enrollment_days")
    val enrollmentDays: Long
) {

    companion object {
        fun from(response: UserTrustScoreDTO): UserTrustScoreResponse {
            return UserTrustScoreResponse(
                score = response.score,
                feedbacksInfo = FeedbacksInfo(
                    received = response.feedbacksReceived,
                    given = response.feedbacksGiven
                ),
                enrollmentDays = response.enrollmentDays
            )
        }
    }
}