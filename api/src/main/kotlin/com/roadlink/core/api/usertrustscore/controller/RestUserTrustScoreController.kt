package com.roadlink.core.api.usertrustscore.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.user.RetrieveUserCommand
import com.roadlink.application.user.RetrieveUserCommandResponse
import com.roadlink.core.api.user.controller.UserResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/{userId}/user_trust_score")
class RestUserTrustScoreController(private val commandBus: CommandBus) {

    @GetMapping
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    fun retrieve(@PathVariable("userId") userId: String): UserResponse {
        val response = commandBus.publish<RetrieveUserCommand, RetrieveUserCommandResponse>(
            RetrieveUserCommand(userId)
        )
        return UserResponse.from(response.user)
    }

}

data class UserTrustScoreResponse(
    @JsonProperty("score")
    val score: Float
)