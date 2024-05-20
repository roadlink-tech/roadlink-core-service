package com.roadlink.core.api.feedback.controller

import com.roadlink.core.api.BaseControllerTest
import com.roadlink.core.api.feedback.FeedbackFactory
import com.roadlink.core.api.friend.FriendshipSolicitudeFactory
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
                    "reviewer_user_compact_display":{
                        "user_id":"${felix.id}",
                        "first_name":"felix",
                        "last_name":"reyero",
                        "profile_photo_url":"https://profile.photo.com",
                        "score":{
                          "type": "NOT_BEEN_SCORED"
                        },
                        "username":"felixreyero"
                    },
                    "friendship_status":"NOT_FRIEND",
                    "trip_leg_id":"${feedback.tripLegId}",
                    "comment":"Sin comentarios",
                    "rating":5
                }
            ]""".trimIndent(),
            response,
            true
        )
    }

    @Test
    fun `when list feedbacks received with a single review and reviewer sent friendship solicitude, then it must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.martin()
        val felix = UserFactory.felix()
        val feedback = FeedbackFactory.common(
            receiverId = george.id,
            reviewerId = felix.id,
        )
        val friendshipSolicitude = FriendshipSolicitudeFactory.common(
            addressedId = martin.id,
            requesterId = felix.id,
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
        }) } returns listOf(friendshipSolicitude)
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
                    "reviewer_user_compact_display":{
                        "user_id":"${felix.id}",
                        "first_name":"felix",
                        "last_name":"reyero",
                        "profile_photo_url":"https://profile.photo.com",
                        "score":{
                          "type": "NOT_BEEN_SCORED"
                        },
                        "username":"felixreyero"
                    },
                    "friendship_status":"PENDING_FRIENDSHIP_SOLICITUDE_RECEIVED",
                    "trip_leg_id":"${feedback.tripLegId}",
                    "comment":"Sin comentarios",
                    "rating":5
                }
            ]""".trimIndent(),
            response,
            true
        )
    }

    @Test
    fun `when list feedbacks received with a single review and reviewer received friendship solicitude, then it must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.martin()
        val felix = UserFactory.felix()
        val feedback = FeedbackFactory.common(
            receiverId = george.id,
            reviewerId = felix.id,
        )
        val friendshipSolicitude = FriendshipSolicitudeFactory.common(
            addressedId = felix.id,
            requesterId = martin.id,
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
        }) } returns listOf(friendshipSolicitude)

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
                    "reviewer_user_compact_display":{
                        "user_id":"${felix.id}",
                        "first_name":"felix",
                        "last_name":"reyero",
                        "profile_photo_url":"https://profile.photo.com",
                        "score":{
                          "type": "NOT_BEEN_SCORED"
                        },
                        "username":"felixreyero"
                    },
                    "friendship_status":"PENDING_FRIENDSHIP_SOLICITUDE_SENT",
                    "trip_leg_id":"${feedback.tripLegId}",
                    "comment":"Sin comentarios",
                    "rating":5
                }
            ]""".trimIndent(),
            response,
            true
        )
    }

    @Test
    fun `when list feedbacks received with a single review and reviewer is friend, then it must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val felix = UserFactory.felix()
        val martin = UserFactory.martin(friends = setOf(felix.id))
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
                    "reviewer_user_compact_display":{
                        "user_id":"${felix.id}",
                        "first_name":"felix",
                        "last_name":"reyero",
                        "profile_photo_url":"https://profile.photo.com",
                        "score":{
                          "type": "NOT_BEEN_SCORED"
                        },
                        "username":"felixreyero"
                    },
                    "friendship_status":"FRIEND",
                    "trip_leg_id":"${feedback.tripLegId}",
                    "comment":"Sin comentarios",
                    "rating":5
                }
            ]""".trimIndent(),
            response,
            true
        )
    }

    @Test
    fun `when list feedbacks received with a single review and reviewer is the caller id, then it must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val martin = UserFactory.martin()
        val feedback = FeedbackFactory.common(
            receiverId = george.id,
            reviewerId = martin.id,
        )

        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { feedbackRepository.findAll(match { it.receiverId == george.id }) } returns listOf(feedback)

        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every { feedbackRepository.findAll(match { it.receiverId == martin.id }) } returns emptyList()
        every { feedbackRepository.findAll(match { it.reviewerId == martin.id }) } returns emptyList()

        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin

        every { friendshipSolicitudeRepository.findAll(match {
            it.addressedId == martin.id
                && it.solicitudeStatus == FriendshipSolicitude.Status.PENDING
        }) } returns emptyList()
        every { friendshipSolicitudeRepository.findAll(match {
            it.addressedId == martin.id
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
                    "reviewer_user_compact_display":{
                        "user_id":"${martin.id}",
                        "first_name":"martin",
                        "last_name":"bosch",
                        "profile_photo_url":"https://profile.photo.com",
                        "score":{
                          "type": "NOT_BEEN_SCORED"
                        },
                        "username":"martinbosch"
                    },
                    "friendship_status":"YOURSELF",
                    "trip_leg_id":"${feedback.tripLegId}",
                    "comment":"Sin comentarios",
                    "rating":5
                }
            ]""".trimIndent(),
            response,
            true
        )
    }
}