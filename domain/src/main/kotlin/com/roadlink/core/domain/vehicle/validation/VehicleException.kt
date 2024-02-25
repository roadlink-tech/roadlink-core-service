package com.roadlink.core.domain.vehicle.validation

import com.roadlink.core.domain.DomainException


sealed class VehicleException(override val message: String, val code: String, throwable: Throwable? = null) :
    DomainException(message, code, throwable) {

    class InvalidCapacity(brand: String, model: String, min: Int, max: Int) :
        VehicleException(
            message = "Vehicle $brand $model must have a capacity between $min and $max",
            code = "INVALID_CAPACITY"
        )

    class InvalidBrand(brand: String) :
        VehicleException(message = "The brand $brand is not available", code = "INVALID_BRAND")

    class EmptyMandatoryFields(missingFields: List<String>) :
        VehicleException(
            message = "The following mandatory fields are empty: $missingFields",
            code = "EMPTY_MANDATORY_FIELDS"
        )
}