package com.roadlink.core.api.vehicle.controller

import com.roadlink.core.api.BaseControllerTest
import com.roadlink.core.api.user.controller.UserFactory
import com.roadlink.core.api.vehicle.VehicleFactory
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@WebMvcTest(controllers = [RestVehicleController::class])
class RestVehicleControllerIntegrationTest : BaseControllerTest() {

    @Autowired
    private lateinit var controller: RestVehicleController

    override fun getControllerUnderTest(): Any {
        return controller
    }

    /**
     * Create Vehicle
     */
    @Test
    fun `when a user try to add valid vehicle, then it must work well`() {
        // Given
        val george = UserFactory.common()
        val vehicleId = UUID.randomUUID()
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { vehicleRepository.save(match { it.driverId == george.id }) } returns VehicleFactory.common(
            id = vehicleId,
            driverId = george.id
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/core-service/users/${george.id}/vehicles").content(
                """{
                    "brand":"Ford",
                    "model":"TERRITORY",
                    "licence_plate":"AG154AG",
                    "icon_url":"https://icon.com",
                    "capacity":"5",
                    "color":"white"
                }""".trimIndent()
            ).contentType(APPLICATION_JSON)
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
        verify(exactly = 1) { vehicleRepository.save(any()) }
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    @Test
    fun `when a user try to add a vehicle without the brand, then it must work well`() {
        // Given
        val george = UserFactory.common()
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/core-service/users/${george.id}/vehicles").content(
                """{
                    "brand":"",
                    "model":"TERRITORY",
                    "licence_plate":"AG154AG",
                    "icon_url":"https://icon.com",
                    "capacity":"5",
                    "color":"white"
                }""".trimIndent()
            ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{"code":"EMPTY_MANDATORY_FIELDS","message":"The following mandatory fields are empty: [brand]"}"""
        )
        verify(exactly = 0) { vehicleRepository.save(any()) }
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    @Test
    fun `when a user try to add a vehicle without mandatory fields, then it must work well`() {
        // Given
        val george = UserFactory.common()
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/core-service/users/${george.id}/vehicles").content(
                """{
                    "brand":"",
                    "model":"",
                    "licence_plate":"",
                    "icon_url":"https://icon.com",
                    "capacity":"5",
                    "color":"white"
                }""".trimIndent()
            ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{"code":"EMPTY_MANDATORY_FIELDS","message":"The following mandatory fields are empty: [brand, licence_plate, model]"}"""
        )
        verify(exactly = 0) { vehicleRepository.save(any()) }
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    @Test
    fun `when a user try to add valid vehicle, but the capacity is invalid, then a bad request exception must be thrown`() {
        // Given
        val george = UserFactory.common()
        val vehicleId = UUID.randomUUID()
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { vehicleRepository.save(match { it.driverId == george.id }) } returns VehicleFactory.common(
            id = vehicleId,
            driverId = george.id
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/core-service/users/${george.id}/vehicles").content(
                """{
                    "brand":"Ford",
                    "model":"TERRITORY",
                    "licence_plate":"AG154AG",
                    "icon_url":"https://icon.com",
                    "capacity":"50",
                    "color":"white"
                }""".trimIndent()
            ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{"code":"INVALID_CAPACITY","message":"Vehicle Ford TERRITORY must have a capacity between 0 and 5"}"""
        )
        verify(exactly = 0) { vehicleRepository.save(any()) }
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    @Test
    fun `when a user try to add valid vehicle, but the user does not exist, then a bad request exception must be thrown`() {
        // Given
        val userId = UUID.randomUUID()
        every { userRepository.findOrFail(match { it.id == userId }) } throws DynamoDbException.EntityDoesNotExist(
            userId.toString()
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/core-service/users/${userId}/vehicles").content(
                """{
                    "brand":"Ford",
                    "model":"TERRITORY",
                    "licence_plate":"AG154AG",
                    "icon_url":"https://icon.com",
                    "capacity":"5",
                    "color":"white"
                }""".trimIndent()
            ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound).andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{"code":"ENTITY_NOT_EXIST","message":"Entity $userId does not exist"}"""
        )
        verify(exactly = 0) { vehicleRepository.save(any()) }
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    @Test
    fun `when a user try to add valid vehicle, but the brand does not exist, then a bad request exception must be thrown`() {
        // Given
        val george = UserFactory.common()
        val vehicleId = UUID.randomUUID()
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { vehicleRepository.save(match { it.driverId == george.id }) } returns VehicleFactory.common(
            id = vehicleId,
            driverId = george.id
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/core-service/users/${george.id}/vehicles").content(
                """{
                    "brand":"Pagani",
                    "model":"Huayra",
                    "licence_plate":"AG154AG",
                    "icon_url":"https://icon.com",
                    "capacity":"5",
                    "color":"white"
                }""".trimIndent()
            ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{"code":"INVALID_BRAND","message":"The brand Pagani is not available"}"""
        )
        verify(exactly = 0) { vehicleRepository.save(any()) }
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    /**
     * List Vehicles
     */
    @Test
    fun `when list all the user vehicles then it must be retrieved`() {
        // Given
        val george = UserFactory.common()
        val vehicle = VehicleFactory.common()
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { vehicleRepository.findAll(match { it.driverId == george.id }) } returns listOf(
            vehicle
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/core-service/users/${george.id}/vehicles")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """
            [
                {
                    "id": "${vehicle.id}",
                    "brand": "Ford",
                    "model": "Territory",
                    "licence_plate": "AG154AG",
                    "icon_url": "https://icon.com",
                    "capacity": 5,
                    "color": "white"
                }
            ]
        """.trimIndent().replace(Regex("\\s+"), "")
        )
    }

    @Test
    fun `when list user vehicles, but the user does not exist, then an exception must be thrown`() {
        // Given
        val georgeId = UUID.randomUUID()
        every { userRepository.findOrFail(match { it.id == georgeId }) } throws DynamoDbException.EntityDoesNotExist(
            georgeId.toString()
        )


        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/core-service/users/${georgeId}/vehicles")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """{"code":"ENTITY_NOT_EXIST","message":"Entity $georgeId does not exist"}"""
        )
    }

    /**
     * Delete Vehicles
     */
    @Test
    fun `when delete an existing vehicle of a user, then it must be remove`() {
        // Given
        val george = UserFactory.common()
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        val vehicleId = UUID.randomUUID()
        every { vehicleRepository.delete(match { it.id == vehicleId }) } just runs

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.delete("/core-service/users/${george.id}/vehicles/${vehicleId}")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Return
        verify(exactly = 1) { userRepository.findOrFail(any()) }
        verify(exactly = 1) { vehicleRepository.delete(any()) }
    }

    @Test
    fun `when try to delete an existing vehicle but the user does not exist, then an exception must be thrown`() {
        // Given
        val georgeId = UUID.randomUUID()
        every { userRepository.findOrFail(match { it.id == georgeId }) } throws DynamoDbException.EntityDoesNotExist(
            georgeId.toString()
        )
        val vehicleId = UUID.randomUUID()

        // When
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/core-service/users/${georgeId}/vehicles/${vehicleId}")
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
            .andReturn().response.contentAsString

        // Return
        verify(exactly = 1) { userRepository.findOrFail(any()) }
        verify(exactly = 0) { vehicleRepository.delete(any()) }
    }

    /**
     * Patch Vehicle
     */
    @Test
    fun `when patch an existing vehicle, then it must be updated`() {
        // Given
        val george = UserFactory.common()
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        val vehicleId = UUID.randomUUID()
        val vehicle = VehicleFactory.common(id = vehicleId)
        every { vehicleRepository.findOrFail(match { it.id == vehicleId }) } returns vehicle
        every { vehicleRepository.save(match { it.id == vehicleId }) } returns vehicle.copy(
            brand = "Ford",
            model = "Bronco",
            licencePlate = "AG123AG",
            iconUrl = "https://ford.bronco.com",
            capacity = 2,
            color = "black"
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.patch("/core-service/users/${george.id}/vehicles/${vehicleId}").content(
                """{
                    "brand":"Ford",
                    "model":"Bronco",
                    "licence_plate":"AG123AG",
                    "icon_url":"https://ford.bronco.com",
                    "capacity":"2",
                    "color":"black"
                }""".trimIndent()
            ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Return
        response.shouldBe(
            """
            {
              "id": "$vehicleId",
              "brand": "Ford",
              "model": "Bronco",
              "licence_plate": "AG123AG",
              "icon_url": "https://ford.bronco.com",
              "capacity": 2,
              "color": "black"
            }
        """.trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { userRepository.findOrFail(any()) }
        verify(exactly = 1) { vehicleRepository.findOrFail(any()) }
        verify(exactly = 1) { vehicleRepository.save(any()) }
    }

    @Test
    fun `when patch an existing vehicle with an invalid brand, then it must thrown an error`() {
        // Given
        val george = UserFactory.common()
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        val vehicleId = UUID.randomUUID()
        val vehicle = VehicleFactory.common(id = vehicleId)
        every { vehicleRepository.findOrFail(match { it.id == vehicleId }) } returns vehicle

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.patch("/core-service/users/${george.id}/vehicles/${vehicleId}").content(
                """{
                    "brand":"Frutelli"
                }""".trimIndent()
            ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn().response.contentAsString

        // Return
        response.shouldBe("""{"code":"INVALID_BRAND","message":"The brand Frutelli is not available"}""")
        verify(exactly = 1) { userRepository.findOrFail(any()) }
        verify(exactly = 1) { vehicleRepository.findOrFail(any()) }
        verify(exactly = 0) { vehicleRepository.save(any()) }
    }

    @Test
    fun `when patch an existing vehicle with the capacity, then it must work ok`() {
        // Given
        val george = UserFactory.common()
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        val vehicleId = UUID.randomUUID()
        val vehicle = VehicleFactory.common(id = vehicleId)
        every { vehicleRepository.findOrFail(match { it.id == vehicleId }) } returns vehicle
        every { vehicleRepository.save(match { it.id == vehicleId }) } returns vehicle.copy(capacity = 2)

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.patch("/core-service/users/${george.id}/vehicles/${vehicleId}").content(
                """{
                    "capacity":2
                }""".trimIndent()
            ).contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Return
        response.shouldBe(
            """
            {
              "id": "$vehicleId",
              "brand": "Ford",
              "model": "Territory",
              "licence_plate": "AG154AG",
              "icon_url": "https://icon.com",
              "capacity": 2,
              "color": "white"
            }
        """.trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { userRepository.findOrFail(any()) }
        verify(exactly = 1) { vehicleRepository.findOrFail(any()) }
        verify(exactly = 1) { vehicleRepository.save(any()) }
    }

}