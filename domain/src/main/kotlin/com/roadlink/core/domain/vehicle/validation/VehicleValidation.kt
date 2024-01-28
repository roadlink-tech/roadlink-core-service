package com.roadlink.core.domain.vehicle.validation

import com.roadlink.core.domain.validation.BaseValidationService
import com.roadlink.core.domain.validation.Validation
import com.roadlink.core.domain.vehicle.Vehicle

private const val MIN_CAPACITY = 1
private const val MAX_CAPACITY = 6
private val AVAILABLE_BRANDS = listOf(
    "Chevrolet",
    "Ford",
    "Toyota",
    "Volkswagen",
    "Nissan",
    "Honda",
    "Fiat",
    "Peugeot",
    "Renault",
    "Hyundai",
    "Kia",
    "Mazda",
    "Mitsubishi",
    "Subaru",
    "Mercedes-Benz",
    "BMW",
    "Audi",
    "Jaguar",
    "Land Rover",
    "Volvo"
)

class VehicleValidationService(
    override val validations: List<Validation<Vehicle>> = listOf(
        MinAndMaxCapacity(MIN_CAPACITY, MAX_CAPACITY),
        AvailableBrands(AVAILABLE_BRANDS),
        MandatoryFields()
    )
) : BaseValidationService<Vehicle>()

class MinAndMaxCapacity(private val minCapacity: Int, private val maxCapacity: Int) : Validation<Vehicle> {
    override fun execute(entity: Vehicle) {
        if (entity.capacity < minCapacity || entity.capacity > maxCapacity) {
            throw VehicleException.InvalidCapacity(entity.brand, entity.model, 0, 5)
        }
    }
}

class AvailableBrands(private val availableBrands: List<String>) : Validation<Vehicle> {
    override fun execute(entity: Vehicle) {
        if (entity.brand.isNotEmpty() && !availableBrands.any { it.equals(entity.brand, ignoreCase = true) }) {
            throw VehicleException.InvalidBrand(entity.brand)
        }
    }
}

class MandatoryFields : Validation<Vehicle> {
    override fun execute(entity: Vehicle) {
        val missingFields = mutableListOf<String>()
        if (entity.brand.isEmpty()) {
            missingFields.add("brand")
        }
        if (entity.licencePlate.isEmpty()) {
            missingFields.add("licence_plate")
        }
        if (entity.model.isEmpty()) {
            missingFields.add("model")
        }
        if (missingFields.isNotEmpty()) {
            throw VehicleException.EmptyMandatoryFields(missingFields)
        }
    }
}