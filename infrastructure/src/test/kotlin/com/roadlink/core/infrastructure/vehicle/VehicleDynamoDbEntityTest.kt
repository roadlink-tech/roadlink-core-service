package com.roadlink.core.infrastructure.vehicle

import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.vehicle.Vehicle
import com.roadlink.core.domain.vehicle.VehicleCriteria
import com.roadlink.core.infrastructure.dynamodb.DynamoDbEntityMapper
import com.roadlink.core.infrastructure.dynamodb.DynamoDbQueryMapper
import com.roadlink.core.infrastructure.dynamodb.RepositoryAdapter
import com.roadlink.core.infrastructure.user.*
import com.roadlink.core.infrastructure.utils.LocalStackHelper
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.perSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.util.*

class VehicleRepositoryAdapterIntegrationTest : BehaviorSpec({
    val container = LocalStackHelper.containerWithDynamoDb()
    listener(container.perSpec())
    afterEach {
        LocalStackHelper.clearDynamoTableIn(container)
    }

    Given("a container with dynamo and the table already created") {
        val dynamoDbClient = LocalStackHelper.dynamoDbClient(container)
        val dynamoEntityMapper: DynamoDbEntityMapper<Vehicle, VehicleDynamoDbEntity> = VehicleDynamoDbEntityMapper()
        val dynamoQueryMapper: DynamoDbQueryMapper<VehicleCriteria, VehicleDynamoDbQuery> = VehicleDynamoDbQueryMapper()

        val repository = RepositoryAdapter(dynamoDbClient, "RoadlinkCore", dynamoEntityMapper, dynamoQueryMapper)
        LocalStackHelper.createTableIn(container)

        When("try to save a new vehicle entity and find it using the driverId") {
            val driverId = UUID.randomUUID()
            val vehicle = VehicleFactory.common(driverId = driverId)
            shouldNotThrow<RuntimeException> {
                repository.save(vehicle)
            }

            val vehicleFound = repository.findOrFail(VehicleCriteria(driverId = driverId))
            Then("the response should not be null") {
                vehicleFound.driverId.shouldBe(driverId)
                vehicleFound.brand.shouldBe("Ford")
                vehicleFound.model.shouldBe("Territory")
                vehicleFound.licencePlate.shouldBe("AG123AG")
                vehicleFound.iconUrl.shouldBe("https://icon.com")
            }
        }
    }

})
