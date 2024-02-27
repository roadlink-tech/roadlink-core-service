package com.roadlink.core.api.vehicle.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.vehicle.*
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/users/{userId}/vehicles")
class RestVehicleController(private val commandBus: CommandBus) {

    @PostMapping
    @ResponseBody
    @ResponseStatus(value = CREATED)
    fun create(
        @PathVariable("userId") driverId: String,
        @RequestBody vehicle: VehicleCreationRequest
    ): VehicleResponse {
        val response =
            commandBus.publish<CreateVehicleCommand, CreateVehicleCommandResponse>(
                CreateVehicleCommand(
                    vehicle.toDto(
                        driverId
                    )
                )
            )
        return VehicleResponse.from(response.vehicle)
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(value = OK)
    fun list(@PathVariable("userId") driverId: String): List<VehicleResponse> {
        val response =
            commandBus.publish<ListVehiclesCommand, ListVehiclesCommandResponse>(
                ListVehiclesCommand(
                    driverId = UUID.fromString(driverId)
                )
            )
        return response.vehicles.map { vehicle -> VehicleResponse.from(vehicle) }
    }

    @DeleteMapping("/{vehicleId}")
    @ResponseBody
    @ResponseStatus(value = OK)
    fun delete(
        @PathVariable("userId") driverId: String,
        @PathVariable("vehicleId") vehicleId: String
    ) {
        commandBus.publish<DeleteVehicleCommand, DeleteVehicleCommandResponse>(
            DeleteVehicleCommand(
                driverId = UUID.fromString(driverId),
                vehicleId = UUID.fromString(vehicleId)
            )
        )
    }

    @PatchMapping("/{vehicleId}")
    @ResponseBody
    @ResponseStatus(value = OK)
    fun patch(
        @PathVariable("userId") driverId: String,
        @PathVariable("vehicleId") vehicleId: String,
        @RequestBody body: PatchVehicleRequest
    ): VehicleResponse {
        val response = commandBus.publish<PatchVehicleCommand, PatchVehicleCommandResponse>(
            PatchVehicleCommand(body.toDto(driverId = driverId, vehicleId = vehicleId))
        )
        return VehicleResponse.from(response.vehicle)
    }
}

data class PatchVehicleRequest(
    @JsonProperty("brand")
    val brand: String,
    @JsonProperty("model")
    val model: String,
    @JsonProperty("licence_plate")
    val licencePlate: String,
    @JsonProperty("icon_url")
    val iconUrl: String,
    @JsonProperty("capacity")
    val capacity: Int,
    @JsonProperty("color")
    val color: String,
) {
    fun toDto(driverId: String, vehicleId: String): VehicleDTO {
        return VehicleDTO(
            id = UUID.fromString(vehicleId),
            driverId = UUID.fromString(driverId),
            brand = brand,
            model = model,
            licencePlate = licencePlate,
            iconUrl = iconUrl,
            capacity = capacity,
            color = color
        )
    }
}

data class VehicleCreationRequest(
    @JsonProperty("brand", required = true)
    val brand: String,
    @JsonProperty("model")
    val model: String,
    @JsonProperty("licence_plate")
    val licencePlate: String,
    @JsonProperty("icon_url")
    val iconUrl: String,
    @JsonProperty("capacity")
    val capacity: Int,
    @JsonProperty("color")
    val color: String,
) {
    fun toDto(driverId: String): VehicleDTO {
        return VehicleDTO(
            driverId = UUID.fromString(driverId),
            brand = brand,
            model = model,
            licencePlate = licencePlate,
            iconUrl = iconUrl,
            capacity = capacity,
            color = color
        )
    }
}

data class VehicleResponse(
    @JsonProperty("id")
    val id: UUID,
    @JsonProperty("brand")
    val brand: String,
    @JsonProperty("model")
    val model: String,
    @JsonProperty("licence_plate")
    val licencePlate: String,
    @JsonProperty("icon_url")
    val iconUrl: String,
    @JsonProperty("capacity")
    val capacity: Int,
    @JsonProperty("color")
    val color: String,
) {
    companion object {
        fun from(vehicle: VehicleDTO): VehicleResponse {
            return VehicleResponse(
                id = vehicle.id,
                brand = vehicle.brand,
                model = vehicle.model,
                licencePlate = vehicle.licencePlate,
                iconUrl = vehicle.iconUrl,
                capacity = vehicle.capacity,
                color = vehicle.color
            )
        }
    }
}