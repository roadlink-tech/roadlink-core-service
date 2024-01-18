package com.roadlink.application.feedback

import com.roadlink.application.user.UserFactory
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class CreateFeedbackCommandHandlerTest : BehaviorSpec({

    Given("a feedback creation command handler") {
        val feedbackRepository: RepositoryPort<Feedback, FeedbackCriteria> = mockk()
        val userRepository: RepositoryPort<User, UserCriteria> = mockk()
        val handler = CreateFeedbackCommandHandler(userRepository, feedbackRepository)
        afterEach {
            clearMocks(userRepository)
        }

        When("handle a command with a receiverId that not exist") {
            val receiverId = UUID.randomUUID()
            val reviewerId = UUID.randomUUID()
            val tripId = UUID.randomUUID()
            val command = CreateFeedbackCommand(
                feedback = FeedbackDTO(
                    id = UUID.randomUUID(),
                    receiverId = receiverId,
                    reviewerId = reviewerId,
                    rating = 2,
                    tripId = tripId
                )
            )

            every { userRepository.findOrFail(match { it.id == reviewerId }) } returns UserFactory.common(id = reviewerId)

            every { userRepository.findOrFail(match { it.id == receiverId }) }.throws(
                DynamoDbException.EntityDoesNotExist(receiverId.toString())
            )

            val ex = shouldThrow<DynamoDbException.EntityDoesNotExist> {
                handler.handle(command)
            }

            Then("an exception must be raised") {
                ex.shouldNotBeNull()
                ex.message.shouldBe("Entity $receiverId does not exist")
                verify(exactly = 0) { feedbackRepository.save(any()) }
            }
        }

        When("handle a command with a reviewer that not exist") {
            val receiverId = UUID.randomUUID()
            val reviewerId = UUID.randomUUID()
            val tripId = UUID.randomUUID()
            val command = CreateFeedbackCommand(
                feedback = FeedbackDTO(
                    id = UUID.randomUUID(),
                    receiverId = receiverId,
                    reviewerId = reviewerId,
                    rating = 2,
                    tripId = tripId
                )
            )

            every { userRepository.findOrFail(match { it.id == receiverId }) }.returns(
                UserFactory.common(
                    id = receiverId
                )
            )

            every { userRepository.findOrFail(match { it.id == reviewerId }) }.throws(
                DynamoDbException.EntityDoesNotExist(reviewerId.toString())
            )

            val ex = shouldThrow<DynamoDbException.EntityDoesNotExist> {
                handler.handle(command)
            }

            Then("an exception must be raised") {
                ex.shouldNotBeNull()
                ex.message.shouldBe("Entity $reviewerId does not exist")
                verify(exactly = 0) { feedbackRepository.save(any()) }
            }
        }

        When("handle a command successfully") {
            val id = UUID.randomUUID()
            val receiverId = UUID.randomUUID()
            val reviewerId = UUID.randomUUID()
            val tripId = UUID.randomUUID()
            val command = CreateFeedbackCommand(
                feedback = FeedbackDTO(
                    id = id,
                    receiverId = receiverId,
                    reviewerId = reviewerId,
                    rating = 2,
                    comment = "No me senti a gusto",
                    tripId = tripId
                )
            )

            every { userRepository.findOrFail(match { it.id == receiverId }) }.returns(
                UserFactory.common(
                    id = receiverId
                )
            )

            every { userRepository.findOrFail(match { it.id == reviewerId }) }.returns(
                UserFactory.common(
                    id = receiverId
                )
            )

            every { feedbackRepository.save(match { it.receiverId == receiverId && it.reviewerId == reviewerId }) }.returns(
                FeedbackFactory.common(
                    id = id,
                    receiverId = receiverId,
                    reviewerId = reviewerId,
                    rating = 2,
                    comment = "No me senti a gusto",
                    tripId = tripId
                )
            )

            val response = handler.handle(command)


            Then("an exception must be raised") {
                verify(exactly = 1) { feedbackRepository.save(any()) }
                response.shouldNotBeNull()
                response.feedback.reviewerId.shouldBe(reviewerId)
                response.feedback.receiverId.shouldBe(receiverId)
                response.feedback.rating.shouldBe(2)
                response.feedback.comment.shouldBe("No me senti a gusto")
                response.feedback.id.shouldBe(id)
                response.feedback.tripId.shouldBe(tripId)
            }
        }
    }
})
