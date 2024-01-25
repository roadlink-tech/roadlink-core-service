package com.roadlink.core.api.friend.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.feedback.*
import com.roadlink.application.friend.*
import com.roadlink.core.domain.friend.FriendshipSolicitude.*
import jakarta.websocket.server.PathParam
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/users/{userId}/friendship_solicitudes")
class RestFriendshipSolicitudesController(private val commandBus: CommandBus) {

    @PostMapping
    @ResponseBody
    @ResponseStatus(value = CREATED)
    fun create(
        @PathVariable("userId") addressedId: String,
        @RequestBody request: FriendshipSolicitudeCreationRequest
    ): FriendshipSolicitudeResponse {
        val response =
            commandBus.publish<CreateFriendshipSolicitudeCommand, CreateFriendshipSolicitudeCommandResponse>(
                CreateFriendshipSolicitudeCommand(request.toDto(addressedId))
            )
        return FriendshipSolicitudeResponse.from(response.friendshipSolicitude)
    }

    @PutMapping("/{friendshipSolicitudeId}/accept")
    @ResponseBody
    @ResponseStatus(value = OK)
    fun accept(
        @PathVariable("userId") addressedId: String,
        @PathVariable("friendshipSolicitudeId") friendshipSolicitudeId: String,
    ): FriendshipSolicitudeResponse {
        val response =
            commandBus.publish<AcceptFriendshipSolicitudeCommand, AcceptFriendshipSolicitudeCommandResponse>(
                AcceptFriendshipSolicitudeCommand(
                    FriendshipSolicitudeDecisionDTO(
                        id = UUID.fromString(friendshipSolicitudeId),
                        addressedId = UUID.fromString(addressedId),
                        status = Status.ACCEPTED
                    )
                )
            )
        return FriendshipSolicitudeResponse.from(response.friendshipSolicitude)
    }

    @PutMapping("/{friendshipSolicitudeId}/reject")
    @ResponseBody
    @ResponseStatus(value = OK)
    fun reject(
        @PathVariable("userId") addressedId: String,
        @PathVariable("friendshipSolicitudeId") friendshipSolicitudeId: String,
    ): FriendshipSolicitudeResponse {
        val response =
            commandBus.publish<RejectFriendshipSolicitudeCommand, RejectFriendshipSolicitudeCommandResponse>(
                RejectFriendshipSolicitudeCommand(
                    FriendshipSolicitudeDecisionDTO(
                        id = UUID.fromString(friendshipSolicitudeId),
                        addressedId = UUID.fromString(addressedId),
                        status = Status.REJECTED
                    )
                )
            )
        return FriendshipSolicitudeResponse.from(response.friendshipSolicitude)
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(value = OK)
    fun list(
        @PathVariable("userId") addressedId: String,
        @PathParam("status") status: String? = null,
    ): List<FriendshipSolicitudeResponse> {
        val response =
            commandBus.publish<ListFriendshipSolicitudesCommand, ListFriendshipSolicitudesCommandResponse>(
                ListFriendshipSolicitudesCommand(
                    FriendshipSolicitudeListFilter(
                        addressedId = UUID.fromString(addressedId),
                        status = status
                    )
                )
            )

        return response.friendshipSolicitudes.map { FriendshipSolicitudeResponse.from(it) }
    }
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
    @field:NotBlank(message = "Requester id cannot be blank")
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