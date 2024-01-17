package com.roadlink.application.usertrustscore

import com.roadlink.application.feedback.FeedbackFactory
import com.roadlink.application.user.UserFactory
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.match
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

class RetrieveUserTrustScoreCommandHandlerTest : BehaviorSpec({

    Given("a RetrieveUserTrustScoreCommandHandler") {
        val userRepository: RepositoryPort<User, UserCriteria> = mockk()
        val feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria> = mockk()
        val handler = RetrieveUserTrustScoreCommandHandler(userRepository, feedbackRepository)

        When("there are a lot of user feedbacks received, and execute a command") {
            val userId = UUID.randomUUID()
            every { feedbackRepository.findAll(match { it.receiverId == userId }) } returns
                    listOf(
                        FeedbackFactory.common(receiverId = userId, rating = 5),
                        FeedbackFactory.common(receiverId = userId, rating = 4),
                        FeedbackFactory.common(receiverId = userId, rating = 3),
                        FeedbackFactory.common(receiverId = userId, rating = 2),
                        FeedbackFactory.common(receiverId = userId, rating = 1),
                        FeedbackFactory.common(receiverId = userId, rating = 1),
                        FeedbackFactory.common(receiverId = userId, rating = 1),
                    )

            every { feedbackRepository.findAll(match { it.reviewerId == userId }) } returns emptyList()

            every { userRepository.findOrFail(match { it.id == userId }) } returns UserFactory.common(
                id = userId
            )

            val response = handler.handle(RetrieveUserTrustScoreCommand(userId))

            Then("the user trust score must be the expected") {
                response.userTrustScore.feedbacksReceived.shouldBe(7)
                response.userTrustScore.feedbacksGiven.shouldBe(0)
                response.userTrustScore.score.shouldBe(2.43)
            }
        }
    }
})
