package com.roadlink.core.api.friend.controller

import com.roadlink.core.api.BaseControllerTest
import com.roadlink.core.api.friend.FriendshipSolicitudeFactory
import com.roadlink.core.api.user.controller.UserFactory
import com.roadlink.core.domain.friend.FriendshipSolicitude.Status.ACCEPTED
import com.roadlink.core.domain.friend.FriendshipSolicitude.Status.PENDING
import com.roadlink.core.domain.friend.FriendshipSolicitude.Status.REJECTED
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@WebMvcTest(controllers = [RestFriendshipSolicitudesController::class])
class RestFriendshipSolicitudesControllerIntegrationTest : BaseControllerTest() {

    @Autowired
    private lateinit var controller: RestFriendshipSolicitudesController

    override fun getControllerUnderTest(): Any {
        return this.controller
    }

    /**
     * Create Friendship Solicitude
     */
    @Test
    fun `a friendship solicitude can be created successfully`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common(
            email = "martin.bosch@gmail.com",
            firstName = "martin",
            lastName = "bosch"
        )
        val expectedFriendshipSolicitude = FriendshipSolicitudeFactory.common(
            requesterId = george.id,
            addressedId = martin.id
        )

        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every { friendshipSolicitudeRepository.findAll(any()) } returns emptyList()
        every {
            friendshipSolicitudeRepository.save(match {
                it.requesterId == george.id
                        && it.addressedId == martin.id
            })
        } returns expectedFriendshipSolicitude

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${martin.id}/friendship_solicitudes").content(
                """{
                        "requester_id": "${george.id}"
                    }""".trimMargin()
            ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn().response.contentAsString

