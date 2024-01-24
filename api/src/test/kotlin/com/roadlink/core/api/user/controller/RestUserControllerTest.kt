package com.roadlink.core.api.user.controller

import com.ninjasquad.springmockk.MockkBean
import com.roadlink.application.command.CommandBus
import com.roadlink.core.api.command.CommandBusDefinition
import com.roadlink.core.api.feedback.FeedbackHandlerDefinition
import com.roadlink.core.api.friend.FriendHandlerDefinition
import com.roadlink.core.api.user.UserHandlerDefinition
import com.roadlink.core.api.usertrustscore.UserTrustScoreDefinition
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@WebMvcTest(RestUserController::class)
@Import(
    CommandBusDefinition::class,
    UserHandlerDefinition::class,
    FriendHandlerDefinition::class,
    FeedbackHandlerDefinition::class,
    UserTrustScoreDefinition::class
)
class RestUserControllerTest(
    private val mockMvc: MockMvc,
    private var commandBus: CommandBus,
    @MockkBean private var feedbackRepositoryPort: RepositoryPort<Feedback, FeedbackCriteria>,
    @MockkBean private var friendshipRepositoryPort: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>,
    @MockkBean private var userRepositoryPort: RepositoryPort<User, UserCriteria>
) :
    BehaviorSpec({

        Given("a user controller") {
            When("create a user successfully") {
                every { userRepositoryPort.findAll(any()) } returns emptyList()

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
                }
            }

        }
    }) {
    override fun extensions() = listOf(SpringExtension)
}
