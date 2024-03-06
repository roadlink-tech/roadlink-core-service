package com.roadlink.application.vehicle

import com.roadlink.application.command.Command
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.CommandResponse
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.vehicle.Vehicle
import com.roadlink.core.domain.vehicle.VehicleCriteria
import java.util.*


class GetVehicleCommandResponse(val vehicle: VehicleDTO) : CommandResponse

class GetVehicleCommand(val driverId: UUID, val vehicleId: UUID) : Command

class GetVehicleCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val vehicleRepository: RepositoryPort<Vehicle, VehicleCriteria>
) :
    CommandHandler<GetVehicleCommand, GetVehicleCommandResponse> {

    override fun handle(command: GetVehicleCommand): GetVehicleCommandResponse {
        User.checkIfEntitiesExist(
            userRepository, listOf(UserCriteria(id = command.driverId))
        )
        vehicleRepository.findOrFail(VehicleCriteria(id = command.vehicleId)).also {
            return GetVehicleCommandResponse(vehicle = VehicleDTO.from(it))
        }
    }
}