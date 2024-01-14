package com.roadlink.application.feedback

import com.roadlink.application.user.UserFactory
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.user.exception.UserInfrastructureException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class CreateFeedbackCommandHandlerTest : BehaviorSpec({

    val feedbackRepository = mockk<RepositoryPort<Feedback, FeedbackCriteria>>()
    val userRepository = mockk<RepositoryPort<User, UserCriteria>>()
    Given("a feedback creation command handler") {
        val commandHandler = CreateFeedbackCommandHandler(userRepository, feedbackRepository)

        When("handle a command with a receiverId that not exist") {
            val receiverId = UUID.randomUUID()
            val reviewerId = UUID.randomUUID()
            val command = CreateFeedbackCommand(
                feedback = FeedbackDTO(
                    id = UUID.randomUUID(),
                    receiverId = receiverId,
                    reviewerId = reviewerId,
                    rating = 2
                )
            )

            every { userRepository.findOrFail(match { it.id == reviewerId }) } returns UserFactory.common(id = reviewerId)

            every { userRepository.findOrFail(match { it.id == receiverId }) }.throws(
                UserInfrastructureException.NotFound(receiverId)
            )

            val ex = shouldThrow<UserInfrastructureException.NotFound> {
                commandHandler.handle(command)
            }

            Then("an exception must be raised") {
                ex.shouldNotBeNull()
                ex.message.shouldBe("User $receiverId not found")
                verify(exactly = 0) { feedbackRepository.save(any()) }
            }
        }

        When("handle a command with a reviewer that not exist") {
            val receiverId = UUID.randomUUID()
            val reviewerId = UUID.randomUUID()
            val command = CreateFeedbackCommand(
                feedback = FeedbackDTO(
                    id = UUID.randomUUID(),
                    receiverId = receiverId,
                    reviewerId = reviewerId,
                    rating = 2
                )
            )

            every { userRepository.findOrFail(match { it.id == receiverId }) }.returns(
                UserFactory.common(
                    id = receiverId
                )
            )

            every { userRepository.findOrFail(match { it.id == reviewerId }) }.throws(
                UserInfrastructureException.NotFound(reviewerId)
            )

            val ex = shouldThrow<UserInfrastructureException.NotFound> {
                commandHandler.handle(command)
            }

            Then("an exception must be raised") {
                ex.shouldNotBeNull()
                ex.message.shouldBe("User $reviewerId not found")
                verify(exactly = 0) { feedbackRepository.save(any()) }
            }
        }

        When("handle a command successfully") {
            val id = UUID.randomUUID()
            val receiverId = UUID.randomUUID()
            val reviewerId = UUID.randomUUID()
            val command = CreateFeedbackCommand(
                feedback = FeedbackDTO(
                    id = id,
                    receiverId = receiverId,
                    reviewerId = reviewerId,
                    rating = 2,
                    comment = "No me senti a gusto"
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
                    comment = "No me senti a gusto"
                )
            )

            val response = commandHandler.handle(command)


            Then("an exception must be raised") {
                verify(exactly = 1) { feedbackRepository.save(any()) }
                response.shouldNotBeNull()
                response.feedback.reviewerId.shouldBe(reviewerId)
                response.feedback.receiverId.shouldBe(receiverId)
                response.feedback.rating.shouldBe(2)
                response.feedback.comment.shouldBe("No me senti a gusto")
                response.feedback.id.shouldBe(id)
            }
        }
    }
})
