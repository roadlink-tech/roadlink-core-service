package com.roadlink.core.api.friend.controller

import com.roadlink.core.api.BaseControllerTest
import com.roadlink.core.api.user.controller.UserFactory
import io.mockk.every
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
        ).andExpect(MockMvcResultMatchers.status().isCreated)
            .andReturn().response.contentAsString

        val friends = objectMapper.readValues(response)
        // Then
        response
    }

}