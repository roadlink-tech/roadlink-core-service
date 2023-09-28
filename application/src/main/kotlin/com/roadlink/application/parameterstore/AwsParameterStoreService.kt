package com.roadlink.application.parameterstore

import com.roadlink.core.infrastructure.InfrastructureException
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest
import com.amazonaws.services.simplesystemsmanagement.model.ParameterNotFoundException

sealed class AwsParametersStoreErrors(override val message: String, cause: Throwable? = null) :
    InfrastructureException(message, cause) {
    class NotFoundException(name: String) :
        AwsParametersStoreErrors("Parameter $name has not been defined in AWS")
}

class AwsParametersStoreService(private var client: AWSSimpleSystemsManagement) : ParametersStoreService {
    override fun getOrFail(name: String): String {
        try {
            val request = GetParameterRequest()
                .withName(name.trim())
                .withWithDecryption(true)
            return client.getParameter(request).parameter.value
        } catch (ex: ParameterNotFoundException) {
            throw AwsParametersStoreErrors.NotFoundException(name)
        }
    }
}