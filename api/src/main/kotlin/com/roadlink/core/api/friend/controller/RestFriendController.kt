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

@RestController
@RequestMapping("/users/{userId}/friendship_solicitudes")
class RestFriendController(private val commandBus: CommandBus) {

    @PostMapping
    @ResponseBody
    @ResponseStatus(value = CREATED)
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

    @PutMapping("/{friendshipSolicitudeId}/accept")
    @ResponseBody
    @ResponseStatus(value = OK)
    fun acceptFriendshipSolicitude(
        @PathVariable("userId") addressedId: String,
        @PathVariable("friendshipSolicitudeId") friendshipSolicitudeId: String,
    ): FriendshipSolicitudeResponse {
        val response =
            commandBus.publish<FriendshipSolicitudeAcceptanceCommand, FriendshipSolicitudeAcceptanceCommandResponse>(
                FriendshipSolicitudeAcceptanceCommand(
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
    fun rejectFriendshipSolicitude(
        @PathVariable("userId") addressedId: String,
        @PathVariable("friendshipSolicitudeId") friendshipSolicitudeId: String,
    ): FriendshipSolicitudeResponse {
        val response =
            commandBus.publish<FriendshipSolicitudeRejectionCommand, FriendshipSolicitudeRejectionCommandResponse>(
                FriendshipSolicitudeRejectionCommand(
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
    fun listFriendshipSolicitudes(
        @PathVariable("userId") addressedId: String,
        @PathParam("status") status: String? = null,
    ): List<FriendshipSolicitudeResponse> {
        val response =
            commandBus.publish<FriendshipSolicitudeListCommand, FriendshipSolicitudeListCommandResponse>(
                FriendshipSolicitudeListCommand(
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