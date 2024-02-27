package com.roadlink.core.domain.vehicle

import com.roadlink.core.domain.DomainEntity
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.feedback.validation.FeedbackValidationService
import com.roadlink.core.domain.vehicle.validation.VehicleValidationService
import java.util.*

data class Vehicle(
    val id: UUID,
    val driverId: UUID,
    var brand: String,
    var model: String,
    var licencePlate: String,
    var iconUrl: String,
    var color: String,
    var capacity: Int,
) : DomainEntity {
    fun save(repository: RepositoryPort<Vehicle, VehicleCriteria>): Vehicle {
        return repository.save(this)
    }

    fun merge(
        brand: String = "",
        model: String = "",
        licencePlate: String = "",
        iconUrl: String = "",
        color: String = "",
        capacity: Int? = null,
    ): Vehicle {
        if (brand.isNotEmpty() && this.brand != brand) {
            this.brand = brand
        }
        if (model.isNotEmpty() && this.model != model) {
            this.model = model
        }
        if (licencePlate.isNotEmpty() && licencePlate != iconUrl) {
            this.licencePlate = licencePlate
        }
        if (iconUrl.isNotEmpty() && this.iconUrl != iconUrl) {
            this.iconUrl = iconUrl
        }
        if (color.isNotEmpty() && color != this.color) {
            this.color = color
        }
        if (capacity != null && capacity != this.capacity) {
            this.capacity = capacity
        }
        VehicleValidationService().validate(this)
        return this
    }

    init {
        VehicleValidationService().validate(this)
    }
}