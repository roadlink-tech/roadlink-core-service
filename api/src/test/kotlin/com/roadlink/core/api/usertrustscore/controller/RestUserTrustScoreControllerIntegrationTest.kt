package com.roadlink.core.api.usertrustscore.controller

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.UUID

@WebMvcTest(controllers = [RestUserTrustScoreController::class])
class RestUserTrustScoreControllerIntegrationTest : BaseControllerTest() {

    @Autowired
    private lateinit var controller: RestUserTrustScoreController

    override fun getControllerUnderTest(): Any {
        return controller
    }

    @Test
    fun `when look for the user trust score, then it must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common()
        george.beFriendOf(martin)
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george

        val feedbacksGiven = mutableListOf<Feedback>()
        repeat(10) {
            feedbacksGiven.add(FeedbackFactory.common(reviewerId = george.id, rating = 2))
        }
        val feedbacksReceived = mutableListOf<Feedback>()
        repeat(15) {
            feedbacksReceived.add(FeedbackFactory.common(receiverId = george.id, rating = 5))
        }

        every { feedbackRepository.findAll(match { it.reviewerId == george.id }) } returns feedbacksGiven
        every { feedbackRepository.findAll(match { it.receiverId == george.id }) } returns feedbacksReceived

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/core-service/users/${george.id}/user_trust_score")
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """
            {
                "score":5.0,
                "feedbacks":{
                    "given":10,
                    "received":15
                },
                "enrollment_days":0,
                "friends":1
            }
        """.trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { userRepository.findOrFail(any()) }
        verify(exactly = 2) { feedbackRepository.findAll(any()) }
    }

    @Test
    fun `when the user does not exist, then an exception must be thrown`() {
        // Given
        val georgeId = UUID.randomUUID()
        every { userRepository.findOrFail(match { it.id == georgeId }) } throws DynamoDbException.EntityDoesNotExist(
            georgeId.toString()
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/core-service/users/${georgeId}/user_trust_score")
        ).andExpect(MockMvcResultMatchers.status().isNotFound).andReturn().response.contentAsString

        // Then
        response.shouldBe("""{"code":"ENTITY_NOT_EXIST","message":"Entity $georgeId does not exist"}""")
        verify(exactly = 1) { userRepository.findOrFail(any()) }
        verify(exactly = 0) { feedbackRepository.findAll(any()) }
    }

}