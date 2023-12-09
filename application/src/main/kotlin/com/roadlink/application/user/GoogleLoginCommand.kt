package com.roadlink.application.user

import com.roadlink.core.domain.IdGenerator
import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.*
import com.roadlink.core.domain.user.google.GoogleIdTokenPayload
import com.roadlink.core.domain.user.google.GoogleIdTokenValidator
import com.roadlink.core.domain.user.google.GoogleUser
import com.roadlink.core.domain.user.google.GoogleUserCriteria

data class GoogleLoginCommand(val googleIdToken: String) : Command

sealed class GoogleLoginCommandResponse : CommandResponse {
    data object InvalidGoogleIdToken : GoogleLoginCommandResponse()
    data class Ok(val user: UserDTO, val jwt: String) : GoogleLoginCommandResponse()
}

class GoogleLoginCommandHandler(
    private val googleIdTokenValidator: GoogleIdTokenValidator,
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val googleUserRepository: RepositoryPort<GoogleUser, GoogleUserCriteria>,
    private val idGenerator: IdGenerator,
    private val jwtGenerator: JwtGenerator,
) : CommandHandler<GoogleLoginCommand, GoogleLoginCommandResponse> {

    override fun handle(command: GoogleLoginCommand): GoogleLoginCommandResponse {
        return googleIdTokenValidator.validate(command.googleIdToken).let { result -> when (result) {
            GoogleIdTokenValidator.Result.InvalidToken ->
                invalidGoogleIdToken()

            is GoogleIdTokenValidator.Result.ValidToken ->
                findGoogleUser(GoogleUserCriteria(googleId = result.payload.googleId))
                    ?.let { signIn(it) }
                    ?: signUp(result.payload)
        } }
    }

    private fun findGoogleUser(googleUserCriteria: GoogleUserCriteria): GoogleUser? =
        googleUserRepository.findOrNull(googleUserCriteria)

    private fun signIn(googleUser: GoogleUser): GoogleLoginCommandResponse =
        userRepository.findOrFail(UserCriteria(id = googleUser.userId))
            .let { user ->
                val jwt = jwtGenerator.generate(user.id)
                ok(user, jwt)
            }

    private fun signUp(payload: GoogleIdTokenPayload): GoogleLoginCommandResponse =
        userFrom(payload)
            .let { user -> userRepository.save(user) }
            .also { user ->
                val googleUser = GoogleUser(googleId = payload.googleId, userId = user.id)
                googleUserRepository.save(googleUser)
            }
            .let { user -> jwtGenerator.generate(user.id)
                .let { jwt -> ok(user, jwt) }
            }

    private fun userFrom(googleIdTokenPayload: GoogleIdTokenPayload): User =
        User.from(googleIdTokenPayload, idGenerator)

    private fun ok(user: User, jwt: String): GoogleLoginCommandResponse.Ok =
        GoogleLoginCommandResponse.Ok(UserDTO.from(user), jwt)

    private fun invalidGoogleIdToken(): GoogleLoginCommandResponse.InvalidGoogleIdToken =
        GoogleLoginCommandResponse.InvalidGoogleIdToken

}
