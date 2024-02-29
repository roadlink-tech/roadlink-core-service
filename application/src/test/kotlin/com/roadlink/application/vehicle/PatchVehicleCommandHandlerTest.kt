package com.roadlink.application.vehicle

import com.roadlink.application.user.UserFactory
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.vehicle.Vehicle
import com.roadlink.core.domain.vehicle.VehicleCriteria
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import java.util.UUID

class PatchVehicleCommandHandlerTest : BehaviorSpec({

    val userRepository: RepositoryPort<User, UserCriteria> = mockk()
    val vehicleRepository: RepositoryPort<Vehicle, VehicleCriteria> = mockk()
    afterEach {
        clearMocks(userRepository, vehicleRepository)
    }

    Given("a patch vehicle command handler") {
        val handler = PatchVehicleCommandHandler(userRepository = userRepository, vehicleRepository = vehicleRepository)

        When("patch an existing user with a new icon url") {
            val vehicleId = UUID.randomUUID()
            val george = UserFactory.common()
            val vehicle = VehicleFactory.common(id = vehicleId, driverId = george.id)
            val vehicleDTO = VehicleDTO(
                id = vehicleId,
                driverId = george.id,
                brand = "",
                model = "",
                licencePlate = "",
                capacity = 0,
                iconUrl = "https://newicon.com",
                color = ""
            )
            every { userRepository.findOrFail(match { it.id == george.id }) } returns george
            every { vehicleRepository.findOrFail(match { it.id == vehicleId }) } returns vehicle
            every {
                vehicleRepository.save(match {
                    it.id == vehicleId &&
                            it.driverId == george.id &&
                            it.brand == vehicle.brand &&
                            it.model == vehicle.model &&
                            it.licencePlate == vehicle.licencePlate &&
                            it.capacity == vehicle.capacity &&
                            it.iconUrl == "https://newicon.com" &&
                            it.color == vehicle.color
                })
            } returns vehicle.copy(iconUrl = "https://newicon.com")

            val response = handler.handle(command = PatchVehicleCommand(vehicle = vehicleDTO))

            Then("the response should be the expected") {
                response.vehicle.id.shouldBe(vehicleId)
                response.vehicle.driverId.shouldBe(george.id)
                response.vehicle.color.shouldBe(vehicle.color)
                response.vehicle.brand.shouldBe(vehicle.brand)
                response.vehicle.capacity.shouldBe(vehicle.capacity)
                response.vehicle.iconUrl.shouldBe(vehicle.iconUrl)
                response.vehicle.licencePlate.shouldBe(vehicle.licencePlate)
                response.vehicle.model.shouldBe(vehicle.model)
            }
        }
    }

})
