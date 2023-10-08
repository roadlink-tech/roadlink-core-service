package com.roadlink.application.parameterstore

class LocalParametersStoreService(private val secrets: Map<String, String>) : ParametersStoreService {
    override fun getOrFail(name: String): String {
        return secrets[name]!!
    }
}

