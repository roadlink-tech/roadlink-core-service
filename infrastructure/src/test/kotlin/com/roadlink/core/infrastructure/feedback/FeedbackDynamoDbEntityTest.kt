package com.roadlink.core.infrastructure.feedback

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FeedbackDynamoDbEntityTest : BehaviorSpec({


    Given("a feedback entity") {
        val entity = FeedbackDynamoDbEntity()

        When("list all the attribute names") {
            val attributeNames = entity.attributeNames

            Then("the all must be retrieved") {
                attributeNames.shouldBe(
                    listOf(
                        "entityId",
                        "id",
                        "createdDate",
                        "rating",
                        "receiverId",
                        "reviewerId",
                        "comment"
                    )
                )
            }
        }
    }
})
