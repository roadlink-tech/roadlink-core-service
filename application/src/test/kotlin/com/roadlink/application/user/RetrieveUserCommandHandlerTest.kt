package com.roadlink.application.user

import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.util.UUID

class RetrieveUserCommandHandlerTest : BehaviorSpec({


    Given("a command handler") {
        val userRepository: RepositoryPort<User, UserCriteria> = mockk()
        val handler = RetrieveUserCommandHandler(userRepository)

        afterEach {
            clearMocks(userRepository)
        }

        When("find an existing user") {
            val userId = UUID.randomUUID()
            val user = UserFactory.common(
                id = userId,
                firstName = "jorge",
                lastName = "cabrera",
                email = "cabrerajjorge@gmail.com"
            )
            every { userRepository.findOrFail(match { it.id == userId }) } returns user

            val response = handler.handle(RetrieveUserCommand(userId = userId))
            Then("the response must be the expected") {
                response.user.id.shouldBe(userId)
                response.user.firstName.shouldBe("jorge")
                response.user.lastName.shouldBe("cabrera")
                response.user.email.shouldBe("cabrerajjorge@gmail.com")
                verify(exactly = 1) { userRepository.findOrFail(any()) }
            }
        }

        When("find an un-existing user") {
            val userId = UUID.randomUUID()
            every { userRepository.findOrFail(match { it.id == userId }) } throws DynamoDbException.EntityDoesNotExist(
                userId.toString()
            )
            val response = shouldThrow<DynamoDbException.EntityDoesNotExist> {
                handler.handle(RetrieveUserCommand(userId = userId))
            }

            Then("the response must be the expected") {
                response.message.shouldBe("Entity $userId does not exist")
                verify(exactly = 1) { userRepository.findOrFail(any()) }
            }
        }
    }
})
