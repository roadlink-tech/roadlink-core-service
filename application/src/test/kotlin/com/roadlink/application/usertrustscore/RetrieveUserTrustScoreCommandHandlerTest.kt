package com.roadlink.application.usertrustscore

import com.roadlink.application.feedback.FeedbackFactory
import com.roadlink.application.user.UserFactory
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.match
import io.kotest.matchers.string.shouldNotBeEmpty
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class RetrieveUserTrustScoreCommandHandlerTest : BehaviorSpec({

    Given("a RetrieveUserTrustScoreCommandHandler") {
        val userRepository: RepositoryPort<User, UserCriteria> = mockk()
        val feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria> = mockk()
        val handler = RetrieveUserTrustScoreCommandHandler(userRepository, feedbackRepository)
        beforeEach { clearMocks(userRepository, feedbackRepository) }

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

        When("there isn't any feedbacks received, and execute a command") {
            val userId = UUID.randomUUID()
            every { feedbackRepository.findAll(match { it.receiverId == userId }) } returns emptyList()

            every { feedbackRepository.findAll(match { it.reviewerId == userId }) } returns emptyList()

            every { userRepository.findOrFail(match { it.id == userId }) } returns UserFactory.common(
                id = userId
            )

            val response = handler.handle(RetrieveUserTrustScoreCommand(userId))

            Then("the user trust score must be the expected") {
                response.userTrustScore.feedbacksReceived.shouldBe(0)
                response.userTrustScore.feedbacksGiven.shouldBe(0)
                response.userTrustScore.score.shouldBe(0.00)
            }
        }

        When("the user does not exist") {
            val userId = UUID.randomUUID()
            every { userRepository.findOrFail(match { it.id == userId }) } throws DynamoDbException.EntityDoesNotExist(
                userId.toString()
            )

            val response = shouldThrow<DynamoDbException.EntityDoesNotExist> {
                handler.handle(RetrieveUserTrustScoreCommand(userId))
            }
            Then("the user trust score must be the expected") {
                response.message.shouldBe("Entity $userId does not exist")
                verify(exactly = 0) { feedbackRepository.findAll(any()) }
            }
        }
    }
})
