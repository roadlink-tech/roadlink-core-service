package com.roadlink.core.infrastructure.feedback

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*

class FeedbackDynamoCriteriaTest : BehaviorSpec({


    Given("A dynamo criteria") {

        When("use the id as filter") {
            val expectedId = UUID.randomUUID()
            val criteria = FeedbackDynamoDbQuery(id = expectedId)

            Then("the key condition expression and attribute values must be ok") {
                criteria.keyConditionExpression().shouldBe("Id = :id AND EntityId = :entityId")
                criteria.expressionAttributeValues().shouldBe(
                    mapOf(
                        ":id" to AttributeValue.builder().s(expectedId.toString()).build(),
                        ":entityId" to AttributeValue.builder().s("EntityId#Feedback").build()
                    )
                )
            }
        }

        When("use the reviewer id as filter") {
            val expectedId = UUID.randomUUID()
            val criteria = FeedbackDynamoDbQuery(reviewerId = expectedId)

            Then("the key condition expression and attribute values must be ok") {
                criteria.keyConditionExpression().shouldBe("ReviewerId = :reviewerId AND EntityId = :entityId")
                criteria.expressionAttributeValues().shouldBe(
                    mapOf(
                        ":reviewerId" to AttributeValue.builder().s(expectedId.toString()).build(),
                        ":entityId" to AttributeValue.builder().s("EntityId#Feedback").build()
                    )
                )
            }
        }

        When("use the receiver id as filter") {
            val expectedId = UUID.randomUUID()
            val criteria = FeedbackDynamoDbQuery(receiverId = expectedId)

            Then("the key condition expression and attribute values must be ok") {
                criteria.keyConditionExpression().shouldBe("ReceiverId = :receiverId AND EntityId = :entityId")
                criteria.expressionAttributeValues().shouldBe(
                    mapOf(
                        ":receiverId" to AttributeValue.builder().s(expectedId.toString()).build(),
                        ":entityId" to AttributeValue.builder().s("EntityId#Feedback").build()
                    )
                )
            }
        }

        When("use the receiver id and reviewer id as filter") {
            val expectedId = UUID.randomUUID()
            val criteria = FeedbackDynamoDbQuery(receiverId = expectedId, reviewerId = expectedId)

            Then("the key condition expression and attribute values must be ok") {
                criteria.keyConditionExpression().shouldBe("ReceiverId = :receiverId AND EntityId = :entityId")
                criteria.expressionAttributeValues().shouldBe(
                    mapOf(
                        ":receiverId" to AttributeValue.builder().s(expectedId.toString()).build(),
                        ":entityId" to AttributeValue.builder().s("EntityId#Feedback").build()
                    )
                )
            }
        }

        When("use the id and receiver id as filter") {
            val expectedId = UUID.randomUUID()
            val criteria = FeedbackDynamoDbQuery(id = expectedId)

            Then("the key condition expression and attribute values must be ok") {
                criteria.keyConditionExpression().shouldBe("Id = :id AND EntityId = :entityId")
                criteria.expressionAttributeValues().shouldBe(
                    mapOf(
                        ":id" to AttributeValue.builder().s(expectedId.toString()).build(),
                        ":entityId" to AttributeValue.builder().s("EntityId#Feedback").build()
                    )
                )
            }
        }

        When("use the rating as filter") {
            val expectedRating = 4
            val criteria = FeedbackDynamoDbQuery(rating = 4)

            Then("the key condition expression and attribute values must be ok") {
                criteria.keyConditionExpression().shouldBe("Rating = :rating")
                criteria.expressionAttributeValues().shouldBe(
                    mapOf(
                        ":rating" to AttributeValue.builder().s(expectedRating.toString()).build()
                    )
                )
            }
        }
    }
})
