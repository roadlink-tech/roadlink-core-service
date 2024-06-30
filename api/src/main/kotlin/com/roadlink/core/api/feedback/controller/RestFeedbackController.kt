package com.roadlink.core.api.feedback.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.feedback.CreateFeedbackCommand
import com.roadlink.application.feedback.CreateFeedbackCommandResponse
import com.roadlink.application.feedback.FeedbackDTO
import com.roadlink.application.feedback.ListFeedbacksCommand
import com.roadlink.application.feedback.ListFeedbacksCommandResponse
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
@RequestMapping("/core-service/users/{userId}/feedbacks")
class FeedbackController(private val commandBus: CommandBus) {

    @Deprecated("This endpoint must no be necessary because the feedbacks are create once a feedback solicitude is completed")
    @PostMapping
    @ResponseBody
    @ResponseStatus(value = CREATED)
    fun create(
        @PathVariable("userId") receiverId: String,
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
    fun list(@PathVariable("userId") receiverId: String): List<FeedbackResponse> {
        val response =
            commandBus.publish<ListFeedbacksCommand, ListFeedbacksCommandResponse>(
                ListFeedbacksCommand(UUID.fromString(receiverId))
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
    @JsonProperty("trip_leg_id")
    val tripLegId: String
) {
    fun toDto(receiverId: String): FeedbackDTO {
        return FeedbackDTO(
            reviewerId = reviewerId,
            comment = comment,
            rating = rating,
            receiverId = UUID.fromString(receiverId),
            tripLegId = UUID.fromString(tripLegId)
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
    @JsonProperty("trip_leg_id")
    val tripLegId: UUID,
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
                tripLegId = feedback.tripLegId
            )
        }
    }
}