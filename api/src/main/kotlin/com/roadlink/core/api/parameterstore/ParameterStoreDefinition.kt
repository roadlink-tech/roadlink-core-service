package com.roadlink.core.api.parameterstore

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import com.roadlink.application.parameterstore.AwsParametersStoreService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class ParameterStoreDefinition {

    @Primary
    @Bean("aws_credentials")
    internal fun awsCredentials(
        @Value("\${cloud.aws.credentials.accessKey}") accessKey: String,
        @Value("\${cloud.aws.credentials.secretKey}") secretKey: String,
    ): AWSStaticCredentialsProvider {
        val credentials = BasicAWSCredentials(accessKey, secretKey)
        return AWSStaticCredentialsProvider(credentials)
    }

    @Bean("aws_endpoint_configuration")
    internal fun awsEndpointConfiguration(
        @Value("\${cloud.aws.endpoint}") endpoint: String,
        @Value("\${cloud.aws.region.static}") region: String,
    ): AwsClientBuilder.EndpointConfiguration {
        return AwsClientBuilder.EndpointConfiguration(endpoint, region)
    }

    @Bean
    fun localSimpleSystemManagementClient(
        @Qualifier("aws_credentials") awsCredentials: AWSStaticCredentialsProvider,
        @Qualifier("aws_endpoint_configuration") endpointConfiguration: AwsClientBuilder.EndpointConfiguration,
    ): AWSSimpleSystemsManagement {
        return AWSSimpleSystemsManagementClientBuilder
            .standard()
            .withCredentials(awsCredentials)
            .withEndpointConfiguration(endpointConfiguration)
            .build()
    }

    @Bean("aws_parameter_store_service")
    fun awsParameterStoreService(simpleSystemManagementClient: AWSSimpleSystemsManagement): AwsParametersStoreService {
        return AwsParametersStoreService(simpleSystemManagementClient)
    }


}