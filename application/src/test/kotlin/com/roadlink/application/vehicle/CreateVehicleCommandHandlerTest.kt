package com.roadlink.application.vehicle

import com.roadlink.application.user.UserFactory
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.vehicle.Vehicle
import com.roadlink.core.domain.vehicle.VehicleCriteria
import com.roadlink.core.infrastructure.dynamodb.error.DynamoDbException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class CreateVehicleCommandHandlerTest : BehaviorSpec({

    val userRepository: RepositoryPort<User, UserCriteria> = mockk()
    val vehicleRepository: RepositoryPort<Vehicle, VehicleCriteria> = mockk()
    afterEach {
        clearMocks(userRepository, vehicleRepository)
    }

    Given("a create vehicle command handler") {
        val handler = CreateVehicleCommandHandler(userRepository, vehicleRepository)

        When("the user does not exist") {
            val driverId = UUID.randomUUID()
            val vehicle = VehicleDTO.from(VehicleFactory.common(driverId = driverId))

            every { userRepository.findOrFail(match { it.id == driverId }) } throws DynamoDbException.EntityDoesNotExist(
                ""
            )

            val response = shouldThrow<DynamoDbException.EntityDoesNotExist> {
                handler.handle(CreateVehicleCommand(vehicle = vehicle))
            }

            Then("the repository calls must be the expected") {
                verify(exactly = 0) { vehicleRepository.save(any()) }
                verify(exactly = 1) { userRepository.findOrFail(any()) }
            }
        }

        When("the user exist") {
            val driverId = UUID.randomUUID()
            val vehicle = VehicleFactory.common(driverId = driverId)
            val vehicleDto = VehicleDTO.from(vehicle)
            val driver = UserFactory.common(id = driverId)

            every { userRepository.findOrFail(match { it.id == driverId }) } returns driver
            every { vehicleRepository.save(match { it.driverId == driverId }) } returns vehicle
            val response = handler.handle(CreateVehicleCommand(vehicle = vehicleDto))

            Then("the vehicle must be saved") {
                response.vehicle.shouldBe(vehicleDto)
                verify(exactly = 1) { vehicleRepository.save(any()) }
                verify(exactly = 1) { userRepository.findOrFail(any()) }
            }
        }
    }

})
