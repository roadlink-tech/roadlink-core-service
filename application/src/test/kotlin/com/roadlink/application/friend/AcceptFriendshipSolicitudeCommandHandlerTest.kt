package com.roadlink.application.friend

import com.roadlink.application.user.UserFactory
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitude.Status.*
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria
import com.roadlink.core.domain.friend.FriendshipSolicitudeException
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.UserException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.exactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class AcceptFriendshipSolicitudeCommandHandlerTest : BehaviorSpec({
    val userRepository: RepositoryPort<User, UserCriteria> = mockk()
    val friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria> = mockk()

    afterEach {
        clearMocks(userRepository, friendshipSolicitudeRepository)
    }

    Given("an accept friendship solicitude command handler") {
        val handler = AcceptFriendshipSolicitudeCommandHandler(userRepository, friendshipSolicitudeRepository)

        When("a solicitude has been already rejected") {
            val friendshipSolicitudeId = UUID.randomUUID()
            val userId = UUID.randomUUID()

            every { friendshipSolicitudeRepository.findOrFail(match { it.id == friendshipSolicitudeId }) } returns FriendshipSolicitudeFactory.common(
                id = friendshipSolicitudeId,
                solicitudeStatus = REJECTED
            )

            val exception = shouldThrow<FriendshipSolicitudeException.FriendshipSolicitudeStatusCanNotChange> {
                handler.handle(
                    AcceptFriendshipSolicitudeCommand(
                        FriendshipSolicitudeDecisionDTO(
                            id = friendshipSolicitudeId,
                            addressedId = userId,
                            status = ACCEPTED
                        )
                    )
                )
            }

            Then("an exception must be raised") {
                exception.shouldNotBeNull()
                verify(exactly = 0) { userRepository.findOrFail(any()) }
                verify(exactly = 0) { userRepository.save(any()) }
            }
        }

        When("a solicitude has been already accepted") {
            val friendshipSolicitudeId = UUID.randomUUID()
            val userId = UUID.randomUUID()

            every { friendshipSolicitudeRepository.findOrFail(match { it.id == friendshipSolicitudeId }) } returns FriendshipSolicitudeFactory.common(
                id = friendshipSolicitudeId,
                solicitudeStatus = ACCEPTED
            )

            val exception = shouldThrow<FriendshipSolicitudeException.FriendshipSolicitudeStatusCanNotChange> {
                handler.handle(
                    AcceptFriendshipSolicitudeCommand(
                        FriendshipSolicitudeDecisionDTO(
                            id = friendshipSolicitudeId,
                            addressedId = userId,
                            status = ACCEPTED
                        )
                    )
                )
            }

            Then("an exception must be raised") {
                exception.shouldNotBeNull()
                verify(exactly = 0) { userRepository.findOrFail(any()) }
                verify(exactly = 0) { userRepository.save(any()) }
            }
        }

        When("the users are already friends, and the solicitude has not been accepted yet") {
            val georgeId = UUID.randomUUID()
            val martinId = UUID.randomUUID()
            val george = UserFactory.common(id = georgeId)
            val martin = UserFactory.common(id = martinId)
            george.beFriendOf(martin)

            val friendshipSolicitudeId = UUID.randomUUID()

            every { friendshipSolicitudeRepository.findOrFail(match { it.id == friendshipSolicitudeId }) } returns FriendshipSolicitudeFactory.common(
                id = friendshipSolicitudeId,
                requesterId = george.id,
                solicitudeStatus = PENDING
            )

            every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
            every { userRepository.findOrFail(match { it.id == george.id }) } returns george
            
            val exception = shouldThrow<UserException.UserAlreadyAreFriends> {
                handler.handle(
                    AcceptFriendshipSolicitudeCommand(
                        FriendshipSolicitudeDecisionDTO(
                            id = friendshipSolicitudeId,
                            addressedId = martin.id,
                            status = ACCEPTED
                        )
                    )
                )
            }

            Then("an exception must be raised") {
                exception.shouldNotBeNull()
                verify(exactly = 1) { friendshipSolicitudeRepository.findOrFail(any()) }
                verify(exactly = 2) { userRepository.findOrFail(any()) }
                verify(exactly = 0) { userRepository.save(any()) }
            }
        }
    }
})
