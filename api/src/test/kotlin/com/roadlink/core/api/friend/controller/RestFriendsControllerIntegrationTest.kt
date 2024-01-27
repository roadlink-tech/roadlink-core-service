package com.roadlink.core.api.friend.controller

import com.roadlink.core.api.BaseControllerTest
import com.roadlink.core.api.user.controller.UserFactory
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [RestFriendsController::class])
class RestFriendsControllerIntegrationTest : BaseControllerTest() {

    @Autowired
    private lateinit var controller: RestFriendsController

    override fun getControllerUnderTest(): Any {
        return controller
    }

    @Test
    fun `when list the user friends, then all of them must be retrieved`() {
        // Given
        val george = UserFactory.withTooManyFriend(amountOfFriends = 40)
        every { userRepositoryPort.findOrFail(match { it.id == george.id }) } returns george

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/users/${george.id}/friends")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val friendsResponse = objectMapper.readValue(response, FriendsResponse::class.java)

        // Then
        friendsResponse.friends.size.shouldBe(40)
        verify(exactly = 1) { userRepositoryPort.findOrFail(any()) }
    }

    @Test
    fun `when list the user friends, but the user does not exist`() {
        // Given
        val george = UserFactory.withTooManyFriend(amountOfFriends = 40)
        every { userRepositoryPort.findOrFail(match { it.id == george.id }) } throws DynamoDbException.EntityDoesNotExist(
            george.id.toString()
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/users/${george.id}/friends")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
            .andReturn().response.contentAsString

        // Then
        response.shouldNotBeNull()
        response.shouldBe("""{"code":"404 NOT_FOUND","message":"Entity ${george.id} does not exist"}""")
        verify(exactly = 1) { userRepositoryPort.findOrFail(any()) }
    }
}