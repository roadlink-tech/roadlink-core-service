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

class DeleteVehicleCommandResponse(val vehicleId: UUID) : CommandResponse

class DeleteVehicleCommand(val driverId: UUID, val vehicleId: UUID) : Command

class DeleteVehicleCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val vehicleRepository: RepositoryPort<Vehicle, VehicleCriteria>
) :
    CommandHandler<DeleteVehicleCommand, DeleteVehicleCommandResponse> {

    override fun handle(command: DeleteVehicleCommand): DeleteVehicleCommandResponse {
        User.checkIfEntitiesExist(
            userRepository, listOf(UserCriteria(id = command.driverId))
        )
        vehicleRepository.delete(VehicleCriteria(id = command.vehicleId))
        return DeleteVehicleCommandResponse(vehicleId = command.vehicleId)
    }
}