package com.roadlink.application.friend

import com.roadlink.application.user.UserFactory
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class DeleteFriendCommandHandlerTest : BehaviorSpec({
    val userRepository: RepositoryPort<User, UserCriteria> = mockk()

    afterEach {
        clearMocks(userRepository)
    }
    val userId = UUID.randomUUID()
    val friendId = UUID.randomUUID()

    Given("a delete friend command handler") {
        val handler = DeleteFriendCommandHandler(userRepository)

        When("handle a command with a user which does not exist") {
            every { userRepository.findOrFail(match { it.id == userId }) } throws DynamoDbException.EntityDoesNotExist(
                ""
            )

            val exception = shouldThrow<DynamoDbException.EntityDoesNotExist> {
                handler.handle(DeleteFriendCommand(FriendDeletionRequest(userId = userId, friendId = friendId)))
            }

            Then("the exception should not be null") {
                exception.shouldNotBeNull()
                verify(exactly = 1) { userRepository.findOrFail(any()) }
            }
        }

        When("handle a command with a friend which does not exist") {
            every { userRepository.findOrFail(match { it.id == userId }) } returns UserFactory.common(id = userId)
            every { userRepository.findOrFail(match { it.id == friendId }) } throws DynamoDbException.EntityDoesNotExist(
                ""
            )

            val exception = shouldThrow<DynamoDbException.EntityDoesNotExist> {
                handler.handle(DeleteFriendCommand(FriendDeletionRequest(userId = userId, friendId = friendId)))
            }

            Then("the exception should not be null") {
                exception.shouldNotBeNull()
                verify(exactly = 2) { userRepository.findOrFail(any()) }
            }
        }

        When("handle a command and can delete friends successfully") {
            val george = UserFactory.common(id = userId)
            val martin = UserFactory.common(id = friendId)
            every { userRepository.findOrFail(match { it.id == userId }) } returns george
            every { userRepository.findOrFail(match { it.id == friendId }) } returns martin
            every { userRepository.saveAll(any()) } returns listOf(george, martin)

            george.beFriendOf(martin)

            val response =
                handler.handle(DeleteFriendCommand(FriendDeletionRequest(userId = userId, friendId = friendId)))

            Then("the friend should be removed") {
                verify(exactly = 2) { userRepository.findOrFail(any()) }
                verify(exactly = 1) { userRepository.saveAll(any()) }
                response.friends.shouldBeEmpty()
            }
        }

        When("handle a command and can delete a friend successfully") {
            val george = UserFactory.common(id = userId)
            val martin = UserFactory.common(id = friendId)
            val felix = UserFactory.common()
            every { userRepository.findOrFail(match { it.id == userId }) } returns george
            every { userRepository.findOrFail(match { it.id == friendId }) } returns martin
            every { userRepository.saveAll(any()) } returns listOf(george, martin)

            george.beFriendOf(felix)
            george.beFriendOf(martin)

            val response =
                handler.handle(DeleteFriendCommand(FriendDeletionRequest(userId = userId, friendId = friendId)))

            Then("the friend should be removed") {
                verify(exactly = 2) { userRepository.findOrFail(any()) }
                verify(exactly = 1) { userRepository.saveAll(any()) }
                response.friends.size.shouldBe(1)
            }
        }

        When("handle a command but the users are not friend") {
            val george = UserFactory.common(id = userId)
            val martin = UserFactory.common(id = friendId)
            val felix = UserFactory.common()
            every { userRepository.findOrFail(match { it.id == userId }) } returns george
            every { userRepository.findOrFail(match { it.id == friendId }) } returns martin
            every { userRepository.saveAll(any()) } returns listOf(george, martin)

            george.beFriendOf(felix)

            val response =
                handler.handle(DeleteFriendCommand(FriendDeletionRequest(userId = userId, friendId = friendId)))

            Then("none friend should be removed") {
                verify(exactly = 2) { userRepository.findOrFail(any()) }
                verify(exactly = 1) { userRepository.saveAll(any()) }
                response.friends.size.shouldBe(1)
            }
        }
    }
})
