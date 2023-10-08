package com.roadlink.application.parameterstore

class LocalParametersStoreService : ParametersStoreService {

    private val secrets =
        mapOf("/local/roadlink-core-service/dynamo/credentials" to "{\"endpoint\":\"http://localstack:4566\", \"region\": \"us-west-2\"}")

    override fun getOrFail(name: String): String {
        return secrets[name]!!
    }
}