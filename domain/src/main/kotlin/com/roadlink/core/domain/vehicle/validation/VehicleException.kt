package com.roadlink.core.domain.vehicle.validation

import com.roadlink.core.domain.DomainException


sealed class VehicleException(override val message: String, throwable: Throwable? = null) :
    DomainException(message, throwable) {

    class InvalidCapacity(brand: String, model: String, min: Int, max: Int) :
        VehicleException("Vehicle $brand $model must have a capacity between $min and $max")

    class InvalidBrand(brand: String) : VehicleException("The brand $brand is not available")

    class EmptyMandatoryFields(missingFields: List<String>) :
        VehicleException("The following mandatory fields are empty: $missingFields")
}