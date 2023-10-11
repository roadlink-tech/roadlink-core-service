package com.roadlink.core.api.parameterstore

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.roadlink.application.parameterstore.AwsParametersStoreService
import com.roadlink.application.parameterstore.LocalParametersStoreService
import com.roadlink.application.parameterstore.ParametersStoreService
import com.roadlink.core.api.Environment
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component


@Configuration
open class ParameterStoreDefinition {

    @Bean
    @Profile(Environment.cloud)
    open fun awsParameterStoreService(simpleSystemManagementClient: AWSSimpleSystemsManagement): ParametersStoreService {
        return AwsParametersStoreService(simpleSystemManagementClient)
    }

    @Bean
    @Profile(Environment.local)
    open fun localParameterStoreService(
        localProperties: LocalProperties
    ): ParametersStoreService {
        return LocalParametersStoreService(localProperties.secrets)
    }

}

@Component
@ConfigurationProperties
class LocalProperties(var secrets: Map<String, String> = emptyMap())