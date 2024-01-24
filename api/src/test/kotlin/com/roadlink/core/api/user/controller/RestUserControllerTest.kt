package com.roadlink.core.api.user.controller

import com.ninjasquad.springmockk.MockkBean
import com.roadlink.core.api.command.CommandBusDefinition
import com.roadlink.core.api.error.ExceptionHandlerController
import com.roadlink.core.api.feedback.FeedbackHandlerDefinition
import com.roadlink.core.api.friend.FriendHandlerDefinition
import com.roadlink.core.api.user.UserHandlerDefinition
import com.roadlink.core.api.usertrustscore.UserTrustScoreDefinition
import com.roadlink.core.api.vehicle.VehicleHandlerDefinition
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.vehicle.Vehicle
import com.roadlink.core.domain.vehicle.VehicleCriteria
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.date.before
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders


@WebMvcTest(controllers = [RestUserController::class])
@Import(
    CommandBusDefinition::class,
    UserHandlerDefinition::class,
    FriendHandlerDefinition::class,
    FeedbackHandlerDefinition::class,
    VehicleHandlerDefinition::class,
    UserTrustScoreDefinition::class
)
class RestUserControllerTest(
    @Autowired private val controller: RestUserController,
    @MockkBean private var feedbackRepositoryPort: RepositoryPort<Feedback, FeedbackCriteria>,
    @MockkBean private var friendshipRepositoryPort: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>,
    @MockkBean private var vehicleRepositoryPort: RepositoryPort<Vehicle, VehicleCriteria>,
    @MockkBean private var userRepositoryPort: RepositoryPort<User, UserCriteria>
) : BehaviorSpec({

    val mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(ExceptionHandlerController())
        .build()


    afterTest {
        clearMocks(userRepositoryPort, vehicleRepositoryPort, friendshipRepositoryPort, feedbackRepositoryPort)
    }


    Given("a user controller") {

        val george = UserFactory.common(
            email = "cabrerajjorge@gmail.com",
            firstName = "jorge",
            lastName = "cabrera"
        )
        When("create a user successfully") {
            every { userRepositoryPort.findAll(any()) } returns emptyList()
            every { userRepositoryPort.save(match { it.email == "cabrerajjorge@gmail.com" }) } returns george

            val response = mockMvc.perform(
                MockMvcRequestBuilders.post("/users").content(
                    """{
                        "email": "cabrerajjorge@gmail.com",
                        "first_name": "jorge",
                        "last_name": "cabrera"
                    }""".trimMargin()
                ).contentType(MediaType.APPLICATION_JSON)
            )

            Then("return HTTP status 201") {
                response.andExpect(MockMvcResultMatchers.status().isCreated)
                response.andDo(MockMvcResultHandlers.print())
                    .andExpect(
                        MockMvcResultMatchers.content().json(
                            """{
                                "email": "cabrerajjorge@gmail.com",
                                "first_name": "jorge",
                                "last_name": "cabrera"
                            }""".trimIndent()
                        )
                    )
                verify(exactly = 1) { userRepositoryPort.save(any()) }
                verify(exactly = 1) { userRepositoryPort.findAll(any()) }
            }
        }

        When("the user can not be created because the email is already in registered") {
            every { userRepositoryPort.findAll(any()) } returns listOf(george)

            val response = mockMvc.perform(
                MockMvcRequestBuilders.post("/users").content(
                    """{
                        "email": "cabrerajjorge@gmail.com",
                        "first_name": "jorge",
                        "last_name": "cabrera"
                    }""".trimMargin()
                ).contentType(MediaType.APPLICATION_JSON)
            ).andExpect(MockMvcResultMatchers.status().isConflict)

            Then("return HTTP status 409") {
                verify(exactly = 0) { userRepositoryPort.save(any()) }
                verify(exactly = 1) { userRepositoryPort.findAll(any()) }
            }
        }

        When("the payload does not have an email") {
            val response = mockMvc.perform(
                MockMvcRequestBuilders.post("/users").content(
                    """{
                        "first_name": "jorge",
                        "last_name": "cabrera"
                    }""".trimMargin()
                ).contentType(MediaType.APPLICATION_JSON)
            )

            Then("return HTTP status 400") {
                response.andExpect(MockMvcResultMatchers.status().isBadRequest)
                response.andDo(MockMvcResultHandlers.print())
                    .andExpect(
                        MockMvcResultMatchers.content().json(
                            """{"code":"400 BAD_REQUEST","message":"Invalid request format: could not be parsed to a valid JSON"}""".trimIndent()
                        )
                    )
                verify(exactly = 0) { userRepositoryPort.save(any()) }
                verify(exactly = 0) { userRepositoryPort.findAll(any()) }
            }
        }

    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
