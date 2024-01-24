package com.roadlink.core.infrastructure.vehicle

import com.roadlink.core.domain.vehicle.Vehicle
import java.util.UUID

object VehicleFactory {

    fun common(
        id: UUID = UUID.randomUUID(),
        brand: String = "Ford",
        model: String = "Territory",
        licencePlate: String = "AG123AG",
        iconUrl: String = "https://icon.com",
        driverId: UUID = UUID.randomUUID()
    ): Vehicle {
        return Vehicle(
            id = id,
            brand = brand,
            model = model,
            licencePlate = licencePlate,
            iconUrl = iconUrl,
            driverId = driverId
        )
    }

}