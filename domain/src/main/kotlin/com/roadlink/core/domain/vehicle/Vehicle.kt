package com.roadlink.core.domain.vehicle

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.RepositoryPort
import java.util.*

data class Vehicle(
    val id: UUID,
    val driverId: UUID,
    val brand: String,
    val model: String,
    val licencePlate: String,
    val iconUrl: String
) : DomainEntity  {
    fun save(repository: RepositoryPort<Vehicle, VehicleCriteria>): Vehicle {
        return repository.save(this)
    }
}