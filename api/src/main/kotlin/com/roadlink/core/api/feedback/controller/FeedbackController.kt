package com.roadlink.core.api.feedback.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.feedback.FeedbackCreationCommand
import com.roadlink.application.feedback.FeedbackCreationCommandResponse
import com.roadlink.application.feedback.FeedbackDTO
import org.springframework.http.HttpStatus
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
@RequestMapping

class FeedbackController(private val commandBus: CommandBus) {


    @PostMapping("/users/{receiverId}/feedbacks")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createFeedback(
        @PathVariable receiverId: String,
        @RequestBody request: FeedbackCreationRequest
    ): FeedbackCreationResponse {
        val response =
            commandBus.publish<FeedbackCreationCommand, FeedbackCreationCommandResponse>(
                FeedbackCreationCommand(request.toDto(receiverId))
            )
        return FeedbackCreationResponse.from(response.feedback)
    }

    @GetMapping("/users/{receiverId}/feedbacks/{feedbackId}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    fun retrieveFeedback(
        @PathVariable receiverId: String,
        @PathVariable feedbackId: String,

        ) {
    }
}

data class FeedbackCreationRequest(
    @JsonProperty("reviewer_id")
    val reviewerId: UUID,
    @JsonProperty("rating")
    val rating: Int,
    @JsonProperty("comment")
    val comment: String
) {
    fun toDto(receiverId: String): FeedbackDTO {
        return FeedbackDTO(
            reviewerId = reviewerId,
            comment = comment,
            rating = rating,
            receiverId = UUID.fromString(receiverId)
        )
    }
}

data class FeedbackCreationResponse(
    @JsonProperty("id")
    val id: UUID,
) {
    companion object {
        fun from(feedback: FeedbackDTO): FeedbackCreationResponse {
            return FeedbackCreationResponse(
                id = feedback.id
            )
        }
    }
}

data class FeedbackResponse(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("reviewer_id")
    val reviewerId: UUID,
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
                rating = feedback.rating
            )
        }
    }
}