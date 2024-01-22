package com.roadlink.core.api.vehicle.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.roadlink.application.command.CommandBus
import com.roadlink.application.user.UserDTO
import com.roadlink.application.vehicle.CreateVehicleCommand
import com.roadlink.application.vehicle.CreateVehicleCommandResponse
import com.roadlink.application.vehicle.VehicleDTO
import com.roadlink.core.api.user.controller.UserResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/users/{userId}/vehicles")
class RestVehicleController(private val commandBus: CommandBus) {

    @PostMapping
    @ResponseBody
    @ResponseStatus(value = HttpStatus.CREATED)
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

}

data class VehicleCreationRequest(
    @JsonProperty("brand")
    val brand: String,
    @JsonProperty("model")
    val model: String,
    @JsonProperty("licence_plate")
    val licencePlate: String,
    @JsonProperty("icon_url")
    val iconUrl: String
) {
    fun toDto(driverId: String): VehicleDTO {
        return VehicleDTO(
            driverId = UUID.fromString(driverId),
            brand = brand,
            model = model,
            licencePlate = licencePlate,
            iconUrl = iconUrl
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
    val iconUrl: String
) {
    companion object {
        fun from(vehicle: VehicleDTO): VehicleResponse {
            return VehicleResponse(
                id = vehicle.id,
                brand = vehicle.brand,
                model = vehicle.model,
                licencePlate = vehicle.licencePlate,
                iconUrl = vehicle.iconUrl
            )
        }
    }
}