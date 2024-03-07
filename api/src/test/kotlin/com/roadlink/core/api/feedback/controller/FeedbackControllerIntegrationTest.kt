package com.roadlink.core.api.feedback.controller

import com.roadlink.core.api.BaseControllerTest
import com.roadlink.core.api.feedback.FeedbackFactory
import com.roadlink.core.api.user.controller.UserFactory
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.lang.RuntimeException
import java.util.*

@WebMvcTest(controllers = [FeedbackController::class])
class FeedbackControllerIntegrationTest : BaseControllerTest() {

    @Autowired
    private lateinit var controller: FeedbackController

    override fun getControllerUnderTest(): Any {
        return controller
    }

    /**
     * Create Feedback
     */
    @Test
    fun `when create a valid feedback, the it must work ok`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common()
        val tripLegId = UUID.randomUUID()
        val feedbackId = UUID.randomUUID()

        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every { feedbackRepository.save(any()) } returns FeedbackFactory.common(
            id = feedbackId,
            tripLegId = tripLegId,
            reviewerId = martin.id,
            receiverId = george.id,
            comment = "ok!",
            rating = 5
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${george.id}/feedbacks").content(
                """{
                    "reviewer_id":"${martin.id}",
                    "trip_leg_id":"$tripLegId",
                    "comment":"ok!",
                    "rating":"5"
                }""".trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isCreated).andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{
                "id":"$feedbackId",
                "reviewer_id":"${martin.id}",
                "receiver_id":"${george.id}",
                "trip_leg_id":"$tripLegId",
                "comment":"ok!",
                "rating":5
            }""".trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 2) { userRepository.findOrFail(any()) }
        verify(exactly = 1) { feedbackRepository.save(any()) }
    }

    @Test
    fun `when create a valid feedback but the receiver does not exist, the it must throw an exception`() {
        // Given
        val georgeId = UUID.randomUUID()
        val martin = UserFactory.common()
        val tripLegId = UUID.randomUUID()

        every { userRepository.findOrFail(match { it.id == georgeId }) } throws DynamoDbException.EntityDoesNotExist(
            georgeId.toString()
        )
        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${georgeId}/feedbacks").content(
                """{
                    "reviewer_id":"${martin.id}",
                    "trip_leg_id":"$tripLegId",
                    "comment":"ok!",
                    "rating":"5"
                }""".trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound).andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{"code":"ENTITY_NOT_EXIST","message":"Entity $georgeId does not exist"}"""
        )
        verify(exactly = 2) { userRepository.findOrFail(any()) }
        verify(exactly = 0) { feedbackRepository.save(any()) }
    }

    @Test
    fun `when create a valid feedback but the reviewer does not exist, the it must throw an exception`() {
        // Given
        val george = UserFactory.common()
        val martinId = UUID.randomUUID()
        val tripLegId = UUID.randomUUID()

        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { userRepository.findOrFail(match { it.id == martinId }) } throws DynamoDbException.EntityDoesNotExist(
            martinId.toString()
        )
        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${george.id}/feedbacks").content(
                """{
                    "reviewer_id":"$martinId",
                    "trip_leg_id":"$tripLegId",
                    "comment":"ok!",
                    "rating":"5"
                }""".trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound).andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{"code":"ENTITY_NOT_EXIST","message":"Entity $martinId does not exist"}"""
        )
        verify(exactly = 1) { userRepository.findOrFail(any()) }
        verify(exactly = 0) { feedbackRepository.save(any()) }
    }

    @Test
    fun `when it was an error when saving feedback, then an exception must be thrown`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common()
        val tripLegId = UUID.randomUUID()

        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every { feedbackRepository.save(any()) } throws RuntimeException()

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${george.id}/feedbacks").content(
                """{
                    "reviewer_id":"${martin.id}",
                    "trip_leg_id":"$tripLegId",
                    "comment":"ok!",
                    "rating":"5"
                }""".trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isInternalServerError)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe("""{"code":"INTERNAL_SERVER_ERROR","message":"Oops, something wrong happened"}""")
        verify(exactly = 2) { userRepository.findOrFail(any()) }
        verify(exactly = 1) { feedbackRepository.save(any()) }
    }

    /**
     * List Feedbacks
     */
    @Test
    fun `when list feedbacks of an existing user with a single review, then it must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val feedbacks = mutableListOf<Feedback>()
        val feedbackId = UUID.randomUUID()
        val tripLegId = UUID.randomUUID()
        val receiverId = UUID.randomUUID()
        feedbacks.add(
            FeedbackFactory.common(
                reviewerId = george.id,
                receiverId = receiverId,
                id = feedbackId,
                tripLegId = tripLegId,
                comment = "ok"
            )
        )

        every { feedbackRepository.findAll(match { it.receiverId == george.id }) } returns feedbacks
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/users/${george.id}/feedbacks")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """
            [
              {
                "id": "$feedbackId",
                "reviewer_id": "${george.id}",
                "receiver_id": "$receiverId",
                "trip_leg_id": "$tripLegId",
                "comment": "ok",
                "rating": 5
              }
            ]
        """.trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { feedbackRepository.findAll(any()) }
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    @Test
    fun `when list the feedbacks of an existing user, then it must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val feedbacks = mutableListOf<Feedback>()
        repeat(5) {
            feedbacks.add(FeedbackFactory.common(receiverId = george.id))
        }
        every { feedbackRepository.findAll(match { it.receiverId == george.id }) } returns feedbacks
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/users/${george.id}/feedbacks")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Then
        val feedbacksResponse: List<FeedbackResponse> =
            objectMapper.readValue(response, List::class.java) as List<FeedbackResponse>
        feedbacksResponse.size.shouldBe(5)
        verify(exactly = 1) { feedbackRepository.findAll(any()) }
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    @Test
    fun `when list the feedbacks but the user does not exist, then an exception must be throw`() {
        // Given
        val george = UserFactory.common()
        every { userRepository.findOrFail(match { it.id == george.id }) } throws DynamoDbException.EntityDoesNotExist(
            george.id.toString()
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/users/${george.id}/feedbacks")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe("""{"code":"ENTITY_NOT_EXIST","message":"Entity ${george.id} does not exist"}""")
        verify(exactly = 0) { feedbackRepository.findAll(any()) }
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }
}