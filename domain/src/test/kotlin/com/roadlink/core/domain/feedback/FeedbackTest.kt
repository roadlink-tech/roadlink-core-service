package com.roadlink.core.domain.feedback

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.util.UUID

class FeedbackTest : BehaviorSpec({

    Given("a feedback domain entity") {
        When("the reviewer and receiver are the same") {
            val id = UUID.randomUUID()
            val ex = shouldThrow<RuntimeException> {
                Feedback(
                    id = UUID.randomUUID(),
                    reviewerId = id,
                    receiverId = id,
                    rating = 4,
                    comment = "asd",
                    tripId = UUID.randomUUID(),
                )
            }
            Then("an expected expcetion must be raised") {
                ex.shouldNotBeNull()
            }
        }
    }
})
