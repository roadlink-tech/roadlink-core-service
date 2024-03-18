package com.roadlink.core.infrastructure.feedback.solicitude

import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitude.*
import com.roadlink.core.domain.feedback.solicitude.FeedbackSolicitudeCriteria
import com.roadlink.core.infrastructure.dynamodb.DynamoDbEntityMapper
import com.roadlink.core.infrastructure.dynamodb.DynamoDbQueryMapper
import com.roadlink.core.infrastructure.dynamodb.RepositoryAdapter
import com.roadlink.core.infrastructure.utils.LocalStackHelper
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.testcontainers.perSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.util.*

class FeedbackSolicitudeRepositoryAdapterIntegrationTest : BehaviorSpec({
    val container = LocalStackHelper.containerWithDynamoDb()
    listener(container.perSpec())
    afterEach {
        LocalStackHelper.clearDynamoTableIn(container)
    }

    Given("a container with dynamodb and the table already created") {
        val dynamoDbClient = LocalStackHelper.dynamoDbClient(container)

        val dynamoEntityMapper: DynamoDbEntityMapper<FeedbackSolicitude, FeedbackSolicitudeDynamoDbEntity> =
            FeedbackSolicitudeDynamoDbEntityMapper()
        val dynamoQueryMapper: DynamoDbQueryMapper<FeedbackSolicitudeCriteria, FeedbackSolicitudeDynamoDbQuery> =
            FeedbackSolicitudeDynamoDbQueryMapper()
        val repository = RepositoryAdapter(dynamoDbClient, "RoadlinkCore", dynamoEntityMapper, dynamoQueryMapper)

        LocalStackHelper.createTableIn(container)

        When("save a new user entity") {
            val solicitude = FeedbackSolicitudeFactory.custom()
            val response = shouldNotThrow<RuntimeException> {
                repository.save(solicitude)
            }

            Then("the response should not be null") {
                response.shouldNotBeNull()
            }
        }

        When("save new feedback solicitudes and find it by reviewer id and status") {
            val reviewerId = UUID.randomUUID()
            repeat(10) {
                repository.save(FeedbackSolicitudeFactory.custom(reviewerId = reviewerId, status = Status.PENDING))
            }

            val feedbackSolicitudesFound = repository.findAll(
                criteria = FeedbackSolicitudeCriteria(
                    reviewerId = reviewerId,
                    status = Status.PENDING
                )
            )

            Then("the response should not be null") {
                feedbackSolicitudesFound.shouldNotBeNull()
                feedbackSolicitudesFound.size.shouldBe(10)
            }
        }

        When("save new feedback solicitudes and find it by trip id and status") {
            val reviewerId = UUID.randomUUID()
            val tripLegId = UUID.randomUUID()
            repeat(10) {
                repository.save(FeedbackSolicitudeFactory.custom(reviewerId = reviewerId, status = Status.PENDING))
            }
            repository.save(
                FeedbackSolicitudeFactory.custom(
                    reviewerId = reviewerId,
                    tripLegId = tripLegId,
                    status = Status.REJECTED
                )
            )

            val feedbackSolicitudesFound = repository.findAll(
                criteria = FeedbackSolicitudeCriteria(
                    reviewerId = reviewerId,
                    tripLegId = tripLegId
                )
            )

            Then("the response should not be null") {
                feedbackSolicitudesFound.shouldNotBeNull()
                feedbackSolicitudesFound.size.shouldBe(1)
                feedbackSolicitudesFound.any { it.status == Status.REJECTED }.shouldBe(true)
            }
        }

    }
})

