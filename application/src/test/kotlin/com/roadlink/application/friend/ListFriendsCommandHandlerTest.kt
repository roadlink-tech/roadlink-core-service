package com.roadlink.application.friend

import com.roadlink.application.user.UserFactory
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

class ListFriendsCommandHandlerTest : BehaviorSpec({
    val userRepository: RepositoryPort<User, UserCriteria> = mockk()

    val handler = ListFriendsCommandHandler(userRepository)
    val userId = UUID.randomUUID()

    afterEach {
        clearMocks(userRepository)
    }

    Given("a list friends command handler") {

        When("the user does not have any friend") {
            val user = UserFactory.common(id = userId)
            every { userRepository.findOrFail(match { it.id == user.id }) } returns user

            val response = handler.handle(ListFriendsCommand(userId = userId))

            Then("the friend list must be empty") {
                response.friends.shouldBeEmpty()
            }
        }

        When("the user has too many friends") {
            val user = UserFactory.withTooManyFriends(id = userId)
            every { userRepository.findOrFail(match { it.id == user.id }) } returns user

            val response = handler.handle(ListFriendsCommand(userId = userId))

            Then("the friend list must be empty") {
                response.friends.size.shouldBe(100)
            }
        }

        When("the user does not exist") {
            val user = UserFactory.common(id = userId)
            every { userRepository.findOrFail(match { it.id == user.id }) } throws DynamoDbException.EntityDoesNotExist(
                userId.toString()
            )

            val exception = shouldThrow<DynamoDbException.EntityDoesNotExist> {
                handler.handle(ListFriendsCommand(userId = userId))
            }

            Then("the exception should not be empty") {
                exception.message.shouldBe("Entity $userId does not exist")
            }
        }
    }
})
