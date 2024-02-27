package com.roadlink.application.vehicle

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.vehicle.Vehicle
import com.roadlink.core.domain.vehicle.VehicleCriteria

class PatchVehicleCommandResponse(val vehicle: VehicleDTO) : CommandResponse

class PatchVehicleCommand(val vehicle: VehicleDTO) : Command

class PatchVehicleCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val vehicleRepository: RepositoryPort<Vehicle, VehicleCriteria>
) :
    CommandHandler<PatchVehicleCommand, PatchVehicleCommandResponse> {

    override fun handle(command: PatchVehicleCommand): PatchVehicleCommandResponse {
        User.checkIfEntitiesExist(
            userRepository, listOf(UserCriteria(id = command.vehicle.driverId))
        )
        vehicleRepository.findOrFail(VehicleCriteria(id = command.vehicle.id))
            .also { vehicle ->
                vehicle.merge(
                    color = command.vehicle.color,
                    iconUrl = command.vehicle.iconUrl,
                    capacity = command.vehicle.capacity,
                    brand = command.vehicle.brand,
                    model = command.vehicle.model,
                    licencePlate = command.vehicle.licencePlate
                ).save(vehicleRepository).also {
                    return PatchVehicleCommandResponse(vehicle = VehicleDTO.from(vehicle))
                }
            }
    }
}