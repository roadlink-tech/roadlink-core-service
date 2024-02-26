package com.roadlink.application.feedback.solicitude

import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude.Status
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitudeCriteria
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class ListFeedbackSolicitudesCommandHandlerTest : BehaviorSpec({

    Given("a list feedback solicitudes command handler") {
        val feedbackSolicitudeRepository: RepositoryPort<FeedbackSolicitude, FeedbackSolicitudeCriteria> = mockk()
        val handler =
            ListFeedbackSolicitudesCommandHandler(feedbackSolicitudeRepository)

        afterEach {
            clearAllMocks()
        }

        When("list all the feedback solicitudes pending") {
            val reviewerId = UUID.randomUUID()
            every { feedbackSolicitudeRepository.findAll(match { it.reviewerId == reviewerId && it.status == Status.PENDING }) } returns listOf(
                FeedbackSolicitudeFactory.common(reviewerId = reviewerId, status = Status.PENDING),
                FeedbackSolicitudeFactory.common(reviewerId = reviewerId, status = Status.PENDING),
                FeedbackSolicitudeFactory.common(reviewerId = reviewerId, status = Status.PENDING),
                FeedbackSolicitudeFactory.common(reviewerId = reviewerId, status = Status.PENDING)
            )
            val response = handler.handle(
                command = ListFeedbackSolicitudesCommand(
                    filter = FeedbackSolicitudeListFilter(
                        reviewerId = reviewerId,
                        status = Status.PENDING
                    )
                )
            )

            Then("all the expected feedback solicitudes must be retrieved") {
                verify { feedbackSolicitudeRepository.findAll(any()) }
                response.solicitudes.size.shouldBe(4)
            }
        }
    }
})
