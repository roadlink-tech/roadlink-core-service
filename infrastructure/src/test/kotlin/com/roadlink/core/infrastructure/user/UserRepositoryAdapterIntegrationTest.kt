package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.utils.LocalStackHelper
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.testcontainers.perSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.util.*

class UserRepositoryAdapterIntegrationTest : BehaviorSpec({
    val container = LocalStackHelper.containerWithDynamoDb()
    listener(container.perSpec())
    afterEach {
        LocalStackHelper.clearDynamoTableIn(container)
    }

    Given("a container with dynamo and the table already created") {
        val dynamoDbClient = LocalStackHelper.dynamoDbClient(container)
        val repository = UserRepositoryAdapter(dynamoDbClient)
        LocalStackHelper.createTableIn(container)

        When("try to save a new user entity") {
            val user = UserFactory.custom()
            val response = shouldNotThrow<RuntimeException> {
                repository.save(user)
            }

            Then("the response should not be null") {
                response.shouldNotBeNull()
            }
        }

        When("save a new user and then find it by id") {
            val id = UUID.randomUUID()
            val user = UserFactory.custom(id = id)
            repository.save(user)

            val response = repository.findOrFail(UserCriteria(id = id))
            Then("the response should not be null") {
                response.id shouldBe id
                response.firstName shouldBe "Jorge Javier"
                response.lastName shouldBe "Cabrera Vera"
                response.email shouldBe "cabrerajjorge@gmail.com"
            }
        }

        When("save entities in batch") {
            val response =
                repository.saveAll(
                    listOf(
                        UserFactory.custom(),
                        UserFactory.custom(),
                        UserFactory.custom()
                    )
                )

            Then("the response should not be null") {
                response.size.shouldBe(3)
            }
        }

        When("save a new user and then find it by email") {
            val id = UUID.randomUUID()
            val email = "jorgejcabrera@hotmail.com.ar"
            val user = UserFactory.custom(id = id, email = email)
            repository.save(user)

            val response = repository.findOrFail(UserCriteria(email = user.email))
            Then("the response should not be null") {
                response.id shouldBe id
                response.email shouldBe email
                response.firstName shouldBe "Jorge Javier"
                response.lastName shouldBe "Cabrera Vera"
            }
        }
    }
})
