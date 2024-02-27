package com.roadlink.core.api.friend.controller

import com.roadlink.core.api.BaseControllerTest
import com.roadlink.core.api.user.controller.UserFactory
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [RestFriendsController::class])
class RestFriendsControllerIntegrationTest : BaseControllerTest() {

    @Autowired
    private lateinit var controller: RestFriendsController

    override fun getControllerUnderTest(): Any {
        return controller
    }

    /**
     * List friends use case
     */
    @Test
    fun `when list the user friends, then all of them must be retrieved`() {
        // Given
        val george = UserFactory.withTooManyFriend(amountOfFriends = 40)
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/users/${george.id}/friends")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        val friendsResponse = objectMapper.readValue(response, FriendsResponse::class.java)

        // Then
        friendsResponse.friends.size.shouldBe(40)
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    @Test
    fun `when list the user friends, but the user does not exist`() {
        // Given
        val george = UserFactory.withTooManyFriend(amountOfFriends = 40)
        every { userRepository.findOrFail(match { it.id == george.id }) } throws DynamoDbException.EntityDoesNotExist(
            george.id.toString()
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/users/${george.id}/friends")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
            .andReturn().response.contentAsString

        // Then
        response.shouldNotBeNull()
        response.shouldBe("""{"code":"ENTITY_NOT_EXIST","message":"Entity ${george.id} does not exist"}""")
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    /**
     * Delete friend use case
     */
    @Test
    fun `when delete a friend, then the deleted friend must no be returned`() {
        // Given
        val george = UserFactory.common(firstName = "jorge")
        val martin = UserFactory.common(firstName = "martin")
        val felix = UserFactory.common(firstName = "felix")

        george.beFriendOf(martin)
        george.beFriendOf(felix)

        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { userRepository.saveAll(any()) } returns emptyList()

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.delete("/users/${george.id}/friends/${martin.id}")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Then
        val friendsResponse = objectMapper.readValue(response, FriendsResponse::class.java)
        friendsResponse.friends.shouldContain(felix.id)
        verify(exactly = 2) { userRepository.findOrFail(any()) }
        verify(exactly = 1) { userRepository.saveAll(any()) }
    }

    @Test
    fun `when delete the single friend which user has, then an empty list friend must be returned`() {
        // Given
        val george = UserFactory.common(firstName = "jorge")
        val martin = UserFactory.common(firstName = "martin")

        george.beFriendOf(martin)

        every { userRepository.findOrFail(match { it.id == martin.id }) } returns martin
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { userRepository.saveAll(any()) } returns emptyList()

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.delete("/users/${george.id}/friends/${martin.id}")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Then
        val friendsResponse = objectMapper.readValue(response, FriendsResponse::class.java)
        friendsResponse.friends.shouldBeEmpty()
        verify(exactly = 2) { userRepository.findOrFail(any()) }
        verify(exactly = 1) { userRepository.saveAll(any()) }
    }
}