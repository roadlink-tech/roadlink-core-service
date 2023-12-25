package com.roadlink.core.api.user.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.user.*
import org.socialsignin.spring.data.dynamodb.repository.Query
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/users")
class UserCreationController(private val commandBus: CommandBus) {

    @PostMapping
    @ResponseBody
    @ResponseStatus(value = CREATED)
    fun create(@RequestBody user: UserCreationRequest): UserResponse {
        val response =
            commandBus.publish<UserCreationCommand, UserCreationCommandResponse>(UserCreationCommand(user.toDto()))
        return UserResponse.from(response.user)
    }

    @GetMapping("/{userId}")
    @ResponseBody
    @ResponseStatus(value = OK)
    fun retrieve(@PathVariable("userId") userId: String): UserResponse {
        val response = commandBus.publish<RetrieveUserCommand, RetrieveUserCommandResponse>(RetrieveUserCommand(userId))
        return UserResponse.from(response.user)
    }

    @GetMapping("/search")
    @ResponseBody
    @ResponseStatus(value = OK)
    fun search(@RequestParam("email") email: String): UserResponse {
        val response = commandBus.publish<SearchUserCommand, SearchUserCommandResponse>(SearchUserCommand(email))
        return UserResponse.from(response.user)
    }
}

data class UserCreationRequest(
    @JsonProperty("email")
    val email: String,
    @JsonProperty("first_name")
    val firstName: String,
    @JsonProperty("last_name")
    val lastName: String
) {
    fun toDto(): UserDTO {
        return UserDTO(email = email, firstName = firstName, lastName = lastName)
    }
}

data class UserResponse(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("email")
    val email: String,
    @JsonProperty("first_name")
    val firstName: String,
    @JsonProperty("last_name")
    val lastName: String
) {
    companion object {
        fun from(user: UserDTO): UserResponse {
            return UserResponse(
                id = user.id,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName
            )
        }
    }
}