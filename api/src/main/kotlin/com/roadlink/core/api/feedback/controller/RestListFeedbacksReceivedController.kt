package com.roadlink.core.api.feedback.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.feedback.*
import com.roadlink.core.domain.feedback.*
import com.roadlink.core.domain.friend.FriendshipStatus
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus.OK
import java.util.*

@RestController
@RequestMapping("/users/{user_id}/feedbacks_received")
class RestListFeedbacksReceivedController(
    private val commandBus: CommandBus,
) {
    @GetMapping
    @ResponseBody
    @ResponseStatus(value = OK)
    fun list(
        @PathVariable("user_id") receiverId: String,
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
    @JsonProperty("type")
    override val type: String = "NOT_BEEN_SCORED",
) : ScoreResultResponse(type)

data class ScoredResponse(
    @JsonProperty("type")
    override val type: String = "SCORED",
    @JsonProperty("score")
    val score: Double,
) : ScoreResultResponse(type)

data class UserCompactDisplayResponse(
    @JsonProperty("user_id")
    val userId: String,
    @JsonProperty("first_name")
    val firstName: String,
    @JsonProperty("last_name")
    val lastName: String,
    @JsonProperty("profile_photo_url")
    val profilePhotoUrl: String,
    @JsonProperty("score")
    val score: ScoreResultResponse,
    @JsonProperty("username")
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
    @JsonProperty("id")
    val id: String,
    @JsonProperty("reviewer_user_compact_display")
    val reviewerUserCompactDisplay: UserCompactDisplayResponse,
    @JsonProperty("friendship_status")
    val friendshipStatus: FriendshipStatus,
    @JsonProperty("trip_leg_id")
    val tripLegId: String,
    @JsonProperty("comment")
    val comment: String,
    @JsonProperty("rating")
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
