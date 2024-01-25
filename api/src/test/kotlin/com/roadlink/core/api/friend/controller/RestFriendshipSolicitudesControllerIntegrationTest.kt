package com.roadlink.core.api.friend.controller

import com.roadlink.core.api.BaseControllerTest
import com.roadlink.core.api.friend.FriendshipSolicitudeFactory
import com.roadlink.core.api.user.controller.RestUserController
import com.roadlink.core.api.user.controller.UserFactory
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.user.User
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

}
