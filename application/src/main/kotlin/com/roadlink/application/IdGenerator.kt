package com.roadlink.application

import java.util.*

interface IdGenerator {
    fun next(): UUID
}

class DefaultIdGenerator : IdGenerator {
    override fun next(): UUID {
        return UUID.randomUUID()
    }
}