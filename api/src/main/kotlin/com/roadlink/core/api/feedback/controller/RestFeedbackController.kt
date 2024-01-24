package com.roadlink.core.api.feedback.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.feedback.CreateFeedbackCommand
import com.roadlink.application.feedback.CreateFeedbackCommandResponse
import com.roadlink.application.feedback.FeedbackDTO
import com.roadlink.application.feedback.RetrieveFeedbacksCommand
import com.roadlink.application.feedback.RetrieveFeedbacksCommandResponse
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/users/{receiverId}/feedbacks")
class FeedbackController(private val commandBus: CommandBus) {

    @PostMapping
    @ResponseBody
    @ResponseStatus(value = CREATED)
    fun createFeedback(
        @PathVariable receiverId: String,
        @RequestBody request: FeedbackCreationRequest
    ): FeedbackResponse {
        val response =
            commandBus.publish<CreateFeedbackCommand, CreateFeedbackCommandResponse>(
                CreateFeedbackCommand(request.toDto(receiverId))
            )
        return FeedbackResponse.from(response.feedback)
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(value = OK)
    fun retrieveUserFeedbacks(@PathVariable receiverId: String): List<FeedbackResponse> {
        val response =
            commandBus.publish<RetrieveFeedbacksCommand, RetrieveFeedbacksCommandResponse>(
                RetrieveFeedbacksCommand(UUID.fromString(receiverId))
            )
        return response.feedbacks.map { FeedbackResponse.from(it) }
    }
}

data class FeedbackCreationRequest(
    @JsonProperty("reviewer_id")
    val reviewerId: UUID,
    @JsonProperty("rating")
    val rating: Int,
    @JsonProperty("comment")
    val comment: String,
    @JsonProperty("trip_id")
    val tripId: String
) {
    fun toDto(receiverId: String): FeedbackDTO {
        return FeedbackDTO(
            reviewerId = reviewerId,
            comment = comment,
            rating = rating,
            receiverId = UUID.fromString(receiverId),
            tripId = UUID.fromString(tripId)
        )
    }
}

data class FeedbackResponse(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("reviewer_id")
    val reviewerId: UUID,
    @JsonProperty("receiver_id")
    val receiverId: UUID,
    @JsonProperty("trip_id")
    val tripId: UUID,
    @JsonProperty("comment")
    val comment: String,
    @JsonProperty("rating")
    val rating: Int
) {
    companion object {
        fun from(feedback: FeedbackDTO): FeedbackResponse {
            return FeedbackResponse(
                id = feedback.id,
                reviewerId = feedback.reviewerId,
                comment = feedback.comment,
                rating = feedback.rating,
                receiverId = feedback.receiverId,
                tripId = feedback.tripId
            )
        }
    }
}