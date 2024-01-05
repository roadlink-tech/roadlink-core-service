package com.roadlink.core.domain.usertrustscore

import com.roadlink.core.domain.feedback.FeedbackRepositoryPort
import com.roadlink.core.domain.feedback.FeedbacksFactory
import com.roadlink.core.domain.user.UserFactory
import com.roadlink.core.domain.user.UserRepositoryPort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

class UserTrustScoreServiceTest : BehaviorSpec({

    val userRepository = mockk<UserRepositoryPort>()
    val feedbackRepository = mockk<FeedbackRepositoryPort>()

    Given("a user trust score service") {
        val userTrustScoreService = DefaultUserTrustScoreService(
            userRepository, feedbackRepository
        )

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

            val response = userTrustScoreService.findById(oldUserId)
            Then("the score and the feedbacks amount must be the expected") {
                response.score.shouldBe(3.5)
                response.feedbacksReceived.shouldBe(4)
                response.feedbacksGiven.shouldBe(1)
                response.enrollmentAge.shouldBeGreaterThan(360)
            }
        }
    }
})
