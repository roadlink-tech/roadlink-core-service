package com.roadlink.application.vehicle

import com.roadlink.core.domain.vehicle.Vehicle
import java.util.UUID

object VehicleFactory {

    fun common(
        id: UUID = UUID.randomUUID(),
        driverId: UUID = UUID.randomUUID(),
        brand: String = "Ford",
        model: String = "Territory",
        licencePlate: String = "AF123AF",
        iconUrl: String = "http://icon.url",
        capacity: Int = 4,
        color: String = "White"
    ): Vehicle {
        return Vehicle(
            id = id,
            driverId = driverId,
            brand = brand,
            model = model,
            licencePlate = licencePlate,
            iconUrl = iconUrl,
            capacity = capacity,
            color = color
        )
    }
}