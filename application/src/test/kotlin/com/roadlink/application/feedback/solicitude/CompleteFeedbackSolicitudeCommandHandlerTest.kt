package com.roadlink.application.feedback.solicitude

import com.roadlink.application.user.UserFactory
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude.Status
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitudeCriteria
import com.roadlink.core.domain.feedback.validation.FeedbackSolicitudeException
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*


class CompleteFeedbackSolicitudeCommandHandlerTest : BehaviorSpec({

    Given("a feedback solicitude completion command handler") {
        val feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria> = mockk()
        val feedbackSolicitudeRepository: RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria> = mockk()
        val userRepository: RepositoryPort<User, UserCriteria> = mockk()
        val handler =
            CompleteFeedbackSolicitudeCommandHandler(userRepository, feedbackRepository, feedbackSolicitudeRepository)
        afterEach {
            clearAllMocks()
        }

        When("complete a solicitude successfully") {
            val reviewerId = UUID.randomUUID()
            val receiverId = UUID.randomUUID()
            val tripLegId = UUID.randomUUID()
            val feedbackSolicitudeId = UUID.randomUUID()
            val comment = "ok"
            val rating = 5

            every { userRepository.findOrFail(match { it.id == reviewerId }) } returns UserFactory.common(id = reviewerId)
            every { feedbackSolicitudeRepository.findOrFail(match { it.id == feedbackSolicitudeId }) } returns FeedbackSolicitudeFactory.common(
                id = feedbackSolicitudeId,
                reviewerId = reviewerId,
                receiverId = receiverId,
                tripLegId = tripLegId
            )
            every { feedbackRepository.save(any()) } returns Feedback(
                id = UUID.randomUUID(),
                reviewerId = reviewerId,
                receiverId = receiverId,
                tripLegId = tripLegId,
                rating = rating,
                comment = comment
            )
            every { feedbackSolicitudeRepository.save(match { it.status == Status.COMPLETED && it.id == feedbackSolicitudeId }) } returns FeedbackSolicitude(
                id = feedbackSolicitudeId,
                tripLegId = tripLegId,
                receiverId = receiverId,
                reviewerId = reviewerId,
                status = Status.COMPLETED
            )

            val response = handler.handle(
                CompleteFeedbackSolicitudeCommand(
                    solicitudeCompletion = FeedbackSolicitudeCompletion(
                        reviewerId = reviewerId,
                        feedbackSolicitudeId = feedbackSolicitudeId,
                        comment = comment,
                        rating = rating
                    )
                )
            )

            Then("the response should be the expected") {
                response.solicitude.id.shouldNotBeNull()
                response.solicitude.receiverId.shouldBe(receiverId)
                response.solicitude.reviewerId.shouldBe(reviewerId)
                response.solicitude.tripLegId.shouldBe(tripLegId)
                verify { userRepository.findOrFail(any()) }
                verify { feedbackRepository.save(any()) }
                verify { feedbackSolicitudeRepository.save(any()) }
                verify { feedbackSolicitudeRepository.findOrFail(any()) }
            }
        }

        When("try to complete a solicitude already completed") {
            val reviewerId = UUID.randomUUID()
            val receiverId = UUID.randomUUID()
            val tripLegId = UUID.randomUUID()
            val feedbackSolicitudeId = UUID.randomUUID()
            val comment = "ok"
            val rating = 5

            every { userRepository.findOrFail(match { it.id == reviewerId }) } returns UserFactory.common(id = reviewerId)
            every { feedbackSolicitudeRepository.findOrFail(match { it.id == feedbackSolicitudeId }) } returns FeedbackSolicitudeFactory.common(
                id = feedbackSolicitudeId,
                reviewerId = reviewerId,
                receiverId = receiverId,
                tripLegId = tripLegId,
                status = Status.COMPLETED
            )

            val response = shouldThrow<FeedbackSolicitudeException.FeedbackSolicitudeAlreadyCompleted> {
                handler.handle(
                    CompleteFeedbackSolicitudeCommand(
                        solicitudeCompletion = FeedbackSolicitudeCompletion(
                            reviewerId = reviewerId,
                            feedbackSolicitudeId = feedbackSolicitudeId,
                            comment = comment,
                            rating = rating
                        )
                    )
                )
            }

            Then("the response should be the expected") {
                response.code.shouldBe("PENDING_FEEDBACK_ALREADY_COMPLETED")
                response.message.shouldBe("Pending feedback $feedbackSolicitudeId was already completed")
                verify { userRepository.findOrFail(any()) }
                verify(exactly = 0) { feedbackRepository.save(any()) }
                verify(exactly = 0) { feedbackSolicitudeRepository.save(any()) }
                verify { feedbackSolicitudeRepository.findOrFail(any()) }
            }
        }

        When("try to complete a solicitude already rejected") {
            val reviewerId = UUID.randomUUID()
            val receiverId = UUID.randomUUID()
            val tripLegId = UUID.randomUUID()
            val feedbackSolicitudeId = UUID.randomUUID()
            val comment = "ok"
            val rating = 5

            every { userRepository.findOrFail(match { it.id == reviewerId }) } returns UserFactory.common(id = reviewerId)
            every {
                feedbackSolicitudeRepository.findOrFail(match {
                    it.id == feedbackSolicitudeId
                })
            } returns FeedbackSolicitudeFactory.common(
                id = feedbackSolicitudeId,
                reviewerId = reviewerId,
                receiverId = receiverId,
                tripLegId = tripLegId,
                status = Status.REJECTED
            )

            val response = shouldThrow<FeedbackSolicitudeException.FeedbackSolicitudeAlreadyRejected> {
                handler.handle(
                    CompleteFeedbackSolicitudeCommand(
                        solicitudeCompletion = FeedbackSolicitudeCompletion(
                            reviewerId = reviewerId,
                            feedbackSolicitudeId = feedbackSolicitudeId,
                            comment = comment,
                            rating = rating
                        )
                    )
                )
            }

            Then("the response should be the expected") {
                response.code.shouldBe("PENDING_FEEDBACK_ALREADY_REJECTED")
                response.message.shouldBe("Pending feedback $feedbackSolicitudeId was already rejected")
                verify { userRepository.findOrFail(any()) }
                verify(exactly = 0) { feedbackRepository.save(any()) }
                verify(exactly = 0) { feedbackSolicitudeRepository.save(any()) }
                verify { feedbackSolicitudeRepository.findOrFail(any()) }
            }
        }
    }
})

