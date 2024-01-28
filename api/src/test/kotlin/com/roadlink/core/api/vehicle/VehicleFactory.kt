package com.roadlink.core.api.vehicle

import com.roadlink.core.domain.vehicle.Vehicle
import java.util.*

object VehicleFactory {

    fun common(
        id: UUID = UUID.randomUUID(),
        driverId: UUID = UUID.randomUUID(),
        brand: String = "Ford",
        model: String = "Territory",
        licencePlate: String = "AG154AG",
        iconUrl: String = "https://icon.com",
        capacity: Int = 5,
    ): Vehicle {
        return Vehicle(
            id = id,
            driverId = driverId,
            brand = brand,
            model = model,
            licencePlate = licencePlate,
            iconUrl = iconUrl,
            capacity = capacity
        )
    }
}