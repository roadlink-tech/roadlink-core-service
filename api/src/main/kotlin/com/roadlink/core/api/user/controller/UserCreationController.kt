package com.roadlink.core.api.user.controller

import com.roadlink.application.command.CommandBus
import com.roadlink.application.user.UserCreationCommand
import com.roadlink.application.user.UserCreationCommandResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class UserCreationController(private val commandBus: CommandBus) {

    @PostMapping("/users")
    fun createUser(@RequestBody user: UserCreationBody): ResponseEntity<UserCreationResponse> {
        val response =
            commandBus.publish<UserCreationCommand, UserCreationCommandResponse>(UserCreationCommand())
        return ResponseEntity<UserCreationResponse>(HttpStatus.CREATED)
    }
}

data class UserCreationBody(
    val email: String
)

data class UserCreationResponse(
    val message: String = "The user was created successfully"
)