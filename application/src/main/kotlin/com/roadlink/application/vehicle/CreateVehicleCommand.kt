package com.roadlink.application.vehicle

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.vehicle.Vehicle
import com.roadlink.core.domain.vehicle.VehicleCriteria


class CreateVehicleCommandResponse(val vehicle: VehicleDTO) : CommandResponse

class CreateVehicleCommand(val vehicle: VehicleDTO) : Command

class CreateVehicleCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val vehicleRepository: RepositoryPort<Vehicle, VehicleCriteria>
) :
    CommandHandler<CreateVehicleCommand, CreateVehicleCommandResponse> {

    override fun handle(command: CreateVehicleCommand): CreateVehicleCommandResponse {
        User.checkIfEntitiesExist(
            userRepository, listOf(UserCriteria(id = command.vehicle.driverId))
        )
        val vehicle = command.vehicle.toDomain().save(vehicleRepository)
        return CreateVehicleCommandResponse(vehicle = VehicleDTO.from(vehicle))
    }
}