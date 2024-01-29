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


class ListVehiclesCommandResponse(val vehicles: List<VehicleDTO>) : CommandResponse

class ListVehiclesCommand(val driverId: UUID) : Command

class ListVehiclesCommandHandler(
    private val userRepository: RepositoryPort<User, UserCriteria>,
    private val vehicleRepository: RepositoryPort<Vehicle, VehicleCriteria>
) :
    CommandHandler<ListVehiclesCommand, ListVehiclesCommandResponse> {

    override fun handle(command: ListVehiclesCommand): ListVehiclesCommandResponse {
        User.checkIfEntitiesExist(
            userRepository, listOf(UserCriteria(id = command.driverId))
        )
        val vehicles = vehicleRepository.findAll(VehicleCriteria(driverId = command.driverId))
        return ListVehiclesCommandResponse(vehicles = vehicles.map { vehicle ->
            VehicleDTO.from(
                vehicle
            )
        })
    }
}