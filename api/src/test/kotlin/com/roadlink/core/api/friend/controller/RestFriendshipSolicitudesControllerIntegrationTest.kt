package com.roadlink.core.api.friend.controller

import com.roadlink.core.api.BaseControllerTest
import com.roadlink.core.api.friend.FriendshipSolicitudeFactory
import com.roadlink.core.api.user.controller.RestUserController
import com.roadlink.core.api.user.controller.UserFactory
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitude.*
import com.roadlink.core.domain.friend.FriendshipSolicitude.Status.*
import com.roadlink.core.domain.user.User
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.http.MediaType.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.UUID

@WebMvcTest(controllers = [RestFriendshipSolicitudesController::class])
class RestFriendshipSolicitudesControllerIntegrationTest : BaseControllerTest() {

    @Autowired
    private lateinit var controller: RestFriendshipSolicitudesController

    override fun getControllerUnderTest(): Any {
        return this.controller
    }

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

        every { userRepositoryPort.findOrFail(match { it.id == george.id }) } returns george
        every { userRepositoryPort.findOrFail(match { it.id == martin.id }) } returns martin
        every { friendshipSolicitudeRepositoryPort.findAll(any()) } returns emptyList()
        every {
            friendshipSolicitudeRepositoryPort.save(match {
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
                "addressed_Id":"${martin.id}",
                "requester_id":"${george.id}",
                "status":"PENDING"
            }
        """.trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 2) { userRepositoryPort.findOrFail(any()) }
        verify(exactly = 2) { friendshipSolicitudeRepositoryPort.findAll(any()) }
        verify(exactly = 1) { friendshipSolicitudeRepositoryPort.save(any()) }
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

        every { userRepositoryPort.findOrFail(match { it.id == george.id }) } returns george
        every { userRepositoryPort.findOrFail(match { it.id == martin.id }) } returns martin
        every { friendshipSolicitudeRepositoryPort.findAll(any()) } returns emptyList()

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
        verify(exactly = 2) { userRepositoryPort.findOrFail(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepositoryPort.findAll(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepositoryPort.save(any()) }
    }

    @Test
    fun `when the requester user does not exist, then an exception must be retrieved`() {
        // Given
        val requesterId = UUID.randomUUID()
        val addressedId = UUID.randomUUID()
        every { userRepositoryPort.findOrFail(match { it.id == requesterId }) } throws DynamoDbException.EntityDoesNotExist(
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
        response.shouldBe("""{"code":"404 NOT_FOUND","message":"Entity $requesterId does not exist"}""")
        verify(exactly = 1) { userRepositoryPort.findOrFail(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepositoryPort.findAll(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepositoryPort.save(any()) }
    }

    @Test
    fun `when the addressed user does not exist, then an exception must be retrieved`() {
        // Given
        val martin = UserFactory.common()
        val addressedId = UUID.randomUUID()

        every { userRepositoryPort.findOrFail(match { it.id == martin.id }) } returns martin
        every { userRepositoryPort.findOrFail(match { it.id == addressedId }) } throws DynamoDbException.EntityDoesNotExist(
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
        response.shouldBe("""{"code":"404 NOT_FOUND","message":"Entity $addressedId does not exist"}""")
        verify(exactly = 2) { userRepositoryPort.findOrFail(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepositoryPort.findAll(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepositoryPort.save(any()) }
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
        response.shouldBe("""{"code":"400 BAD_REQUEST","message":"Invalid request format: could not be parsed to a valid JSON"}""")
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

        every { userRepositoryPort.findOrFail(match { it.id == george.id }) } returns george
        every { userRepositoryPort.findOrFail(match { it.id == martin.id }) } returns martin
        every {
            friendshipSolicitudeRepositoryPort.findAll(match {
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
        response.shouldBe("""{"code":"412 PRECONDITION_FAILED","message":"User ${george.id} has a pending friendship solicitude to ${martin.id}"}""")
        verify(exactly = 2) { userRepositoryPort.findOrFail(any()) }
        verify(exactly = 1) { friendshipSolicitudeRepositoryPort.findAll(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepositoryPort.save(any()) }
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

        every { userRepositoryPort.findOrFail(match { it.id == george.id }) } returns george
        every { userRepositoryPort.findOrFail(match { it.id == martin.id }) } returns martin
        every {
            friendshipSolicitudeRepositoryPort.findAll(match {
                it.addressedId == martin.id && it.requesterId == george.id && it.solicitudeStatus == PENDING
            })
        } returns listOf()
        every {
            friendshipSolicitudeRepositoryPort.findAll(match {
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
        response.shouldBe("""{"code":"412 PRECONDITION_FAILED","message":"User ${martin.id} has a pending friendship solicitude to ${george.id}"}""")
        verify(exactly = 2) { userRepositoryPort.findOrFail(any()) }
        verify(exactly = 2) { friendshipSolicitudeRepositoryPort.findAll(any()) }
        verify(exactly = 0) { friendshipSolicitudeRepositoryPort.save(any()) }
    }


}
