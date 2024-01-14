package com.roadlink.application.user

import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.*

class CreateUserCommandHandlerTest : BehaviorSpec({

    Given("a UserCreationCommandHandler") {
        val userRepository: RepositoryPort<User, UserCriteria> = mockk()
        val handler = UserCreationCommandHandler(userRepository)

        When("receives a command and the user could be saved successfully") {
            val userId = UUID.randomUUID()
            val email = "cabrerajjorge@gmail.com"
            val command = UserCreationCommand(
                user = UserDTO(
                    id = userId,
                    email = email,
                    firstName = "jorge",
                    lastName = "cabrera"
                )
            )
            every { userRepository.save(match { it.id == userId }) } returns User(
                id = userId,
                email = email,
                firstName = "jorge",
                lastName = "cabrera"
            )

            val response = handler.handle(command)

            Then("the response must not be null") {
                response.user shouldBe UserDTO(id = userId, email = email, firstName = "jorge", lastName = "cabrera")
            }
        }
    }
})
