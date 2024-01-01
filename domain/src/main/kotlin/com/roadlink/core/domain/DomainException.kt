package com.roadlink.core.domain

open class DomainException(message: String, throwable: Throwable? = null) :
    RuntimeException(message, throwable)