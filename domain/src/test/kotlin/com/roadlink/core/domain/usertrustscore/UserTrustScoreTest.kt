package com.roadlink.core.domain.usertrustscore

import com.roadlink.core.domain.feedback.FeedbackRepositoryPort
import com.roadlink.core.domain.feedback.FeedbacksFactory
import com.roadlink.core.domain.user.UserFactory
import com.roadlink.core.domain.user.UserRepositoryPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.lang.RuntimeException
import java.util.*

class UserTrustScoreTest : BehaviorSpec({

    val userRepository = mockk<UserRepositoryPort>()
    val feedbackRepository = mockk<FeedbackRepositoryPort>()

    Given("a user trust score service") {
        When("look for a user trust score that is related to a user which does not exist") {
            val userId = UUID.randomUUID()
            every { userRepository.findOrFail(any()) } throws RuntimeException("User does not exist")

            shouldThrow<RuntimeException> {
                UserTrustScore.get(userId, userRepository, feedbackRepository)
            }

            Then("the feedback repository was not used") {
                verify(exactly = 0) {
                    feedbackRepository.findAll(any())
                }
            }
        }

        When("look for a user trust score but there isn't any feedback saved") {
            val userId = UUID.randomUUID()
            every { userRepository.findOrFail(match { it.id == userId }) } returns UserFactory.old(
                id = userId
            )
            every { feedbackRepository.findAll(any()) } returns listOf()

            val response = UserTrustScore.get(userId, userRepository, feedbackRepository)


            Then("the feedback repository was not used") {
                response.feedbacksGiven.shouldBe(0)
                response.feedbacksReceived.shouldBe(0)
                response.enrollmentDays.shouldBeGreaterThan(360)
                response.score.shouldBe(0.0)
            }
        }

        When("look for the score of an old user with some feedbacks") {
            val oldUserId = UUID.randomUUID()
            every { userRepository.findOrFail(match { it.id == oldUserId }) } returns UserFactory.old(
                id = oldUserId
            )

            every { feedbackRepository.findAll(match { it.receiverId == oldUserId }) } returns listOf(
                FeedbacksFactory.common(receiverId = oldUserId, rating = 5),
                FeedbacksFactory.common(receiverId = oldUserId, rating = 3),
                FeedbacksFactory.common(receiverId = oldUserId, rating = 1),
                FeedbacksFactory.common(receiverId = oldUserId, rating = 5)
            )

            every { feedbackRepository.findAll(match { it.reviewerId == oldUserId }) } returns listOf(
                FeedbacksFactory.common(reviewerId = oldUserId)
            )

            val response = UserTrustScore.get(oldUserId, userRepository, feedbackRepository)
            Then("the score and the feedbacks amount must be the expected") {
                response.score.shouldBe(3.5)
                response.feedbacksReceived.shouldBe(4)
                response.feedbacksGiven.shouldBe(1)
                response.enrollmentDays.shouldBeGreaterThan(360)
            }
        }
    }
})
