package com.roadlink.core.api.feedback.controller

import com.roadlink.core.api.BaseControllerTest
import com.roadlink.core.api.feedback.FeedbackFactory
import com.roadlink.core.api.feedback.FeedbackSolicitudeFactory
import com.roadlink.core.api.user.controller.UserFactory
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude.Status
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@WebMvcTest(controllers = [FeedbackSolicitudeController::class])
class FeedbackSolicitudeControllerIntegrationTest : BaseControllerTest() {

    @Autowired
    private lateinit var controller: FeedbackSolicitudeController

    override fun getControllerUnderTest(): Any {
        return controller
    }

    /**
     * Create Feedback Solicitude
     */
    @Test
    fun `when create a valid feedback solicitude, the it must work ok`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common()
        val tripId = UUID.randomUUID()
        val feedbackSolicitudeId = UUID.randomUUID()

        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every { feedbackSolicitudeRepository.save(any()) } returns FeedbackSolicitudeFactory.common(
            id = feedbackSolicitudeId,
            tripId = tripId,
            reviewerId = martin.id,
            receiverId = george.id,
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${george.id}/feedback_solicitudes").content(
                """{
                    "reviewer_id":"${martin.id}",
                    "trip_id":"$tripId"
                }""".trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isCreated).andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{
                "id":"$feedbackSolicitudeId",
                "reviewer_id":"${martin.id}",
                "receiver_id":"${george.id}",
                "trip_id":"$tripId",
                "status":"PENDING"
            }""".trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 2) { userRepository.findOrFail(any()) }
        verify(exactly = 1) { feedbackSolicitudeRepository.save(any()) }
    }

    /**
     * List Feedback Solicitudes
     */
    @Test
    fun `when list pending feedback solicitudes, the it must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val receiverId = UUID.randomUUID()
        val tripId = UUID.randomUUID()
        val feedbackSolicitudeId = UUID.randomUUID()

        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { feedbackSolicitudeRepository.findAll(match { it.reviewerId == george.id && it.status == Status.PENDING }) } returns listOf(
            FeedbackSolicitudeFactory.common(
                id = feedbackSolicitudeId,
                tripId = tripId,
                reviewerId = george.id,
                receiverId = receiverId,
            )
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/users/${george.id}/feedback_solicitudes?status=PENDING")
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """[
                  {
                    "id": "$feedbackSolicitudeId",
                    "reviewer_id": "${george.id}",
                    "receiver_id": "$receiverId",
                    "trip_id": "$tripId",
                    "status": "PENDING"
                  }
            ]""".trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { feedbackSolicitudeRepository.findAll(any()) }
    }

    @Test
    fun `list pending feedback solicitudes but there no is anything`() {
        // Given
        val george = UserFactory.common()

        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { feedbackSolicitudeRepository.findAll(match { it.reviewerId == george.id && it.status == Status.PENDING }) } returns listOf()

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/users/${george.id}/feedback_solicitudes?status=PENDING")
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """[]""".trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { feedbackSolicitudeRepository.findAll(any()) }
    }

    /**
     * Complete Feedback Solicitude
     */
    @Test
    fun `when complete a feedback solicitude successfully, then it must work ok`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common()
        val tripId = UUID.randomUUID()
        val feedbackSolicitudeId = UUID.randomUUID()

        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every {
            feedbackSolicitudeRepository.findOrFail(match {
                it.id == feedbackSolicitudeId && it.status == Status.PENDING
            })
        } returns FeedbackSolicitudeFactory.common(
            id = feedbackSolicitudeId,
            receiverId = martin.id,
            reviewerId = george.id,
            tripId = tripId,
            status = Status.PENDING
        )

        every { feedbackSolicitudeRepository.save(any()) } returns FeedbackSolicitudeFactory.common(
            id = feedbackSolicitudeId,
            tripId = tripId,
            reviewerId = martin.id,
            receiverId = george.id,
            status = Status.COMPLETED
        )

        every { feedbackRepository.save(match { it.receiverId == martin.id && it.reviewerId == george.id && it.tripId == tripId }) } returns FeedbackFactory.common(
            id = UUID.randomUUID(),
            receiverId = martin.id,
            reviewerId = george.id,
            comment = "ok",
            rating = 4,
            tripId = tripId
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.put("/users/${george.id}/feedback_solicitudes/$feedbackSolicitudeId/complete")
                .content(
                    """{
                        "comment":"ok",
                        "rating":"4"
                    }""".trimIndent()
                ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{
                "id":"$feedbackSolicitudeId",
                "reviewer_id":"${martin.id}",
                "receiver_id":"${george.id}",
                "trip_id":"$tripId",
                "status":"COMPLETED"
            }""".trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { userRepository.findOrFail(any()) }
        verify(exactly = 1) { feedbackSolicitudeRepository.save(any()) }
        verify { feedbackRepository.save(any()) }
        verify { feedbackSolicitudeRepository.findOrFail(any()) }
    }
}
