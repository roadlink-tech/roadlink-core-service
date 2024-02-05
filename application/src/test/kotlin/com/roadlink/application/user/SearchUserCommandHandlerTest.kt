package com.roadlink.application.user

import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class SearchUserCommandHandlerTest : BehaviorSpec({

    Given("a command handler") {
        val userRepository: RepositoryPort<User, UserCriteria> = mockk()
        val handler = SearchUserCommandHandler(userRepository)
        afterEach {
            clearMocks(userRepository)
        }

        When("find an existing user by email") {
            val userId = UUID.randomUUID()
            val user = UserFactory.common(
                id = userId,
                firstName = "jorge",
                lastName = "cabrera",
                email = "cabrerajjorge@gmail.com"
            )
            every { userRepository.findOrFail(match { it.email == "cabrerajjorge@gmail.com" }) } returns user

            val response = handler.handle(SearchUserCommand(email = "cabrerajjorge@gmail.com"))

            Then("the response must be the expected") {
                response.user.id.shouldBe(userId)
                response.user.firstName.shouldBe("jorge")
                response.user.lastName.shouldBe("cabrera")
                response.user.email.shouldBe("cabrerajjorge@gmail.com")
                verify(exactly = 1) { userRepository.findOrFail(any()) }
            }
        }

        When("find an existing user by userName") {
            val userId = UUID.randomUUID()
            val user = UserFactory.common(
                id = userId,
                firstName = "jorge",
                lastName = "cabrera",
                email = "cabrerajjorge@gmail.com",
                userName = "jorgecabrera"
            )
            every { userRepository.findOrFail(match { it.userName == "jorgecabrera" }) } returns user

            val response = handler.handle(SearchUserCommand(userName = "jorgecabrera"))

            Then("the response must be the expected") {
                response.user.id.shouldBe(userId)
                response.user.firstName.shouldBe("jorge")
                response.user.lastName.shouldBe("cabrera")
                response.user.email.shouldBe("cabrerajjorge@gmail.com")
                response.user.userName.shouldBe("jorgecabrera")
                verify(exactly = 1) { userRepository.findOrFail(any()) }
            }
        }

        When("find an un-existing user") {
            every { userRepository.findOrFail(match { it.email == "cabrerajjorge@gmail.com" }) } throws DynamoDbException.EntityDoesNotExist(
                "cabrerajjorge@gmail.com"
            )
            val response = shouldThrow<DynamoDbException.EntityDoesNotExist> {
                handler.handle(SearchUserCommand(email = "cabrerajjorge@gmail.com"))
            }

            Then("the response must be the expected") {
                response.message.shouldBe("Entity cabrerajjorge@gmail.com does not exist")
                verify(exactly = 1) { userRepository.findOrFail(any()) }
            }
        }
    }

})
