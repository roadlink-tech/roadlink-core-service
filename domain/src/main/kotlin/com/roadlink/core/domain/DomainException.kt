package com.roadlink.core.domain

open class DomainException(message: String, code: String, throwable: Throwable? = null) :
    RuntimeException(message, throwable)