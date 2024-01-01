package com.roadlink.core.domain.validation

import com.roadlink.core.domain.DomainEntity

interface Validation<T : DomainEntity> {
    fun execute(entity: T)
}