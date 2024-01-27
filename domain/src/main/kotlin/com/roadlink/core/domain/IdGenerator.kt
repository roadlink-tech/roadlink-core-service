package com.roadlink.core.domain

import java.util.*

interface IdGenerator {
    fun next(): UUID
}

class DefaultIdGenerator : IdGenerator {
    override fun next(): UUID {
        return UUID.randomUUID()
    }
}