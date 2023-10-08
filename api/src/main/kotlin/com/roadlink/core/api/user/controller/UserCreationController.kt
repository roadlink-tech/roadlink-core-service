package com.roadlink.core.api.user.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.user.UserCreationCommand
import com.roadlink.application.user.UserCreationCommandResponse
import com.roadlink.application.user.UserDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class UserCreationController(private val commandBus: CommandBus) {

    @PostMapping("/users")
    fun createUser(@RequestBody user: UserCreationBody): ResponseEntity<UserCreationResponse> {
        val response =
            commandBus.publish<UserCreationCommand, UserCreationCommandResponse>(UserCreationCommand(user.toDto()))
        return ResponseEntity<UserCreationResponse>(HttpStatus.CREATED)
    }
}

data class UserCreationBody(
    @JsonProperty("email")
    val email: String
) {
    fun toDto(): UserDTO {
        return UserDTO(email = email)
    }
}

data class UserCreationResponse(
    val message: String = "The user was created successfully"
)