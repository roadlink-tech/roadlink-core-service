package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.google.GoogleUser
import com.roadlink.core.domain.user.google.GoogleUserCriteria
import com.roadlink.core.infrastructure.dynamodb.DynamoDbEntityMapper
import com.roadlink.core.infrastructure.dynamodb.DynamoDbQueryMapper
import com.roadlink.core.infrastructure.dynamodb.RepositoryAdapter
import com.roadlink.core.infrastructure.utils.LocalStackHelper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.testcontainers.perSpec
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import java.util.*

class GoogleUserRepositoryAdapterIntegrationTest : BehaviorSpec({
    val container = LocalStackHelper.containerWithDynamoDb()
    listener(container.perSpec())

    afterEach {
        LocalStackHelper.clearDynamoTableIn(container)
    }

    Given("a container with dynamodb and the table already created") {
        val dynamoDbClient = LocalStackHelper.dynamoDbClient(container)

        val dynamoEntityMapper: DynamoDbEntityMapper<GoogleUser, GoogleUserDynamoDbEntity> = GoogleUserDynamoDbEntityMapper()
        val dynamoQueryMapper: DynamoDbQueryMapper<GoogleUserCriteria, GoogleUserDynamoDbQuery> = GoogleUserDynamoDbQueryMapper()
        val repository = RepositoryAdapter(dynamoDbClient, "RoadlinkCore", dynamoEntityMapper, dynamoQueryMapper)

        LocalStackHelper.createTableIn(container)

        When("save a new google user and find it by google id") {
            val googleId = "109097944437190043577"
            val userId = UUID.fromString("7a54ae3a-13f2-4280-8a40-c61bc3f283ed")
            val googleUser = GoogleUser(googleId = googleId, userId = userId)
            repository.save(googleUser)

            val result = repository.findOrNull(GoogleUserCriteria(googleId = googleId))

            Then("should found it") {
                result shouldBe googleUser
            }
        }

        When("find by a non existing google id") {
            val googleId = "109097944437190043577"
            val otherGoogleId = "109097944437190043578"
            val userId = UUID.fromString("7a54ae3a-13f2-4280-8a40-c61bc3f283ed")
            val googleUser = GoogleUser(googleId = googleId, userId = userId)
            repository.save(googleUser)

            val result = repository.findOrNull(GoogleUserCriteria(googleId = otherGoogleId))

            Then("should return null") {
                result should beNull()
            }
        }
    }
})
