package com.roadlink.application.friend

import com.roadlink.application.user.UserFactory
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitude.Status.*
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria
import com.roadlink.core.domain.friend.FriendshipSolicitudeException
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class RejectFriendshipSolicitudeCommandHandlerTest : BehaviorSpec({
    val userRepository: RepositoryPort<User, UserCriteria> = mockk()
    val friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria> = mockk()

    afterEach {
        clearMocks(userRepository, friendshipSolicitudeRepository)
    }
    val addressedId = UUID.randomUUID()
    val friendshipSolicitudeId = UUID.randomUUID()

    Given("an reject friendship solicitude command handler") {
        val handler = RejectFriendshipSolicitudeCommandHandler(userRepository, friendshipSolicitudeRepository)

        When("handle a command with a user which does not exist") {
            every { userRepository.findOrFail(match { it.id == addressedId }) } throws DynamoDbException.EntityDoesNotExist(
                ""
            )

            val exception = shouldThrow<DynamoDbException.EntityDoesNotExist> {
                handler.handle(
                    RejectFriendshipSolicitudeCommand(
                        FriendshipSolicitudeDecisionDTO(
                            id = UUID.randomUUID(),
                            addressedId = addressedId,
                            status = REJECTED
                        )
                    )
                )
            }

            Then("the exception must not be null") {
                exception.message.shouldBe("Entity  does not exist")
                verify(exactly = 1) { userRepository.findOrFail(any()) }
                verify(exactly = 0) { friendshipSolicitudeRepository.findOrFail(any()) }
                verify(exactly = 0) { friendshipSolicitudeRepository.save(any()) }
            }
        }

        When("handle a command with a friendship solicitude which does not exist") {
            every { userRepository.findOrFail(match { it.id == addressedId }) } returns UserFactory.common(id = addressedId)
            every { friendshipSolicitudeRepository.findOrFail(any()) } throws DynamoDbException.EntityDoesNotExist(
                ""
            )
            val exception = shouldThrow<DynamoDbException.EntityDoesNotExist> {
                handler.handle(
                    RejectFriendshipSolicitudeCommand(
                        FriendshipSolicitudeDecisionDTO(
                            id = UUID.randomUUID(),
                            addressedId = addressedId,
                            status = REJECTED
                        )
                    )
                )
            }

            Then("the exception must not be null") {
                exception.message.shouldBe("Entity  does not exist")
                verify(exactly = 1) { userRepository.findOrFail(any()) }
                verify(exactly = 1) { friendshipSolicitudeRepository.findOrFail(any()) }
                verify(exactly = 0) { friendshipSolicitudeRepository.save(any()) }
            }
        }

        When("handle a command with a friendship solicitude which has been already rejected") {
            every { userRepository.findOrFail(match { it.id == addressedId }) } returns UserFactory.common(id = addressedId)
            every { friendshipSolicitudeRepository.findOrFail(any()) } returns FriendshipSolicitudeFactory.common(
                id = friendshipSolicitudeId,
                solicitudeStatus = REJECTED
            )

            val exception = shouldThrow<FriendshipSolicitudeException.FriendshipSolicitudeStatusCanNotChange> {
                handler.handle(
                    RejectFriendshipSolicitudeCommand(
                        FriendshipSolicitudeDecisionDTO(
                            id = friendshipSolicitudeId,
                            addressedId = addressedId,
                            status = REJECTED
                        )
                    )
                )
            }

            Then("the exception must not be null") {
                exception.message.shouldBe("Friendship solicitude $friendshipSolicitudeId status can not change, because it has raised an immutable status REJECTED")
                verify(exactly = 1) { userRepository.findOrFail(any()) }
                verify(exactly = 1) { friendshipSolicitudeRepository.findOrFail(any()) }
                verify(exactly = 0) { friendshipSolicitudeRepository.save(any()) }
            }
        }

        When("handle a command with a friendship solicitude which has been already accepted") {
            every { userRepository.findOrFail(match { it.id == addressedId }) } returns UserFactory.common(id = addressedId)
            every { friendshipSolicitudeRepository.findOrFail(any()) } returns FriendshipSolicitudeFactory.common(
                id = friendshipSolicitudeId,
                solicitudeStatus = ACCEPTED
            )

            val exception = shouldThrow<FriendshipSolicitudeException.FriendshipSolicitudeStatusCanNotChange> {
                handler.handle(
                    RejectFriendshipSolicitudeCommand(
                        FriendshipSolicitudeDecisionDTO(
                            id = friendshipSolicitudeId,
                            addressedId = addressedId,
                            status = REJECTED
                        )
                    )
                )
            }

            Then("the exception must not be null") {
                exception.message.shouldBe("Friendship solicitude $friendshipSolicitudeId status can not change, because it has raised an immutable status ACCEPTED")
                verify(exactly = 1) { userRepository.findOrFail(any()) }
                verify(exactly = 1) { friendshipSolicitudeRepository.findOrFail(any()) }
                verify(exactly = 0) { friendshipSolicitudeRepository.save(any()) }
            }
        }

        When("handle a command with and reject a friendship solicitude successfully") {
            every { userRepository.findOrFail(match { it.id == addressedId }) } returns UserFactory.common(id = addressedId)
            val friendshipSolicitude = FriendshipSolicitudeFactory.common(
                id = friendshipSolicitudeId,
                solicitudeStatus = PENDING,
                addressedId = addressedId
            )
            every { friendshipSolicitudeRepository.findOrFail(any()) } returns friendshipSolicitude
            every { friendshipSolicitudeRepository.save(match { it.id == friendshipSolicitudeId }) } returns friendshipSolicitude.copy(
                solicitudeStatus = REJECTED
            )

            val response = handler.handle(
                RejectFriendshipSolicitudeCommand(
                    FriendshipSolicitudeDecisionDTO(
                        id = friendshipSolicitudeId,
                        addressedId = addressedId,
                        status = REJECTED
                    )
                )
            )

            Then("the friendship solicitude must be rejected") {
                response.friendshipSolicitude.status.shouldBe(REJECTED)
                response.friendshipSolicitude.id.shouldBe(friendshipSolicitudeId)
                response.friendshipSolicitude.addressedId.shouldBe(addressedId)
                verify(exactly = 1) { userRepository.findOrFail(any()) }
                verify(exactly = 1) { friendshipSolicitudeRepository.findOrFail(any()) }
                verify(exactly = 1) { friendshipSolicitudeRepository.save(any()) }
            }
        }
    }
})
