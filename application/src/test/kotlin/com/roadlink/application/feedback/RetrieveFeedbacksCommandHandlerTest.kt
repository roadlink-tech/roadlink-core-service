package com.roadlink.application.feedback

import com.roadlink.application.user.UserFactory
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class RetrieveFeedbacksCommandHandlerTest : BehaviorSpec({
    val repository: RepositoryPort<Feedback, FeedbackCriteria> = mockk()
    val userRepository: RepositoryPort<User, UserCriteria> = mockk()
    afterEach {
        clearMocks(repository, userRepository)
    }

    Given("a feedback command handler") {
        val handler = ListFeedbacksCommandHandler(userRepository, repository)

        When("a user has more than one feedback received") {
            val user = UserFactory.common()
            val feedbacks = mutableListOf<Feedback>()
            repeat(10) {
                feedbacks.add(FeedbackFactory.common(receiverId = user.id))
            }
            every { userRepository.findOrFail(match { it.id == user.id }) } returns user
            every { repository.findAll(match { it.receiverId == user.id }) } returns feedbacks
            val response = handler.handle(ListFeedbacksCommand(receiverId = user.id))

            Then("all of those must be retrieved") {
                verify(exactly = 1) { repository.findAll(any()) }
                response.feedbacks.size.shouldBe(10)
            }
        }

        When("a user does not have any feedback received") {
            val user = UserFactory.common()
            every { userRepository.findOrFail(match { it.id == user.id }) } returns user
            every { repository.findAll(match { it.receiverId == user.id }) } returns emptyList()
            val response = handler.handle(ListFeedbacksCommand(receiverId = user.id))

            Then("all of those must be retrieved") {
                verify(exactly = 1) { repository.findAll(any()) }
                response.feedbacks.shouldBeEmpty()
            }
        }
    }

})
