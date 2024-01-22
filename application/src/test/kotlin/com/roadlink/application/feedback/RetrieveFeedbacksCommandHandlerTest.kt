package com.roadlink.application.feedback

import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.Feedback
import com.roadlink.core.domain.feedback.FeedbackCriteria
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class RetrieveFeedbacksCommandHandlerTest : BehaviorSpec({
    val repository: RepositoryPort<Feedback, FeedbackCriteria> = mockk()

    afterEach {
        clearMocks(repository)
    }

    Given("a feedback command handler") {
        val handler = RetrieveFeedbacksCommandHandler(repository)

        When("a user has more than one feedback received") {
            val userId = UUID.randomUUID()
            val feedbacks = mutableListOf<Feedback>()
            repeat(10) {
                feedbacks.add(FeedbackFactory.common(receiverId = userId))
            }

            every { repository.findAll(match { it.receiverId == userId }) } returns feedbacks
            val response = handler.handle(RetrieveFeedbacksCommand(receiverId = userId))

            Then("all of those must be retrieved") {
                verify(exactly = 1) { repository.findAll(any()) }
                response.feedbacks.size.shouldBe(10)
            }
        }

        When("a user does not have any feedback received") {
            val userId = UUID.randomUUID()

            every { repository.findAll(match { it.receiverId == userId }) } returns emptyList()
            val response = handler.handle(RetrieveFeedbacksCommand(receiverId = userId))

            Then("all of those must be retrieved") {
                verify(exactly = 1) { repository.findAll(any()) }
                response.feedbacks.shouldBeEmpty()
            }
        }
    }

})
