package com.roadlink.application.vehicle

import com.roadlink.application.DefaultIdGenerator
import com.roadlink.core.domain.vehicle.Vehicle
import java.util.*

data class VehicleDTO(
    val id: UUID = DefaultIdGenerator().next(),
    val driverId: UUID,
    val brand: String,
    val model: String,
    val licencePlate: String,
    val iconUrl: String,
    val capacity: Int
) {

    fun toDomain(): Vehicle {
        return Vehicle(
            id = id,
            brand = brand,
            model = model,
            licencePlate = licencePlate,
            iconUrl = iconUrl,
            driverId = driverId,
            capacity = capacity
        )
    }

    companion object {
        fun from(vehicle: Vehicle): VehicleDTO {
            return VehicleDTO(
                id = vehicle.id,
                brand = vehicle.brand,
                model = vehicle.model,
                licencePlate = vehicle.licencePlate,
                iconUrl = vehicle.iconUrl,
                driverId = vehicle.driverId,
                capacity = vehicle.capacity
            )
        }
    }
}
