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



    @Bean("aws_parameter_store_service")
    fun awsParameterStoreService(simpleSystemManagementClient: AWSSimpleSystemsManagement): AwsParametersStoreService {
        return AwsParametersStoreService(simpleSystemManagementClient)
    }


}