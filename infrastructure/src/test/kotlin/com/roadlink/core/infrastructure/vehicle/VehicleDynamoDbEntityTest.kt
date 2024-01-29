package com.roadlink.core.infrastructure.vehicle

import com.roadlink.core.domain.vehicle.Vehicle
import com.roadlink.core.domain.vehicle.VehicleCriteria
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
import java.lang.Thread.sleep
import java.util.*

class VehicleRepositoryAdapterIntegrationTest : BehaviorSpec({
    val container = LocalStackHelper.containerWithDynamoDb()
    listener(container.perSpec())
    afterEach {
        LocalStackHelper.clearDynamoTableIn(container)
    }

    Given("a container with dynamo and the table already created") {
        val dynamoDbClient = LocalStackHelper.dynamoDbClient(container)
        val dynamoEntityMapper: DynamoDbEntityMapper<Vehicle, VehicleDynamoDbEntity> =
            VehicleDynamoDbEntityMapper()
        val dynamoQueryMapper: DynamoDbQueryMapper<VehicleCriteria, VehicleDynamoDbQuery> =
            VehicleDynamoDbQueryMapper()

        val repository =
            RepositoryAdapter(dynamoDbClient, "RoadlinkCore", dynamoEntityMapper, dynamoQueryMapper)
        LocalStackHelper.createTableIn(container)

        When("find a vehicle which does not exist") {
            val driverId = UUID.randomUUID()
            shouldThrow<RuntimeException> {
                repository.findOrFail(VehicleCriteria(driverId = driverId))
            }
        }

        When("try to save a new vehicle entity and find it using the driverId") {
            val driverId = UUID.randomUUID()
            val id = UUID.randomUUID()
            val vehicle = VehicleFactory.common(id = id, driverId = driverId, capacity = 6)
            shouldNotThrow<RuntimeException> {
                repository.save(vehicle)
            }

            val vehicleFoundByDriverId = repository.findOrFail(VehicleCriteria(driverId = driverId))
            Then("the vehicle found by driver id must not be null") {
                vehicleFoundByDriverId.id.shouldBe(id)
                vehicleFoundByDriverId.driverId.shouldBe(driverId)
                vehicleFoundByDriverId.brand.shouldBe("Ford")
                vehicleFoundByDriverId.model.shouldBe("Territory")
                vehicleFoundByDriverId.licencePlate.shouldBe("AG123AG")
                vehicleFoundByDriverId.iconUrl.shouldBe("https://icon.com")
                vehicleFoundByDriverId.capacity.shouldBe(6)
                vehicleFoundByDriverId.color.shouldBe("White")
            }

            val vehicleFoundById = repository.findOrFail(VehicleCriteria(id = id))
            Then("the vehicle found by id must not be null") {
                vehicleFoundById.id.shouldBe(id)
                vehicleFoundById.driverId.shouldBe(driverId)
                vehicleFoundById.brand.shouldBe("Ford")
                vehicleFoundById.model.shouldBe("Territory")
                vehicleFoundById.licencePlate.shouldBe("AG123AG")
                vehicleFoundById.iconUrl.shouldBe("https://icon.com")
                vehicleFoundById.capacity.shouldBe(6)
                vehicleFoundById.color.shouldBe("White")
            }
        }
    }
})
