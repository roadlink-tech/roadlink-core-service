package com.roadlink.core.api.friend.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.friend.ListFriendsCommand
import com.roadlink.application.friend.ListFriendsCommandResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/users/{userId}/friends")
class RestFriendsController(private val commandBus: CommandBus) {

    @GetMapping
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    fun list(
        @PathVariable("userId") userId: String,
    ): FriendsResponse {
        val response =
            commandBus.publish<ListFriendsCommand, ListFriendsCommandResponse>(
                ListFriendsCommand(
                    userId = UUID.fromString(userId)
                )
            )
        return FriendsResponse(friends = response.friends)
    }
}


data class FriendsResponse(
    @JsonProperty("friends")
    val friends: Set<UUID>,
)