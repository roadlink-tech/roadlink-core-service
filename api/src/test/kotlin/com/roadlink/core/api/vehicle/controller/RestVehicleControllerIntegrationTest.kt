package com.roadlink.core.api.vehicle.controller

import com.roadlink.core.api.BaseControllerTest
import com.roadlink.core.api.user.controller.UserFactory
import com.roadlink.core.api.vehicle.VehicleFactory
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
import java.util.UUID

@WebMvcTest(controllers = [RestVehicleController::class])
class RestVehicleControllerIntegrationTest : BaseControllerTest() {

    @Autowired
    private lateinit var controller: RestVehicleController

    override fun getControllerUnderTest(): Any {
        return controller
    }

    @Test
    fun `when a user try to add valid vehicle, then it must work well`() {
        // Given
        val george = UserFactory.common()
        val vehicleId = UUID.randomUUID()
        every { userRepositoryPort.findOrFail(match { it.id == george.id }) } returns george
        every { vehicleRepositoryPort.save(match { it.driverId == george.id }) } returns VehicleFactory.common(
            id = vehicleId,
            driverId = george.id
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${george.id}/vehicles").content(
                """{
                    "brand":"Ford",
                    "model":"TERRITORY",
                    "licence_plate":"AG154AG",
                    "icon_url":"https://icon.com",
                    "capacity":"5",
                    "color":"white"
                }""".trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isCreated).andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{
                "id":"$vehicleId",
                "brand":"Ford",
                "model":"Territory",
                "licence_plate":"AG154AG",
                "icon_url":"https://icon.com",
                "capacity":5,
                "color":"white"
            }""".trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { vehicleRepositoryPort.save(any()) }
        verify(exactly = 1) { userRepositoryPort.findOrFail(any()) }
    }

    @Test
    fun `when a user try to add a vehicle without the brand, then it must work well`() {
        // Given
        val george = UserFactory.common()
        every { userRepositoryPort.findOrFail(match { it.id == george.id }) } returns george

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${george.id}/vehicles").content(
                """{
                    "brand":"",
                    "model":"TERRITORY",
                    "licence_plate":"AG154AG",
                    "icon_url":"https://icon.com",
                    "capacity":"5",
                    "color":"white"
                }""".trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{"code":"400 BAD_REQUEST","message":"The following mandatory fields are empty: [brand]"}"""
        )
        verify(exactly = 0) { vehicleRepositoryPort.save(any()) }
        verify(exactly = 1) { userRepositoryPort.findOrFail(any()) }
    }

    @Test
    fun `when a user try to add a vehicle without mandatory fields, then it must work well`() {
        // Given
        val george = UserFactory.common()
        every { userRepositoryPort.findOrFail(match { it.id == george.id }) } returns george

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${george.id}/vehicles").content(
                """{
                    "brand":"",
                    "model":"",
                    "licence_plate":"",
                    "icon_url":"https://icon.com",
                    "capacity":"5",
                    "color":"white"
                }""".trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{"code":"400 BAD_REQUEST","message":"The following mandatory fields are empty: [brand, licence_plate, model]"}"""
        )
        verify(exactly = 0) { vehicleRepositoryPort.save(any()) }
        verify(exactly = 1) { userRepositoryPort.findOrFail(any()) }
    }

    @Test
    fun `when a user try to add valid vehicle, but the capacity is invalid, then a bad request exception must be thrown`() {
        // Given
        val george = UserFactory.common()
        val vehicleId = UUID.randomUUID()
        every { userRepositoryPort.findOrFail(match { it.id == george.id }) } returns george
        every { vehicleRepositoryPort.save(match { it.driverId == george.id }) } returns VehicleFactory.common(
            id = vehicleId,
            driverId = george.id
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${george.id}/vehicles").content(
                """{
                    "brand":"Ford",
                    "model":"TERRITORY",
                    "licence_plate":"AG154AG",
                    "icon_url":"https://icon.com",
                    "capacity":"50",
                    "color":"white"
                }""".trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{"code":"400 BAD_REQUEST","message":"Vehicle Ford TERRITORY must have a capacity between 0 and 5"}"""
        )
        verify(exactly = 0) { vehicleRepositoryPort.save(any()) }
        verify(exactly = 1) { userRepositoryPort.findOrFail(any()) }
    }

    @Test
    fun `when a user try to add valid vehicle, but the user does not exist, then a bad request exception must be thrown`() {
        // Given
        val userId = UUID.randomUUID()
        every { userRepositoryPort.findOrFail(match { it.id == userId }) } throws DynamoDbException.EntityDoesNotExist(
            userId.toString()
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${userId}/vehicles").content(
                """{
                    "brand":"Ford",
                    "model":"TERRITORY",
                    "licence_plate":"AG154AG",
                    "icon_url":"https://icon.com",
                    "capacity":"5",
                    "color":"white"
                }""".trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound).andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{"code":"404 NOT_FOUND","message":"Entity $userId does not exist"}"""
        )
        verify(exactly = 0) { vehicleRepositoryPort.save(any()) }
        verify(exactly = 1) { userRepositoryPort.findOrFail(any()) }
    }

    @Test
    fun `when a user try to add valid vehicle, but the brand does not exist, then a bad request exception must be thrown`() {
        // Given
        val george = UserFactory.common()
        val vehicleId = UUID.randomUUID()
        every { userRepositoryPort.findOrFail(match { it.id == george.id }) } returns george
        every { vehicleRepositoryPort.save(match { it.driverId == george.id }) } returns VehicleFactory.common(
            id = vehicleId,
            driverId = george.id
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${george.id}/vehicles").content(
                """{
                    "brand":"Pagani",
                    "model":"Huayra",
                    "licence_plate":"AG154AG",
                    "icon_url":"https://icon.com",
                    "capacity":"5",
                    "color":"white"
                }""".trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{"code":"400 BAD_REQUEST","message":"The brand Pagani is not available"}"""
        )
        verify(exactly = 0) { vehicleRepositoryPort.save(any()) }
        verify(exactly = 1) { userRepositoryPort.findOrFail(any()) }
    }
}