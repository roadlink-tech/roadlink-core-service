package com.roadlink.core.api.friend.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.feedback.*
import com.roadlink.application.friend.FriendshipSolicitudeCreationCommand
import com.roadlink.application.friend.FriendshipSolicitudeCreationCommandResponse
import com.roadlink.application.friend.FriendshipSolicitudeDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/users/{userId}")
class RestFriendController(private val commandBus: CommandBus) {

    @PostMapping("/friendship_solicitude")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createFriendshipSolicitud(
        @PathVariable("userId") addressedId: String,
        @RequestBody request: FriendshipSolicitudeCreationRequest
    ): FriendshipSolicitudeResponse {
        val response =
            commandBus.publish<FriendshipSolicitudeCreationCommand, FriendshipSolicitudeCreationCommandResponse>(
                FriendshipSolicitudeCreationCommand(request.toDto(addressedId))
            )
        return FriendshipSolicitudeResponse.from(response.friendshipSolicitude)
    }

//    @GetMapping
//    @ResponseBody
//    @ResponseStatus(value = HttpStatus.OK)
//    fun retrieveUserFeedbacks(@PathVariable receiverId: String): List<FeedbackResponse> {
//        val response =
//            commandBus.publish<RetrieveFeedbacksCommand, RetrieveFeedbacksCommandResponse>(
//                RetrieveFeedbacksCommand(receiverId)
//            )
//        return response.feedbacks.map { FeedbackResponse.from(it) }
//    }
}

data class FriendshipSolicitudeResponse(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("addressed_Id")
    val addressedId: UUID,
    @JsonProperty("requester_id")
    val requesterId: UUID,
    @JsonProperty("status")
    val status: String
) {
    companion object {
        fun from(dto: FriendshipSolicitudeDTO): FriendshipSolicitudeResponse {
            return FriendshipSolicitudeResponse(
                id = dto.id,
                addressedId = dto.addressedId,
                requesterId = dto.requesterId,
                status = dto.status.toString()
            )
        }
    }
}


data class FriendshipSolicitudeCreationRequest(
    @JsonProperty("requester_id")
    val requesterId: UUID
) {
    fun toDto(addressedId: String): FriendshipSolicitudeDTO {
        return FriendshipSolicitudeDTO(
            requesterId = this.requesterId,
            addressedId = UUID.fromString(addressedId),
        )
    }
}