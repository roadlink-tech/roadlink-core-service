package com.roadlink.application.user

import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserRepositoryPort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.*

class UserCreationCommandHandlerTest : BehaviorSpec({

    Given("a UserCreationCommandHandler") {
        val userRepository: UserRepositoryPort = mockk()
        val handler = UserCreationCommandHandler(userRepository)

        When("receives a command and the user could be saved successfully") {
            val userId = UUID.randomUUID()
            val email = "cabrerajjorge@gmail.com"
            val command = UserCreationCommand(user = UserDTO(id = userId, email = email))
            every { userRepository.save(user = match { it.id == userId }) } returns User(
                id = userId,
                email = email
            )

            val response = handler.handle(command)

            Then("the response must not be null") {
                response.user shouldBe UserDTO(id = userId, email = email)
            }
        }
    }
})
