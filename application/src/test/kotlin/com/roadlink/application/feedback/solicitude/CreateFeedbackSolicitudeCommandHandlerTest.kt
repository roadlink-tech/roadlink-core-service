package com.roadlink.application.feedback.solicitude

import com.roadlink.application.user.UserFactory
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude.*
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitudeCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class CreateFeedbackSolicitudeCommandHandlerTest : BehaviorSpec({


    Given("a create feedback solicitude command handler") {
        val feedbackSolicitudeRepository: RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria> = mockk()
        val userRepository: RepositoryPort<User, UserCriteria> = mockk()
        val handler =
            CreateFeedbackSolicitudeCommandHandler(userRepository, feedbackSolicitudeRepository)

        afterEach {
            clearAllMocks()
        }

        When("create a feedback solicitude successfully") {
            val feedbackId = UUID.randomUUID()
            val reviewerId = UUID.randomUUID()
            val receiverId = UUID.randomUUID()
            val tripLegId = UUID.randomUUID()

            every { userRepository.findOrFail(match { it.id == reviewerId || it.id == receiverId }) } returns UserFactory.common(
                id = reviewerId
            )
            every {
                feedbackSolicitudeRepository.save(match {
                    it.receiverId == receiverId &&
                            it.reviewerId == reviewerId &&
                            it.status == Status.PENDING
                })
            } returns FeedbackSolicitudeFactory.common(
                receiverId = receiverId,
                reviewerId = reviewerId,
                tripLegId = tripLegId,
                status = Status.PENDING
            )

            val response = handler.handle(
                command = CreateFeedbackSolicitudeCommand(
                    solicitude = FeedbackSolicitudeDTO(
                        id = feedbackId,
                        reviewerId = reviewerId,
                        receiverId = receiverId,
                        tripLegId = tripLegId
                    )
                )
            )

            Then("the response should be the expected") {
                response.solicitude.reviewerId.shouldBe(reviewerId)
                response.solicitude.receiverId.shouldBe(receiverId)
                response.solicitude.tripLegId.shouldBe(tripLegId)
                response.solicitude.status.shouldBe(Status.PENDING)
                verify(exactly = 2) { userRepository.findOrFail(any()) }
                verify { feedbackSolicitudeRepository.save(any()) }
            }
        }
    }
})
