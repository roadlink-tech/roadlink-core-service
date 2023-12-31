package com.roadlink.core.infrastructure.feedback

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.*

class FeedbackDynamoDbEntityTest : BehaviorSpec({


    Given("a FeedbackDynamo entity") {
        val entity = FeedbackDynamoDbEntity(id = UUID.randomUUID(), createdDate = Date())
        When("get the entityId") {
            val entityId = entity.entityId
            Then("it must be the expected") {
                entityId.shouldBe("EntityId#Feedback")
            }
        }
    }
})
