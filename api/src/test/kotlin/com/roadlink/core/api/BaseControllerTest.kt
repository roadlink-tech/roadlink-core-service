package com.roadlink.core.api

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
import io.mockk.clearAllMocks
import io.mockk.clearMocks
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@Import(
    CommandBusDefinition::class,
    UserHandlerDefinition::class,
    FriendHandlerDefinition::class,
    FeedbackHandlerDefinition::class,
    VehicleHandlerDefinition::class,
    UserTrustScoreDefinition::class
)
abstract class BaseControllerTest {

    @MockkBean
    lateinit var feedbackRepositoryPort: RepositoryPort<Feedback, FeedbackCriteria>

    @MockkBean
    lateinit var friendshipSolicitudeRepositoryPort: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>

    @MockkBean
    lateinit var vehicleRepositoryPort: RepositoryPort<Vehicle, VehicleCriteria>

    @MockkBean
    lateinit var userRepositoryPort: RepositoryPort<User, UserCriteria>

    lateinit var mockMvc: MockMvc

    abstract fun getControllerUnderTest(): Any

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(getControllerUnderTest())
            .setControllerAdvice(ExceptionHandlerController())
            .build()
    }

    /*    @AfterEach
        fun after() {
            clearMocks(
                userRepositoryPort,
                vehicleRepositoryPort,
                friendshipSolicitudeRepositoryPort,
                feedbackRepositoryPort
            )
        }
    */
}