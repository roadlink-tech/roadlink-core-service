package com.roadlink.core.infrastructure.feedback

import com.roadlink.core.domain.feedback.FeedbackCriteria
import com.roadlink.core.infrastructure.utils.LocalStackHelper
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.testcontainers.perSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.util.UUID

class FeedbackRepositoryAdapterIntegrationTest : BehaviorSpec({
    val container = LocalStackHelper.containerWithDynamoDb()
    listener(container.perSpec())
    afterEach {
        LocalStackHelper.clearDynamoTableIn(container)
    }

    Given("a container with dynamodb and the table already created") {
        val dynamoDbClient = LocalStackHelper.dynamoDbClient(container)
        val repository = FeedbackRepositoryAdapter(dynamoDbClient)
        LocalStackHelper.createTableIn(container)

        When("save a new user entity") {
            val feedback = FeedbackFactory.custom()
            val response = shouldNotThrow<RuntimeException> {
                repository.save(feedback)
            }

            Then("the response should not be null") {
                response.shouldNotBeNull()
            }
        }

        When("save a new feedbacks and find it by rating") {
            val feedback = FeedbackFactory.custom(rating = 2)
            repository.save(feedback)

            val feedbackFound = repository.findOrFail(criteria = FeedbackCriteria(rating = 2))
            Then("the response should not be null") {
                feedbackFound.shouldNotBeNull()
                feedbackFound.rating.shouldBe(2)
            }
        }

        When("there isn't any feedback that saved") {
            val feedbacks = repository.findAll(
                criteria = FeedbackCriteria(
                    rating = 5,
                    receiverId = UUID.randomUUID(),
                    reviewerId = UUID.randomUUID()
                )
            )

            Then("the response should be empty") {
                feedbacks.shouldBeEmpty()
            }
        }
        When("save some new feedbacks and find some of them") {
            val receiverId = UUID.randomUUID()
            val reviewerId = UUID.randomUUID()
            val aFeedbackId = UUID.randomUUID()
            val feedbacks = listOf(
                FeedbackFactory.custom(id = aFeedbackId, receiverId = receiverId, rating = 2),
                FeedbackFactory.custom(receiverId = receiverId, rating = 5),
                FeedbackFactory.custom(receiverId = receiverId, rating = 4),
                FeedbackFactory.custom(rating = 5),
                FeedbackFactory.custom(reviewerId = reviewerId, rating = 5, receiverId = receiverId)
            )

            feedbacks.forEach { feedback ->
                repository.save(feedback)
            }

            val feedbackFoundByReceiverId = repository.findAll(criteria = FeedbackCriteria(receiverId = receiverId))
            val feedbackFoundByRating = repository.findAll(criteria = FeedbackCriteria(rating = 5))
            val feedbackFoundById = repository.findAll(criteria = FeedbackCriteria(id = aFeedbackId))
            val feedbacksFoundByRatingAndReceiverId =
                repository.findAll(criteria = FeedbackCriteria(rating = 5, receiverId = receiverId))
            val feedbacksFoundByIdAndReceiverId =
                repository.findAll(criteria = FeedbackCriteria(id = aFeedbackId, receiverId = receiverId))
            val feedbacksFoundByRatingAndReceiverIdAndReviewer = repository.findAll(
                criteria = FeedbackCriteria(
                    rating = 5,
                    receiverId = receiverId,
                    reviewerId = reviewerId
                )
            )
            Then("the response should not be null") {
                feedbackFoundByReceiverId.shouldNotBeEmpty()
                feedbackFoundByReceiverId.size.shouldBe(4)
                feedbackFoundByReceiverId.filter { it.receiverId != receiverId }.shouldBeEmpty()

                feedbackFoundByRating.shouldNotBeEmpty()
                feedbackFoundByRating.size.shouldBe(3)
                feedbackFoundByRating.filter { it.rating != 5 }.shouldBeEmpty()

                feedbackFoundById.shouldNotBeEmpty()
                feedbackFoundById.size.shouldBe(1)
                feedbackFoundById.filter { it.id != aFeedbackId }.shouldBeEmpty()

                feedbacksFoundByRatingAndReceiverId.shouldNotBeEmpty()
                feedbacksFoundByRatingAndReceiverId.size.shouldBe(2)
                feedbacksFoundByRatingAndReceiverId.filter { it.rating != 5 && it.receiverId != receiverId }
                    .shouldBeEmpty()

                feedbacksFoundByIdAndReceiverId.shouldNotBeEmpty()
                feedbacksFoundByIdAndReceiverId.size.shouldBe(1)
                feedbacksFoundByRatingAndReceiverIdAndReviewer.shouldNotBeEmpty()
                feedbacksFoundByRatingAndReceiverIdAndReviewer.size.shouldBe(1)
            }
        }
    }
})
