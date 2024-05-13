package com.roadlink.core.api.feedback.controller

import com.roadlink.application.command.CommandBus
import com.roadlink.application.feedback.*
import com.roadlink.core.domain.feedback.*
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus.OK
import java.util.*

@RestController
@RequestMapping("/users/{userId}/feedbacks_received")
class RestListFeedbacksReceivedController(
    private val commandBus: CommandBus,
) {
    @GetMapping
    @ResponseBody
    @ResponseStatus(value = OK)
    fun list(
        @PathVariable("userId") receiverId: String,
        @RequestHeader("X-Caller-Id") callerId: String,
    ): List<FeedbackReceivedResponse> {
        val response = commandBus.publish<ListFeedbacksReceivedCommand, ListFeedbacksReceivedCommandResponse>(
            ListFeedbacksReceivedCommand(
                receiverId = UUID.fromString(receiverId),
                callerId = UUID.fromString(callerId),
            )
        )
        return response.feedbacksReceived.map { FeedbackReceivedResponse.from(it) }
    }
}

sealed class ScoreResultResponse(open val type: String) {
    companion object {
        fun from(scoreResult: ScoreResult): ScoreResultResponse =
            when (scoreResult) {
                NotBeenScored -> NotBeenScoredResponse()
                is Scored -> ScoredResponse(score = scoreResult.score)
            }
    }
}

data class NotBeenScoredResponse(
    override val type: String = "NOT_BEEN_SCORED",
) : ScoreResultResponse(type)

data class ScoredResponse(
    override val type: String = "SCORED",
    val score: Double,
) : ScoreResultResponse(type)

data class UserCompactDisplayResponse(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val profilePhotoUrl: String,
    val score: ScoreResultResponse,
    val username: String,
) {
    companion object {
        fun from(userCompactDisplay: UserCompactDisplay): UserCompactDisplayResponse =
            UserCompactDisplayResponse(
                userId = userCompactDisplay.userId,
                firstName = userCompactDisplay.firstName,
                lastName = userCompactDisplay.lastName,
                profilePhotoUrl = userCompactDisplay.profilePhotoUrl,
                score = ScoreResultResponse.from(userCompactDisplay.score),
                username = userCompactDisplay.username,
            )
    }
}

data class FeedbackReceivedResponse(
    val id: String,
    val reviewerUserCompactDisplay: UserCompactDisplayResponse,
    val friendshipStatus: FriendshipStatus,
    val tripLegId: String,
    val comment: String,
    val rating: Int,
) {
    companion object {
        fun from(feedbackReceived: FeedbackReceived): FeedbackReceivedResponse =
            FeedbackReceivedResponse(
                id = feedbackReceived.id,
                reviewerUserCompactDisplay = UserCompactDisplayResponse.from(feedbackReceived.reviewerUserCompactDisplay),
                friendshipStatus = feedbackReceived.friendshipStatus,
                tripLegId = feedbackReceived.tripLegId,
                comment = feedbackReceived.comment,
                rating = feedbackReceived.rating,
            )
    }
}
