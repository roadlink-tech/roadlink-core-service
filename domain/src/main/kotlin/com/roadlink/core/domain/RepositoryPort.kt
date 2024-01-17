package com.roadlink.core.domain

interface RepositoryPort<T : DomainEntity, C : DomainCriteria> {
    fun save(entity: T): T
    fun saveAll(entities: List<T>): List<T>
    fun findOrFail(criteria: C): T
    fun findAll(criteria: C): List<T>
}

interface DomainCriteria