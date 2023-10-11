package com.roadlink.core.api.user.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.user.UserCreationCommand
import com.roadlink.application.user.UserCreationCommandResponse
import com.roadlink.application.user.UserDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping
class UserCreationController(private val commandBus: CommandBus) {

    @PostMapping("/users")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.CREATED)
    fun createUser(@RequestBody user: UserCreationRequest): UserCreationResponse {
        val response =
            commandBus.publish<UserCreationCommand, UserCreationCommandResponse>(UserCreationCommand(user.toDto()))
        return UserCreationResponse.from(response.user)
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

data class UserCreationResponse(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("email")
    val email: String
) {
    companion object {
        fun from(user: UserDTO): UserCreationResponse {
            return UserCreationResponse(
                id = user.id,
                email = user.email
            )
        }
    }
}