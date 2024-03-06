package com.roadlink.core.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.roadlink.core.api.command.CommandBusDefinition
import com.roadlink.core.api.error.ExceptionHandlerController
import com.roadlink.core.api.feedback.FeedbackHandlerDefinition
import com.roadlink.core.api.friend.FriendHandlerDefinition
import com.roadlink.core.api.user.GoogleLoginDefinition
import com.roadlink.core.api.user.UserHandlerDefinition
import com.roadlink.core.api.usertrustscore.UserTrustScoreDefinition
import com.roadlink.core.api.vehicle.VehicleHandlerDefinition
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitudeCriteria
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.google.GoogleIdTokenValidator
import com.roadlink.core.domain.user.google.GoogleUser
import com.roadlink.core.domain.user.google.GoogleUserCriteria
import com.roadlink.core.domain.vehicle.Vehicle
import com.roadlink.core.domain.vehicle.VehicleCriteria
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@Import(
    CommandBusDefinition::class,
    UserHandlerDefinition::class,
    FriendHandlerDefinition::class,
    FeedbackHandlerDefinition::class,
    VehicleHandlerDefinition::class,
    UserTrustScoreDefinition::class,
    GoogleLoginDefinition::class
)
abstract class BaseControllerTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria>

    @MockkBean
    lateinit var feedbackSolicitudeRepository: RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria>

    @MockkBean
    lateinit var friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>

    @MockkBean
    lateinit var vehicleRepository: RepositoryPort<Vehicle, VehicleCriteria>

    @MockkBean
    lateinit var userRepository: RepositoryPort<User, UserCriteria>

    @MockkBean
    lateinit var googleUserRepository: RepositoryPort<GoogleUser, GoogleUserCriteria>

    @MockkBean
    lateinit var googleIdTokenValidator: GoogleIdTokenValidator

    lateinit var mockMvc: MockMvc

    abstract fun getControllerUnderTest(): Any

    @BeforeEach
    fun setUp() {
        val converter = MappingJackson2HttpMessageConverter(objectMapper)
        mockMvc = MockMvcBuilders
            .standaloneSetup(getControllerUnderTest())
            .setMessageConverters(converter)
            .setControllerAdvice(ExceptionHandlerController())
            .build()
    }
}