package com.roadlink.application.parameterstore

interface ParametersStoreService {
    fun getOrFail(name: String): String
}