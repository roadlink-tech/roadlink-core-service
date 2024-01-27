package com.roadlink.application.user

import com.roadlink.core.domain.IdGenerator
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.*
import com.roadlink.core.domain.user.google.GoogleIdTokenValidator
import com.roadlink.core.domain.user.google.GoogleUser
import com.roadlink.core.domain.user.google.GoogleUserCriteria
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class GoogleSignInOrSignUpCommandTest : BehaviorSpec({

    val googleIdTokenValidator: GoogleIdTokenValidator = mockk()
    val userRepository: RepositoryPort<User, UserCriteria> = mockk()
    val googleUserRepository: RepositoryPort<GoogleUser, GoogleUserCriteria> = mockk()
    val idGenerator: IdGenerator = mockk()
    val jwtGenerator: JwtGenerator = mockk()

    val handler = GoogleLoginCommandHandler(
        googleIdTokenValidator = googleIdTokenValidator,
        userRepository = userRepository,
        googleUserRepository = googleUserRepository,
        idGenerator = idGenerator,
        jwtGenerator = jwtGenerator,
    )

    val googleIdToken = GoogleIdTokenFactory.common()
    val command = GoogleLoginCommand(googleIdToken = googleIdToken)
    val googleId = "109097944437190043577"
    val googleIdTokenPayload = GoogleIdTokenPayloadFactory.common(googleId = googleId)
    val userId = UUID.fromString("7a54ae3a-13f2-4280-8a40-c61bc3f283ed")
    val user = UserFactory.common(id = userId)
    val googleUser = GoogleUser(googleId = googleId, userId = userId)
    val jwt = JwtFactory.common()

    fun givenGoogleIdTokenIsValid() {
        every {
            googleIdTokenValidator.validate(googleIdTokenString = googleIdToken)
        } returns GoogleIdTokenValidator.Result.ValidToken(payload = googleIdTokenPayload)
    }

    fun givenGoogleIdTokenIsInvalid() {
        every {
            googleIdTokenValidator.validate(googleIdTokenString = googleIdToken)
        } returns GoogleIdTokenValidator.Result.InvalidToken
    }

    fun givenExistsUserForThatGoogleAccount() {
        every {
            googleUserRepository.findOrNull(GoogleUserCriteria(googleId = googleId))
        } returns googleUser
        every {
            userRepository.findOrFail(UserCriteria(id = userId))
        } returns user
    }

    fun givenNotExistsUserForThatGoogleAccount() {
        every {
            googleUserRepository.findOrNull(GoogleUserCriteria(googleId = googleId))
        } returns null
    }

    fun generateJwt() {
        every {
            jwtGenerator.generate(userId = user.id)
        } returns jwt
    }

    fun givenUserIsSavedOk() {
        every {
            userRepository.save(match { it.id == userId })
        } returns user
    }

    fun givenGoogleUserIsSavedOk() {
        every {
            googleUserRepository.save(googleUser)
        } returns googleUser
    }

    fun generateNextId(id: UUID) {
        every { idGenerator.next() } returns id
    }

    afterEach { clearAllMocks() }

    Given("a GoogleSignInOrSignUpCommandHandler") {

        When("the google id token is invalid") {
            givenGoogleIdTokenIsInvalid()

            val response = handler.handle(command)

            Then("the response should be invalid google id token") {
                response shouldBe GoogleLoginCommandResponse.InvalidGoogleIdToken
                verify(exactly = 1) { googleIdTokenValidator.validate(any()) }
                verify(exactly = 0) { userRepository.save(any()) }
                verify(exactly = 0) { googleUserRepository.save(any()) }
                verify(exactly = 0) { jwtGenerator.generate(any()) }
            }
        }

        When("the google id token is valid and exists user for that google account") {
            givenGoogleIdTokenIsValid()
            givenExistsUserForThatGoogleAccount()
            generateJwt()

            val response = handler.handle(command)

            Then("the response should be ok") {
                response shouldBe GoogleLoginCommandResponse.Ok(user = UserDTO.from(user), jwt = jwt)
                verify(exactly = 1) { googleIdTokenValidator.validate(any()) }
                verify(exactly = 0) { userRepository.save(any()) }
                verify(exactly = 0) { googleUserRepository.save(any()) }
                verify(exactly = 1) { jwtGenerator.generate(any()) }
            }
        }

        When("the google id token is valid and not exists user for that google account") {
            givenGoogleIdTokenIsValid()
            givenNotExistsUserForThatGoogleAccount()
            generateNextId(id = userId)
            givenUserIsSavedOk()
            givenGoogleUserIsSavedOk()
            generateJwt()

            val response = handler.handle(command)

            Then("the response should be ok") {
                response shouldBe GoogleLoginCommandResponse.Ok(user = UserDTO.from(user), jwt = jwt)
                verify(exactly = 1) { googleIdTokenValidator.validate(any()) }
                verify(exactly = 1) { userRepository.save(any()) }
                verify(exactly = 1) { googleUserRepository.save(any()) }
                verify(exactly = 1) { jwtGenerator.generate(any()) }
            }
        }

    }
})