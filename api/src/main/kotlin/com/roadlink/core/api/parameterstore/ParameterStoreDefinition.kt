package com.roadlink.core.api.parameterstore

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import com.roadlink.application.parameterstore.AwsParametersStoreService
import com.roadlink.application.parameterstore.LocalParametersStoreService
import com.roadlink.application.parameterstore.ParametersStoreService
import com.roadlink.core.api.Environment
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component


@Configuration
open class ParameterStoreDefinition {

    @Bean
    @Profile(Environment.cloud)
    open fun awsParameterStoreService(
        @Qualifier("aws_credentials") awsCredentials: AWSStaticCredentialsProvider,
        @Qualifier("aws_endpoint_configuration") endpointConfiguration: AwsClientBuilder.EndpointConfiguration,
    ): ParametersStoreService {
        val ssmClient = AWSSimpleSystemsManagementClientBuilder
            .standard()
            .withCredentials(awsCredentials)
            .build()
        return AwsParametersStoreService(ssmClient)
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