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
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class CreateFriendshipSolicitudeCommandHandlerTest : BehaviorSpec({

    val userRepository: RepositoryPort<User, UserCriteria> = mockk()
    val friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria> =
        mockk()

    afterEach {
        clearMocks(userRepository, friendshipSolicitudeRepository)
    }


    Given("a create friendship solicitude command") {
        val handler =
            CreateFriendshipSolicitudeCommandHandler(userRepository, friendshipSolicitudeRepository)

        When("the users are friends") {
            val jorge = UserFactory.common()
            val martin = UserFactory.common()

            jorge.beFriendOf(martin)

            every { userRepository.findOrFail(match { it.id == jorge.id }) } returns jorge
            every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin

            shouldThrow<UserException.UserAlreadyAreFriends> {
                handler.handle(
                    CreateFriendshipSolicitudeCommand(
                        friendshipSolicitude = FriendshipSolicitudeDTO(
                            requesterId = jorge.id,
                            addressedId = martin.id
                        )
                    )
                )
            }

            Then("the solicitude must not be created") {
                verify(exactly = 2) { userRepository.findOrFail(any()) }
                verify(exactly = 0) { friendshipSolicitudeRepository.findAll(any()) }
                verify(exactly = 0) { friendshipSolicitudeRepository.save(any()) }
            }
        }

        When("there are pending solicitudes") {
            val jorge = UserFactory.common()
            val martin = UserFactory.common()

            every { userRepository.findOrFail(match { it.id == jorge.id }) } returns jorge
            every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin

            every { friendshipSolicitudeRepository.findAll(any()) } returns listOf(
                FriendshipSolicitudeFactory.common(requesterId = jorge.id, addressedId = martin.id)
            )

            shouldThrow<FriendshipSolicitudeException.FriendshipSolicitudeAlreadySent> {
                handler.handle(
                    CreateFriendshipSolicitudeCommand(
                        friendshipSolicitude = FriendshipSolicitudeDTO(
                            requesterId = jorge.id,
                            addressedId = martin.id
                        )
                    )
                )
            }

            Then("the solicitude must be created successfully") {
                verify(exactly = 2) { userRepository.findOrFail(any()) }
                verify(exactly = 2) { friendshipSolicitudeRepository.findAll(any()) }
                verify(exactly = 0) { friendshipSolicitudeRepository.save(any()) }
            }
        }

        When("there are pending solicitudes, but with a different user ") {
            val jorge = UserFactory.common()
            val martin = UserFactory.common()
            val felix = UserFactory.common()

            every { userRepository.findOrFail(match { it.id == jorge.id }) } returns jorge
            every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin

            every { friendshipSolicitudeRepository.findAll(match { it.addressedId == felix.id && it.requesterId == jorge.id }) } returns listOf(
                FriendshipSolicitudeFactory.common(requesterId = jorge.id, addressedId = felix.id)
            )
            every { friendshipSolicitudeRepository.findAll(match { it.addressedId == martin.id && it.requesterId == jorge.id }) } returns emptyList()
            every { friendshipSolicitudeRepository.findAll(match { it.addressedId == jorge.id && it.requesterId == martin.id }) } returns emptyList()

            every { friendshipSolicitudeRepository.save(any()) } returns FriendshipSolicitude(
                id = UUID.randomUUID(),
                requesterId = jorge.id,
                addressedId = martin.id
            )

            val response = handler.handle(
                CreateFriendshipSolicitudeCommand(
                    friendshipSolicitude = FriendshipSolicitudeDTO(
                        requesterId = jorge.id,
                        addressedId = martin.id
                    )
                )
            )

            Then("the solicitude must be created successfully") {
                verify(exactly = 2) { userRepository.findOrFail(any()) }
                verify(exactly = 2) { friendshipSolicitudeRepository.findAll(any()) }
                verify(exactly = 1) { friendshipSolicitudeRepository.save(any()) }
                response.friendshipSolicitude.addressedId.shouldBe(martin.id)
                response.friendshipSolicitude.requesterId.shouldBe(jorge.id)
                response.friendshipSolicitude.status.shouldBe(PENDING)
            }
        }

        When("felix sent a friendship solicitude to jorge, but jorge try to send another one to felix") {
            val jorge = UserFactory.common()
            val felix = UserFactory.common()

            every { userRepository.findOrFail(match { it.id == jorge.id }) } returns jorge
            every { userRepository.findOrFail(match { it.id == felix.id }) } returns felix

            every { friendshipSolicitudeRepository.findAll(match { it.addressedId == jorge.id && it.requesterId == felix.id }) } returns listOf(
                FriendshipSolicitudeFactory.common(requesterId = felix.id, addressedId = jorge.id)
            )

            every { friendshipSolicitudeRepository.findAll(match { it.addressedId == felix.id && it.requesterId == jorge.id }) } returns listOf()

            every { friendshipSolicitudeRepository.save(any()) } returns FriendshipSolicitude(
                id = UUID.randomUUID(),
                requesterId = felix.id,
                addressedId = jorge.id
            )

            shouldThrow<FriendshipSolicitudeException.FriendshipSolicitudeAlreadySent> {
                handler.handle(
                    CreateFriendshipSolicitudeCommand(
                        friendshipSolicitude = FriendshipSolicitudeDTO(
                            requesterId = jorge.id,
                            addressedId = felix.id
                        )
                    )
                )
            }

            Then("the solicitude must be created successfully") {
                verify(exactly = 2) { userRepository.findOrFail(any()) }
                verify(exactly = 2) { friendshipSolicitudeRepository.findAll(any()) }
                verify(exactly = 0) { friendshipSolicitudeRepository.save(any()) }
            }
        }

        When("the users are not friends") {
            val jorge = UserFactory.common()
            val martin = UserFactory.common()

            every { userRepository.findOrFail(match { it.id == jorge.id }) } returns jorge
            every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin

            every { friendshipSolicitudeRepository.findAll(any()) } returns emptyList()
            every { friendshipSolicitudeRepository.save(any()) } returns FriendshipSolicitude(
                id = UUID.randomUUID(),
                requesterId = jorge.id,
                addressedId = martin.id
            )

            val response = handler.handle(
                CreateFriendshipSolicitudeCommand(
                    friendshipSolicitude = FriendshipSolicitudeDTO(
                        requesterId = jorge.id,
                        addressedId = martin.id
                    )
                )
            )

            Then("the solicitude must be created successfully") {
                verify(exactly = 2) { userRepository.findOrFail(any()) }
                verify(exactly = 2) { friendshipSolicitudeRepository.findAll(any()) }
                verify(exactly = 1) { friendshipSolicitudeRepository.save(any()) }
                response.friendshipSolicitude.addressedId.shouldBe(martin.id)
                response.friendshipSolicitude.requesterId.shouldBe(jorge.id)
                response.friendshipSolicitude.status.shouldBe(PENDING)
            }
        }
    }

})
