package com.roadlink.core.api.cloud

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
open class AwsConfiguration {

    @Primary
    @Bean("aws_credentials")
    internal open fun awsCredentials(
        @Value("\${cloud.aws.credentials.accessKey}") accessKey: String,
        @Value("\${cloud.aws.credentials.secretKey}") secretKey: String,
    ): AWSStaticCredentialsProvider {
        val credentials = BasicAWSCredentials(accessKey, secretKey)
        return AWSStaticCredentialsProvider(credentials)
    }

    @Bean("aws_endpoint_configuration")
    internal open fun awsEndpointConfiguration(
        @Value("\${cloud.aws.endpoint}") endpoint: String,
        @Value("\${cloud.aws.region.static}") region: String,
    ): AwsClientBuilder.EndpointConfiguration {
        return AwsClientBuilder.EndpointConfiguration(endpoint, region)
    }

    @Bean
    open fun localSimpleSystemManagementClient(
        @Qualifier("aws_credentials") awsCredentials: AWSStaticCredentialsProvider,
        @Qualifier("aws_endpoint_configuration") endpointConfiguration: AwsClientBuilder.EndpointConfiguration,
    ): AWSSimpleSystemsManagement {
        return AWSSimpleSystemsManagementClientBuilder
            .standard()
            .withCredentials(awsCredentials)
            .withEndpointConfiguration(endpointConfiguration)
            .build()
    }
}