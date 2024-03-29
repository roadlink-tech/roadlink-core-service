package com.roadlink.core.infrastructure.user

import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.DefaultLocalDateTimeHandler
import com.roadlink.core.infrastructure.dynamodb.DynamoDbEntityMapper
import com.roadlink.core.infrastructure.dynamodb.DynamoDbQueryMapper
import com.roadlink.core.infrastructure.dynamodb.RepositoryAdapter
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import com.roadlink.core.infrastructure.utils.LocalStackHelper
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.testcontainers.perSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.util.*

class UserRepositoryAdapterIntegrationTest : BehaviorSpec({
    val container = LocalStackHelper.containerWithDynamoDb()
    listener(container.perSpec())
    afterEach {
        LocalStackHelper.clearDynamoTableIn(container)
    }

    Given("a container with dynamo and the table already created") {
        val dynamoDbClient = LocalStackHelper.dynamoDbClient(container)
        val dynamoEntityMapper: DynamoDbEntityMapper<User, UserDynamoDbEntity> =
            UserDynamoDbEntityMapper()
        val dynamoQueryMapper: DynamoDbQueryMapper<UserCriteria, UserDynamoDbQuery> =
            UserDynamoDbQueryMapper()

        val repository =
            RepositoryAdapter(dynamoDbClient, "RoadlinkCore", dynamoEntityMapper, dynamoQueryMapper)
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

        When("save a new user entity and then delete it") {
            val user =
                UserFactory.custom(id = UUID.fromString("6eada274-39a6-4231-837f-cad7e96cb4ce"))
            repository.save(user)

            shouldNotThrow<RuntimeException> { repository.delete(UserCriteria(id = user.id)) }

            val response = shouldThrow<DynamoDbException.EntityDoesNotExist> {
                repository.findOrFail(UserCriteria(id = user.id))
            }

            Then("the response should not be null") {
                response.shouldNotBeNull()
            }
        }

        When("save a new user and then find it by id") {
            val id = UUID.randomUUID()
            val user = UserFactory.custom(id = id, birthDay = LocalDate.of(1991, 12, 6))
            repository.save(user)

            val response = repository.findOrFail(UserCriteria(id = id))
            Then("the response should not be null") {
                response.id shouldBe id
                response.firstName shouldBe "Jorge Javier"
                response.lastName shouldBe "Cabrera Vera"
                response.email shouldBe "cabrerajjorge@gmail.com"
                response.profilePhotoUrl shouldBe "https://lh3.googleusercontent.com/a/ACg8ocJW5g-yavaNzKPZcF-U8-W5zGfIQdww2mOcyDq_48xfdHE=s96-c"
                response.gender shouldBe "male"
                response.birthDay shouldBe DefaultLocalDateTimeHandler.from("06/12/1991")
                response.userName shouldBe "jorge.cabrera"
            }
        }

        When("save a new user with friends and then find it by id") {
            val id = UUID.randomUUID()
            val friends = setOf(UUID.randomUUID(), UUID.randomUUID())
            val user = UserFactory.custom(id = id, friends = friends)
            repository.save(user)

            val response = repository.findOrFail(UserCriteria(id = id))
            Then("the response should not be null") {
                response.id shouldBe id
                response.firstName shouldBe "Jorge Javier"
                response.lastName shouldBe "Cabrera Vera"
                response.email shouldBe "cabrerajjorge@gmail.com"
                response.friends shouldBe friends.toMutableSet()
                response.gender shouldBe "male"
                response.birthDay shouldBe null
            }
        }

        When("save entities in batch") {
            val response = repository.saveAll(
                listOf(
                    UserFactory.custom(), UserFactory.custom(), UserFactory.custom()
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
                response.userName shouldBe "jorge.cabrera"
            }
        }

        When("save a new user and then find it by user name") {
            val id = UUID.randomUUID()
            val user = UserFactory.custom(
                id = id,
                firstName = "John",
                lastName = "Doe",
                userName = "johndoe",
                email = "john.doe@gmail.com"
            )
            repository.save(user)

            val response = repository.findOrFail(UserCriteria(userName = "johndoe"))
            Then("the response should not be null") {
                response.id shouldBe id
                response.firstName shouldBe "John"
                response.lastName shouldBe "Doe"
                response.userName shouldBe "johndoe"
            }
        }

        When("save a new user and then find it by user name and email") {
            val id = UUID.randomUUID()
            val user = UserFactory.custom(
                id = id,
                firstName = "John",
                lastName = "Doe",
                userName = "john.doe",
                email = "john.doe.1@gmail.com"
            )
            repository.save(user)

            val response = repository.findOrFail(
                UserCriteria(
                    userName = "john.doe", email = "john.doe.1@gmail.com"
                )
            )
            Then("the response should not be null") {
                response.id shouldBe id
                response.firstName shouldBe "John"
                response.lastName shouldBe "Doe"
                response.userName shouldBe "john.doe"
                response.email shouldBe "john.doe.1@gmail.com"
            }
        }

        When("patch a user then it must work ok") {
            val userId = UUID.randomUUID()
            val user = UserFactory.custom(
                id = userId,
            )
            repository.save(user)

            repository.save(
                user.copy(
                    email = "martin.bosch@roadlink.com",
                    firstName = "martin",
                    lastName = "bosch"
                )
            )

            val response = repository.findOrFail(UserCriteria(email = "martin.bosch@roadlink.com"))

            Then("the response should not be null") {
                response.id shouldBe userId
                response.firstName shouldBe "martin"
                response.lastName shouldBe "bosch"
                response.email shouldBe "martin.bosch@roadlink.com"
                response.userName shouldBe user.userName
                response.gender shouldBe user.gender
                response.birthDay shouldBe user.birthDay
                response.friends shouldBe user.friends
            }
        }

        When("find a user by user name and email, but it does not exist") {
            val response = shouldThrow<RuntimeException> {
                repository.findOrFail(
                    UserCriteria(
                        userName = "juan", email = "roman.riquelme@gmail.com"
                    )
                )
            }
            Then("the response should not be null") {
                response.message.shouldBe("""Entity UserCriteria(id=null, email=roman.riquelme@gmail.com, userName=juan) does not exist""")
            }
        }
    }
})