        // Then
        response.shouldNotBeNull()
        response.shouldBe(
            """
            {
                "id":"${expectedFriendshipSolicitude.id}",
                "addressed_id":"${martin.id}",
                "requester_id":"${george.id}",
                "status":"PENDING"
            }
        """.trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 2) { userRepository.findOrFail(any()) }
        verify(exactly = 2) { friendshipSolicitudeRepository.findAll(any()) }
        verify(exactly = 1) { friendshipSolicitudeRepository.save(any()) }
    }

    @Test
    fun `when the users are already friends, then an exception must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common(
            email = "martin.bosch@gmail.com",
            firstName = "martin",
            lastName = "bosch"
        )
        martin.beFriendOf(george)

        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every { friendshipSolicitudeRepository.findAll(any()) } returns emptyList()

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${martin.id}/friendship_solicitudes").content(
                """{
                        "requester_id": "${george.id}"
                    }""".trimMargin()
            ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isPreconditionFailed)
            .andReturn().response.contentAsString

        // Then
        response.shouldNotBeNull()
        verify(exactly = 2) { userRepository.findOrFail(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepository.findAll(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepository.save(any()) }
    }

    @Test
    fun `when the requester user does not exist, then an exception must be retrieved`() {
        // Given
        val requesterId = UUID.randomUUID()
        val addressedId = UUID.randomUUID()
        every { userRepository.findOrFail(match { it.id == requesterId }) } throws DynamoDbException.EntityDoesNotExist(
            requesterId.toString()
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/$addressedId/friendship_solicitudes").content(
                """{
                        "requester_id": "$requesterId"
                    }""".trimMargin()
            ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
            .andReturn().response.contentAsString

        // Then
        response.shouldNotBeNull()
        response.shouldBe("""{"code":"ENTITY_NOT_EXIST","message":"Entity $requesterId does not exist"}""")
        verify(exactly = 1) { userRepository.findOrFail(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepository.findAll(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepository.save(any()) }
    }

    @Test
    fun `when the addressed user does not exist, then an exception must be retrieved`() {
        // Given
        val martin = UserFactory.common()
        val addressedId = UUID.randomUUID()

        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every { userRepository.findOrFail(match { it.id == addressedId }) } throws DynamoDbException.EntityDoesNotExist(
            addressedId.toString()
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${addressedId}/friendship_solicitudes").content(
                """{
                        "requester_id": "${martin.id}"
                    }""".trimMargin()
            ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
            .andReturn().response.contentAsString

        // Then
        response.shouldNotBeNull()
        response.shouldBe("""{"code":"ENTITY_NOT_EXIST","message":"Entity $addressedId does not exist"}""")
        verify(exactly = 2) { userRepository.findOrFail(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepository.findAll(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepository.save(any()) }
    }

    @Test
    fun `when the requester is empty, then a 400 status code must be retrieved`() {
        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${UUID.randomUUID()}/friendship_solicitudes")
                .content(
                    """{
                        "requester_id": ""
                    }""".trimMargin()
                ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn().response.contentAsString

        // Then
        response.shouldNotBeNull()
        response.shouldBe("""{"code":"INVALID_JSON","message":"Invalid request format: could not be parsed to a valid JSON"}""")
    }

    @Test
    fun `when there are pending solicitudes, then an exception must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common(
            email = "martin.bosch@gmail.com",
            firstName = "martin",
            lastName = "bosch"
        )
        val friendshipSolicitude = FriendshipSolicitudeFactory.common(
            requesterId = george.id,
            addressedId = martin.id
        )

        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every {
            friendshipSolicitudeRepository.findAll(match {
                it.addressedId == martin.id && it.requesterId == george.id && it.solicitudeStatus == PENDING
            })
        } returns listOf(
            friendshipSolicitude
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${martin.id}/friendship_solicitudes").content(
                """{
                        "requester_id": "${george.id}"
                    }""".trimMargin()
            ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isPreconditionFailed)
            .andReturn().response.contentAsString

        // Then
        response.shouldNotBeNull()
        response.shouldBe("""{"code":"FRIENDSHIP_SOLICITUDE_ALREADY_SENT","message":"User ${george.id} has a pending friendship solicitude to ${martin.id}"}""")
        verify(exactly = 2) { userRepository.findOrFail(any()) }
        verify(exactly = 1) { friendshipSolicitudeRepository.findAll(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepository.save(any()) }
    }

    @Test
    fun `when the addressed user has already sent a solicitude previously, then an exception must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common(
            email = "martin.bosch@gmail.com",
            firstName = "martin",
            lastName = "bosch"
        )
        val friendshipSolicitude = FriendshipSolicitudeFactory.common(
            requesterId = george.id,
            addressedId = martin.id
        )

        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every {
            friendshipSolicitudeRepository.findAll(match {
                it.addressedId == martin.id && it.requesterId == george.id && it.solicitudeStatus == PENDING
            })
        } returns listOf()
        every {
            friendshipSolicitudeRepository.findAll(match {
                it.addressedId == george.id && it.requesterId == martin.id && it.solicitudeStatus == PENDING
            })
        } returns listOf(
            friendshipSolicitude
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${martin.id}/friendship_solicitudes").content(
                """{
                        "requester_id": "${george.id}"
                    }""".trimMargin()
            ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isPreconditionFailed)
            .andReturn().response.contentAsString

        // Then
        response.shouldNotBeNull()
        response.shouldBe("""{"code":"FRIENDSHIP_SOLICITUDE_ALREADY_SENT","message":"User ${martin.id} has a pending friendship solicitude to ${george.id}"}""")
        verify(exactly = 2) { userRepository.findOrFail(any()) }
        verify(exactly = 2) { friendshipSolicitudeRepository.findAll(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepository.save(any()) }
    }

    /**
     * Accept Friendship Solicitude
     */
    @Test
    fun `when the users are not friends and there isn't any pending solicitude, then a 200 response must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common()
        val solicitude =
            FriendshipSolicitudeFactory.common(addressedId = george.id, requesterId = martin.id)

        every { friendshipSolicitudeRepository.findOrFail(match { it.id == solicitude.id }) } returns solicitude
        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { userRepository.saveAll(any()) } returns listOf(george, martin)
        every { friendshipSolicitudeRepository.save(any()) } returns solicitude.copy(
            solicitudeStatus = ACCEPTED
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.put("/users/${george.id}/friendship_solicitudes/${solicitude.id}/accept")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """
            {
                "id":"${solicitude.id}",
                "addressed_id":"${george.id}",
                "requester_id":"${martin.id}",
                "status":"ACCEPTED"
            }
            """.trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { friendshipSolicitudeRepository.findOrFail(any()) }
        verify(exactly = 1) { userRepository.saveAll(any()) }
        // TODO it could be resolved with only 2 network calls
        verify(exactly = 4) { userRepository.findOrFail(any()) }
    }

    @Test
    fun `when the users are not friends but the solicitude was rejected, then an exception must be throw`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common()
        val solicitude =
            FriendshipSolicitudeFactory.common(
                addressedId = george.id,
                requesterId = martin.id,
                solicitudeStatus = REJECTED
            )

        every { friendshipSolicitudeRepository.findOrFail(match { it.id == solicitude.id }) } returns solicitude
        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.put("/users/${george.id}/friendship_solicitudes/${solicitude.id}/accept")
        ).andExpect(MockMvcResultMatchers.status().isPreconditionFailed)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe("""{"code":"FRIENDSHIP_SOLICITUDE_STATUS_CAN_NOT_CHANGE","message":"Friendship solicitude ${solicitude.id} status can not change, because it has raised an immutable status REJECTED"}""")
        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { userRepository.findAll(any()) }
    }

    @Test
    fun `when the users are not friends but the solicitude was accepted, then an exception must be rejected`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common()
        val solicitude =
            FriendshipSolicitudeFactory.common(
                addressedId = george.id,
                requesterId = martin.id,
                solicitudeStatus = ACCEPTED
            )

        every { friendshipSolicitudeRepository.findOrFail(match { it.id == solicitude.id }) } returns solicitude
        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.put("/users/${george.id}/friendship_solicitudes/${solicitude.id}/accept")
        ).andExpect(MockMvcResultMatchers.status().isPreconditionFailed)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe("""{"code":"FRIENDSHIP_SOLICITUDE_STATUS_CAN_NOT_CHANGE","message":"Friendship solicitude ${solicitude.id} status can not change, because it has raised an immutable status ACCEPTED"}""")
        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { userRepository.findAll(any()) }
    }

    /**
     * Reject friendship solicitude
     */
    @Test
    fun `when reject a friendship solicitude, the users are not friends and there isn't any pending solicitude, then a 200 response must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common()
        val solicitude =
            FriendshipSolicitudeFactory.common(addressedId = george.id, requesterId = martin.id)

        every { friendshipSolicitudeRepository.findOrFail(match { it.id == solicitude.id }) } returns solicitude
        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { userRepository.saveAll(any()) } returns listOf(george, martin)
        every { friendshipSolicitudeRepository.save(any()) } returns solicitude.copy(
            solicitudeStatus = REJECTED
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.put("/users/${george.id}/friendship_solicitudes/${solicitude.id}/reject")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """
            {
                "id":"${solicitude.id}",
                "addressed_id":"${george.id}",
                "requester_id":"${martin.id}",
                "status":"REJECTED"
            }
            """.trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { friendshipSolicitudeRepository.findOrFail(any()) }
        verify(exactly = 1) { friendshipSolicitudeRepository.save(any()) }
    }

    @Test
    fun `when reject a friendship solicitude, the users are not friends but the solicitude was rejected, then an exception must be throw`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common()
        val solicitude =
            FriendshipSolicitudeFactory.common(
                addressedId = george.id,
                requesterId = martin.id,
                solicitudeStatus = REJECTED
            )

        every { friendshipSolicitudeRepository.findOrFail(match { it.id == solicitude.id }) } returns solicitude
        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.put("/users/${george.id}/friendship_solicitudes/${solicitude.id}/reject")
        ).andExpect(MockMvcResultMatchers.status().isPreconditionFailed)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe("""{"code":"FRIENDSHIP_SOLICITUDE_STATUS_CAN_NOT_CHANGE","message":"Friendship solicitude ${solicitude.id} status can not change, because it has raised an immutable status REJECTED"}""")
        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { userRepository.findAll(any()) }
    }

    @Test
    fun `when reject a friendship solicitude, the users are not friends but the solicitude was accepted, then an exception must be rejected`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common()
        val solicitude =
            FriendshipSolicitudeFactory.common(
                addressedId = george.id,
                requesterId = martin.id,
                solicitudeStatus = ACCEPTED
            )

        every { friendshipSolicitudeRepository.findOrFail(match { it.id == solicitude.id }) } returns solicitude
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.put("/users/${george.id}/friendship_solicitudes/${solicitude.id}/reject")
        ).andExpect(MockMvcResultMatchers.status().isPreconditionFailed)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe("""{"code":"FRIENDSHIP_SOLICITUDE_STATUS_CAN_NOT_CHANGE","message":"Friendship solicitude ${solicitude.id} status can not change, because it has raised an immutable status ACCEPTED"}""")
        verify(exactly = 0) { friendshipSolicitudeRepository.save(any()) }
        verify(exactly = 1) { friendshipSolicitudeRepository.findOrFail(any()) }
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    @Test
    fun `when reject a friendship solicitude, but the users does not exist then an exception must be rejected`() {
        // Given
        val george = UserFactory.common()
        val addressedId = UUID.randomUUID()
        val solicitude =
            FriendshipSolicitudeFactory.common(
                addressedId = addressedId,
                requesterId = george.id,
                solicitudeStatus = PENDING
            )

        every { friendshipSolicitudeRepository.findOrFail(match { it.id == solicitude.id }) } returns solicitude
        every { userRepository.findOrFail(match { it.id == addressedId }) } throws DynamoDbException.EntityDoesNotExist(
            addressedId.toString()
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.put("/users/$addressedId/friendship_solicitudes/${solicitude.id}/reject")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe("""{"code":"ENTITY_NOT_EXIST","message":"Entity $addressedId does not exist"}""")
        verify(exactly = 0) { friendshipSolicitudeRepository.save(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepository.findOrFail(any()) }
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }
}
