package com.roadlink.core.domain.validation

import com.roadlink.core.domain.DomainEntity

interface ValidationService<T : DomainEntity> {
    val validations: List<Validation<T>>
    fun validate(entity: T)
}