package com.roadlink.application


open class ApplicationException(message: String, throwable: Throwable? = null) :
    RuntimeException(message, throwable)