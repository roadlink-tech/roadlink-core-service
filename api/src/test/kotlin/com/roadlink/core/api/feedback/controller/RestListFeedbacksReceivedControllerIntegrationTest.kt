package com.roadlink.core.api.feedback.controller

import com.roadlink.core.api.BaseControllerTest
import com.roadlink.core.api.feedback.FeedbackFactory
import com.roadlink.core.api.user.controller.UserFactory
import com.roadlink.core.domain.friend.FriendshipSolicitude
import io.mockk.every
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [RestListFeedbacksReceivedController::class])
class RestListFeedbacksReceivedControllerIntegrationTest : BaseControllerTest() {

    @Autowired
    private lateinit var controller: RestListFeedbacksReceivedController

    override fun getControllerUnderTest(): Any {
        return controller
    }

    @Test
    fun `when list feedbacks received and there are none, then it must return an empty list`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.common()

        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { feedbackRepository.findAll(any()) } returns emptyList()

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/users/${george.id}/feedbacks_received")
                .header("X-Caller-Id", "${martin.id}")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Then
        JSONAssert.assertEquals("[]", response, true)
    }

    @Test
    fun `when list feedbacks received with a single review, then it must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.martin()
        val felix = UserFactory.felix()
        val feedback = FeedbackFactory.common(
            receiverId = george.id,
            reviewerId = felix.id,
        )

        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { feedbackRepository.findAll(match { it.receiverId == george.id }) } returns listOf(feedback)

        every { userRepository.findOrFail(match { it.id == felix.id }) } returns felix
        every { feedbackRepository.findAll(match { it.receiverId == felix.id }) } returns emptyList()
        every { feedbackRepository.findAll(match { it.reviewerId == felix.id }) } returns emptyList()

        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin

        every { friendshipSolicitudeRepository.findAll(match {
            it.addressedId == martin.id
                && it.solicitudeStatus == FriendshipSolicitude.Status.PENDING
        }) } returns emptyList()
        every { friendshipSolicitudeRepository.findAll(match {
            it.addressedId == felix.id
                && it.solicitudeStatus == FriendshipSolicitude.Status.PENDING
        }) } returns emptyList()

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/users/${george.id}/feedbacks_received")
                .header("X-Caller-Id", "${martin.id}")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Then
        JSONAssert.assertEquals(
            """[
                {
                    "id":"${feedback.id}",
                    "reviewerUserCompactDisplay":{
                        "userId":"${felix.id}",
                        "firstName":"felix",
                        "lastName":"reyero",
                        "profilePhotoUrl":"https://profile.photo.com",
                        "score":{
                          "type": "NOT_BEEN_SCORED"
                        },
                        "username":"felixreyero"
                    },
                    "friendshipStatus":"NOT_FRIEND",
                    "tripLegId":"${feedback.tripLegId}",
                    "comment":"Sin comentarios",
                    "rating":5
                }
            ]""".trimIndent(),
            response,
            true
        )
    }

}