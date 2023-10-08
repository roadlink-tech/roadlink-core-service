package com.roadlink.core.api.parameterstore

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.roadlink.application.parameterstore.AwsParametersStoreService
import com.roadlink.application.parameterstore.LocalParametersStoreService
import com.roadlink.application.parameterstore.ParametersStoreService
import com.roadlink.core.api.Environment
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class ParameterStoreDefinition {

    @Bean
    @Profile(Environment.cloud)
    fun awsParameterStoreService(simpleSystemManagementClient: AWSSimpleSystemsManagement): ParametersStoreService {
        return AwsParametersStoreService(simpleSystemManagementClient)
    }

    @Bean
    @Profile(Environment.local)
    fun localParameterStoreService(): ParametersStoreService {
        return LocalParametersStoreService()
    }

}