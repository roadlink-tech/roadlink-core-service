package com.roadlink.application.user

import com.roadlink.core.infrastructure.ApplicationDateTime
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.UserException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class CreateUserCommandHandlerTest : BehaviorSpec({

    Given("a UserCreationCommandHandler") {
        val userRepository: RepositoryPort<User, UserCriteria> = mockk()
        val handler = CreateUserCommandHandler(userRepository)
        afterEach {
            clearMocks(userRepository)
        }

        When("receives a command and the user could be saved successfully") {
            val userId = UUID.randomUUID()
            val email = "cabrerajjorge@gmail.com"
            val command = CreateUserCommand(
                user = UserDTO(
                    id = userId,
                    email = email,
                    firstName = "jorge",
                    lastName = "cabrera",
                    birthDay = "06/12/1991",
                    profilePhotoUrl = "https://lh3.googleusercontent.com/a/ACg8ocJW5g-yavaNzKPZcF-U8-W5zGfIQdww2mOcyDq_48xfdHE=s96-c",
                    gender = "male"

                )
            )
            every { userRepository.save(match { it.id == userId }) } returns User(
                id = userId,
                email = email,
                firstName = "jorge",
                lastName = "cabrera",
                birthDay = ApplicationDateTime.from("06/12/1991"),
                profilePhotoUrl = "https://lh3.googleusercontent.com/a/ACg8ocJW5g-yavaNzKPZcF-U8-W5zGfIQdww2mOcyDq_48xfdHE=s96-c",
                gender = "male"

            )

            every { userRepository.findAll(match { it.email == email }) } returns emptyList()

            val response = handler.handle(command)

            Then("the response must not be null") {
                response.user shouldBe UserDTO(
                    id = userId,
                    email = email,
                    firstName = "jorge",
                    lastName = "cabrera",
                    birthDay = "06/12/1991",
                    profilePhotoUrl = "https://lh3.googleusercontent.com/a/ACg8ocJW5g-yavaNzKPZcF-U8-W5zGfIQdww2mOcyDq_48xfdHE=s96-c",
                    gender = "male"
                )
                verify(exactly = 1) { userRepository.findAll(any()) }
                verify(exactly = 1) { userRepository.save(any()) }
            }
        }

        When("the user email is already registered") {
            val userId = UUID.randomUUID()
            val email = "cabrerajjorge@gmail.com"
            every { userRepository.findAll(match { it.email == email }) } returns listOf(
                UserFactory.common(
                    id = userId,
                    email = email
                )
            )

            val command = CreateUserCommand(
                user = UserDTO(
                    id = userId,
                    email = email,
                    firstName = "jorge",
                    lastName = "cabrera",
                    birthDay = "06/12/1991",
                    profilePhotoUrl = "https://profile.photo.com",
                    gender = "male"

                )
            )

            val response = shouldThrow<UserException.UserEmailAlreadyRegistered> { handler.handle(command) }

            Then("the response must not be null") {
                response.message.shouldBe("User cabrerajjorge@gmail.com is already registered")
            }
        }
    }
})
