package com.roadlink.core.domain.validation

import com.roadlink.core.domain.DomainEntity

abstract class BaseValidationService<T : DomainEntity> : ValidationService<T> {
    override fun validate(entity: T) {
        validations.forEach {
            it.execute(entity)
        }
    }
}