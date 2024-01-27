package com.roadlink.core.api.user.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.user.GoogleLoginCommand
import com.roadlink.application.user.GoogleLoginCommandResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping
class RestGoogleLoginController(private val commandBus: CommandBus) {

    @PostMapping("/google-login")
    @ResponseBody
    fun googleLogin(@RequestBody request: GoogleLoginRequest): ResponseEntity<*> {
        val response = commandBus.publish<GoogleLoginCommand, GoogleLoginCommandResponse>(
            GoogleLoginCommand(request.googleIdToken)
        )

        return when (response) {
            GoogleLoginCommandResponse.InvalidGoogleIdToken ->
                ResponseEntity.status(401).build<Unit>()
            is GoogleLoginCommandResponse.Ok ->
                ResponseEntity.ok(GoogleLoginResponse(user = UserResponse.from(response.user), jwt = response.jwt))
        }
    }
}

data class GoogleLoginRequest(
    @JsonProperty("google_id_token")
    val googleIdToken: String
)

data class GoogleLoginResponse(
    @JsonProperty("user")
    val user: UserResponse,
    @JsonProperty("jwt")
    val jwt: String
)
