package com.roadlink.core.infrastructure

open class InfrastructureException(message: String,  code: String, throwable: Throwable? = null) : RuntimeException(message, throwable)