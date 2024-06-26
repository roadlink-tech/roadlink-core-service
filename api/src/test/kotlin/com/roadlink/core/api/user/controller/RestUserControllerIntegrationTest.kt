package com.roadlink.core.api.user.controller

import com.roadlink.core.api.BaseControllerTest
import io.kotest.matchers.nulls.shouldNotBeNull
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

@WebMvcTest(controllers = [RestUserController::class])
class RestUserControllerIntegrationTest : BaseControllerTest() {

    @Autowired
    private lateinit var controller: RestUserController

    private val george = UserFactory.common(
        email = "cabrerajjorge@gmail.com", firstName = "jorge", lastName = "cabrera"
    )

    /**
     * Create User
     */
    @Test
    fun `when the request does not contain an email, then a bad request must be retrieved`() {
        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/core-service/users").content(
                """{
                        "first_name": "jorge",
                        "last_name": "cabrera"
                    }""".trimMargin()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe("""{"code":"INVALID_JSON","message":"Invalid request format: could not be parsed to a valid JSON"}""")
        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { userRepository.findAll(any()) }
    }

    @Test
    fun `when the user can be created succeeded, then it must be saved and a 201 response must be retrieved`() {
        // Given
        every { userRepository.findAll(any()) } returns emptyList()
        every { userRepository.findOrNull(match { it.userName == "jorgecabrera" }) } returns null
        every { userRepository.save(match { it.email == "cabrerajjorge@gmail.com" }) } returns george

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/core-service/users").content(
                """{
                        "email": "cabrerajjorge@gmail.com",
                        "first_name": "jorge",
                        "last_name": "cabrera",
                        "birth_day": "06/12/1991",
                        "profile_photo_url": "https://profile.photo.com",
                        "gender": "male"
                    }""".trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isCreated).andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """
            {
                "id":"${george.id}",
                "email":"cabrerajjorge@gmail.com",
                "first_name":"jorge",
                "last_name":"cabrera",
                "gender":"male",
                "profile_photo_url":"https://profile.photo.com",
                "birth_day":"06/12/1991",
                "user_name":"jorgecabrera",
                "friends":[]
            }
            """.trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { userRepository.save(any()) }
        verify(exactly = 1) { userRepository.findAll(any()) }
        verify { userRepository.findOrNull(any()) }
    }

    @Test
    fun `when the user can be created succeeded, but the user name must contain a dot, then it must be saved and a 201 response must be retrieved`() {
        // Given
        val jorgecabrera = UserFactory.common(userName = "jorgecabrera")
        every { userRepository.findAll(any()) } returns emptyList()
        every { userRepository.findOrNull(match { it.userName == "jorgecabrera" }) } returns jorgecabrera
        every { userRepository.findOrNull(match { it.userName == "jorge.cabrera" }) } returns null
        every { userRepository.save(match { it.email == "cabrerajjorge@gmail.com" }) } returns george.copy(
            userName = "jorge.cabrera"
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/core-service/users").content(
                """{
                        "email": "cabrerajjorge@gmail.com",
                        "first_name": "jorge",
                        "last_name": "cabrera",
                        "birth_day": "06/12/1991",
                        "profile_photo_url": "https://profile.photo.com",
                        "gender": "male"
                    }""".trimIndent()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isCreated).andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """
            {
                "id":"${george.id}",
                "email":"cabrerajjorge@gmail.com",
                "first_name":"jorge",
                "last_name":"cabrera",
                "gender":"male",
                "profile_photo_url":"https://profile.photo.com",
                "birth_day":"06/12/1991",
                "user_name":"jorge.cabrera",
                "friends":[]
            }
            """.trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { userRepository.save(any()) }
        verify(exactly = 1) { userRepository.findAll(any()) }
        verify { userRepository.findOrNull(any()) }
    }

    @Test
    fun `when try to create a user with an email already registered, then a conflict error must be retrieved`() {
        // Given
        every { userRepository.findAll(any()) } returns listOf(george)

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/core-service/users").content(
                """{
                        "email": "cabrerajjorge@gmail.com",
                        "first_name": "jorge",
                        "last_name": "cabrera",
                        "birth_day": "06/12/1991",
                        "profile_photo_url": "https://profile.photo.com",
                        "gender": "male"
                    }""".trimMargin()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isConflict).andReturn().response.contentAsString

        // Then
        response.shouldNotBeNull()
        response.shouldBe("""{"code":"USER_EMAIL_ALREADY_REGISTERED","message":"User cabrerajjorge@gmail.com is already registered"}""")
        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 1) { userRepository.findAll(any()) }
    }

    @Test
    fun `when try to create a user but the repository works badly, then a 500 error must be retrieved`() {
        // Given
        every { userRepository.findAll(any()) } throws RuntimeException()

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/core-service/users").content(
                """{
                        "email": "cabrerajjorge@gmail.com",
                        "first_name": "jorge",
                        "last_name": "cabrera",
                        "birth_day": "06/12/1991",
                        "profile_photo_url": "https://profile.photo.com",
                        "gender": "male"
                    }""".trimMargin()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isInternalServerError)
            .andReturn().response.contentAsString

        // Then
        response.shouldNotBeNull()
        response.shouldBe("""{"code":"INTERNAL_SERVER_ERROR","message":"Oops, something wrong happened"}""")
        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 1) { userRepository.findAll(any()) }

    }

    /**
     * Patch User
     */
    @Test
    fun `when patch the first and last name of an existing user, then it must work`() {
        // Given
        val userId = UUID.randomUUID()
        val martinbosch = UserFactory.common(id = userId, firstName = "martin", lastName = "bosch")
        every { userRepository.findOrFail(match { it.id == userId }) } returns martinbosch
        every { userRepository.save(match { it.id == userId }) } returns martinbosch.copy(
            firstName = "jorge", lastName = "cabrera"
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.patch("/core-service/users/$userId").content(
                """{
                        "first_name": "jorge",
                        "last_name": "cabrera"
                    }""".trimMargin()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """
            {
                "id":"$userId",
                "email":"cabrerajjorge@gmail.com",
                "first_name":"jorge",
                "last_name":"cabrera",
                "gender":"male",
                "profile_photo_url":"https://profile.photo.com",
                "birth_day":"06/12/1991",
                "user_name":"jorgecabrera",
                "friends":[]
            }
            """.trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { userRepository.save(any()) }
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    @Test
    fun `when patch a user with a new email which is not being used, then it must work`() {
        // Given
        every { userRepository.findOrNull(match { it.email == "martin.bosch@roadlink.com" }) } returns null
        every { userRepository.findOrFail(match { it.id == george.id }) } returns george
        every { userRepository.save(match { it.id == george.id }) } returns george.copy(
            firstName = "martin",
            lastName = "bosch",
            email = "martin.bosch@roadlink.com",
            profilePhotoUrl = "http://martin.photo.url",
            gender = "M",
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.patch("/core-service/users/${george.id}").content(
                """{
                        "first_name": "martin",
                        "last_name": "bosh",
                        "email": "martin.bosch@roadlink.com",
                        "profile_photo_url": "http://martin.photo.url",
                        "gender": "M"
                    }""".trimMargin()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """
            {
                "id":"${george.id}",
                "email":"martin.bosch@roadlink.com",
                "first_name":"martin",
                "last_name":"bosch",
                "gender":"M",
                "profile_photo_url":"http://martin.photo.url",
                "birth_day":"06/12/1991",
                "user_name":"jorgecabrera",
                "friends":[]
            }
            """.trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { userRepository.save(any()) }
        verify(exactly = 1) { userRepository.findOrFail(any()) }
        verify(exactly = 1) { userRepository.findOrNull(any()) }
    }

    @Test
    fun `when patch a user with a new email but it is being used, then an exception must be raised`() {
        // Given
        every { userRepository.findOrNull(match { it.email == "martin.bosch@roadlink.com" }) } returns UserFactory.common()

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.patch("/core-service/users/${george.id}").content(
                """{
                        "first_name": "martin",
                        "last_name": "bosh",
                        "email": "martin.bosch@roadlink.com",
                        "profile_photo_url": "http://martin.photo.url",
                        "gender": "M"
                    }""".trimMargin()
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isConflict)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe("""{"code":"USER_EMAIL_ALREADY_REGISTERED","message":"User martin.bosch@roadlink.com is already registered"}""")
        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { userRepository.findOrFail(any()) }
        verify(exactly = 1) { userRepository.findOrNull(any()) }
    }

    /**
     * Search user
     */
    @Test
    fun `when search a user by username an it exist, then it must be retrieved`() {
        // Given
        val userId = UUID.randomUUID()
        every { userRepository.findOrFail(match { it.userName == "jorgecabrera" }) } returns UserFactory.common(
            id = userId
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/core-service/users/search?user_name=jorgecabrera")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """
            {
                "id":"$userId",
                "email":"cabrerajjorge@gmail.com",
                "first_name":"jorge",
                "last_name":"cabrera",
                "gender":"male",
                "profile_photo_url":"https://profile.photo.com",
                "birth_day":"06/12/1991",
                "user_name":"jorgecabrera",
                "friends":[]
            }""".trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    @Test
    fun `when search a user by email an it exist, then it must be retrieved`() {
        // Given
        val userId = UUID.randomUUID()
        every { userRepository.findOrFail(match { it.email == "cabrerajjorge@gmail.com" }) } returns UserFactory.common(
            id = userId
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/core-service/users/search?email=cabrerajjorge@gmail.com")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """
            {
                "id":"$userId",
                "email":"cabrerajjorge@gmail.com",
                "first_name":"jorge",
                "last_name":"cabrera",
                "gender":"male",
                "profile_photo_url":"https://profile.photo.com",
                "birth_day":"06/12/1991",
                "user_name":"jorgecabrera",
                "friends":[]
            }""".trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    @Test
    fun `when search a user by email and user name and it exist, then it must be retrieved`() {
        // Given
        val userId = UUID.randomUUID()
        every { userRepository.findOrFail(match { it.email == "cabrerajjorge@gmail.com" && it.userName == "jorgecabrera" }) } returns UserFactory.common(
            id = userId
        )

        // When
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/core-service/users/search?email=cabrerajjorge@gmail.com&user_name=jorgecabrera")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        // Then
        response.shouldBe(
            """
            {
                "id":"$userId",
                "email":"cabrerajjorge@gmail.com",
                "first_name":"jorge",
                "last_name":"cabrera",
                "gender":"male",
                "profile_photo_url":"https://profile.photo.com",
                "birth_day":"06/12/1991",
                "user_name":"jorgecabrera",
                "friends":[]
            }""".trimIndent().replace(Regex("\\s+"), "")
        )
        verify(exactly = 1) { userRepository.findOrFail(any()) }
    }

    override fun getControllerUnderTest(): Any {
        return this.controller
    }


}