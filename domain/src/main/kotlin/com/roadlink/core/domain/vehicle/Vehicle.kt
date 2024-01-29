package com.roadlink.core.domain.vehicle

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.validation.FeedbackValidationService
import com.roadlink.core.domain.vehicle.validation.VehicleValidationService
import java.util.*

data class Vehicle(
    val id: UUID,
    val driverId: UUID,
    val brand: String,
    val model: String,
    val licencePlate: String,
    val iconUrl: String,
    val capacity: Int,
    val color: String,
) : DomainEntity {
    fun save(repository: RepositoryPort<Vehicle, VehicleCriteria>): Vehicle {
        return repository.save(this)
    }

    init {
        VehicleValidationService().validate(this)
    }
}