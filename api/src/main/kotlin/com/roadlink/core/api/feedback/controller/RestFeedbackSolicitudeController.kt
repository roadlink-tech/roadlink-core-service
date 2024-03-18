package com.roadlink.core.api.feedback.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.feedback.solicitude.*
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/users/{userId}/feedback_solicitudes")
class FeedbackSolicitudeController(private val commandBus: CommandBus) {
    @Deprecated("It might not be here because this pending feedbacks must be created automatically when a trip finish")
    @PostMapping
    @ResponseBody
    @ResponseStatus(value = HttpStatus.CREATED)
    fun create(
        @PathVariable("userId") receiverId: String,
        @RequestBody request: FeedbackSolicitudeCreationRequest
    ): FeedbackSolicitudeResponse {
        val response =
            commandBus.publish<CreateFeedbackSolicitudeCommand, CreateFeedbackSolicitudeCommandResponse>(
                CreateFeedbackSolicitudeCommand(request.toDto(receiverId))
            )
        return FeedbackSolicitudeResponse.from(response.solicitude)
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    fun list(
        @PathVariable("userId") reviewerId: String,
        @RequestParam("status", required = false) status: String? = null,
    ): List<FeedbackSolicitudeResponse> {
        val response =
            commandBus.publish<ListFeedbackSolicitudesCommand, ListFeedbackSolicitudesCommandResponse>(
                ListFeedbackSolicitudesCommand(
                    FeedbackSolicitudeListFilter(
                        reviewerId = UUID.fromString(reviewerId),
                        status = status?.takeIf { it.isNotBlank() }?.let { FeedbackSolicitude.Status.valueOf(it) }
                    )
                )
            )
        return response.solicitudes.map { FeedbackSolicitudeResponse.from(it) }
    }

    @PutMapping("/{feedbackSolicitudeId}/complete")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    fun complete(
        @PathVariable("userId") reviewerId: String,
        @PathVariable("feedbackSolicitudeId") feedbackSolicitudeId: String,
        @RequestBody body: FeedbackSolicitudeCompletionRequest
    ): FeedbackSolicitudeResponse {
        val response =
            commandBus.publish<CompleteFeedbackSolicitudeCommand, CompleteFeedbackSolicitudeCommandResponse>(
                CompleteFeedbackSolicitudeCommand(
                    FeedbackSolicitudeCompletion(
                        reviewerId = UUID.fromString(reviewerId),
                        feedbackSolicitudeId = UUID.fromString(feedbackSolicitudeId),
                        comment = body.comment,
                        rating = body.rating
                    )
                )
            )
        return FeedbackSolicitudeResponse.from(response.solicitude)
    }

}

data class FeedbackSolicitudeCreationRequest(
    @JsonProperty("reviewer_id")
    val reviewerId: String,
    @JsonProperty("trip_leg_id")
    val tripLegId: String
) {
    fun toDto(receiverId: String): FeedbackSolicitudeDTO {
        return FeedbackSolicitudeDTO(
            reviewerId = UUID.fromString(reviewerId),
            receiverId = UUID.fromString(receiverId),
            tripLegId = UUID.fromString(tripLegId)
        )
    }
}

data class FeedbackSolicitudeCompletionRequest(
    @JsonProperty("comment")
    val comment: String,
    @JsonProperty("rating")
    val rating: Int
)

data class FeedbackSolicitudeResponse(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("reviewer_id")
    val reviewerId: UUID,
    @JsonProperty("receiver_id")
    val receiverId: UUID,
    @JsonProperty("trip_leg_id")
    val tripLegId: UUID,
    @JsonProperty("status")
    val status: String? = null,
) {
    companion object {
        fun from(feedback: FeedbackSolicitudeDTO): FeedbackSolicitudeResponse {
            return FeedbackSolicitudeResponse(
                id = feedback.id,
                reviewerId = feedback.reviewerId,
                receiverId = feedback.receiverId,
                status = feedback.status?.toString(),
                tripLegId = feedback.tripLegId
            )
        }
    }
}