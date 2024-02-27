package com.roadlink.core.api.vehicle

import com.roadlink.application.command.CommandHandler
import com.roadlink.application.vehicle.*
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.vehicle.Vehicle
import com.roadlink.core.domain.vehicle.VehicleCriteria
import com.roadlink.core.infrastructure.dynamodb.RepositoryAdapter
import com.roadlink.core.infrastructure.vehicle.VehicleDynamoDbEntityMapper
import com.roadlink.core.infrastructure.vehicle.VehicleDynamoDbQueryMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
open class VehicleRepositoryDefinition {
    @Bean
    open fun vehicleRepository(dynamoDbClient: DynamoDbClient): RepositoryPort<Vehicle, VehicleCriteria> {
        val dynamoEntityMapper = VehicleDynamoDbEntityMapper()
        val dynamoQueryMapper = VehicleDynamoDbQueryMapper()
        return RepositoryAdapter(
            dynamoDbClient,
            "RoadlinkCore",
            dynamoEntityMapper,
            dynamoQueryMapper
        )
    }
}

@Configuration
open class VehicleHandlerDefinition {

    @Bean("create_vehicle_command_handler")
    open fun createVehicleCommandHandler(
        vehicleRepositoryPort: RepositoryPort<Vehicle, VehicleCriteria>,
        userRepositoryPort: RepositoryPort<User, UserCriteria>
    ): CommandHandler<CreateVehicleCommand, CreateVehicleCommandResponse> {
        return CreateVehicleCommandHandler(userRepositoryPort, vehicleRepositoryPort)
    }

    @Bean("list_vehicles_command_handler")
    open fun listVehiclesCommandHandler(
        vehicleRepositoryPort: RepositoryPort<Vehicle, VehicleCriteria>,
        userRepositoryPort: RepositoryPort<User, UserCriteria>
    ): CommandHandler<ListVehiclesCommand, ListVehiclesCommandResponse> {
        return ListVehiclesCommandHandler(userRepositoryPort, vehicleRepositoryPort)
    }

    @Bean("delete_vehicle_command_handler")
    open fun deleteVehicleCommandHandler(
        vehicleRepositoryPort: RepositoryPort<Vehicle, VehicleCriteria>,
        userRepositoryPort: RepositoryPort<User, UserCriteria>
    ): CommandHandler<DeleteVehicleCommand, DeleteVehicleCommandResponse> {
        return DeleteVehicleCommandHandler(userRepositoryPort, vehicleRepositoryPort)
    }

    @Bean("patch_vehicle_command_handler")
    open fun patchVehicleCommandHandler(
        vehicleRepositoryPort: RepositoryPort<Vehicle, VehicleCriteria>,
        userRepositoryPort: RepositoryPort<User, UserCriteria>
    ): CommandHandler<PatchVehicleCommand, PatchVehicleCommandResponse> {
        return PatchVehicleCommandHandler(userRepositoryPort, vehicleRepositoryPort)
    }
}