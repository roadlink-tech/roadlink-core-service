package com.roadlink.core.api.datasource

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.roadlink.application.parameterstore.ParametersStoreService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

@Configuration
open class DynamoConfiguration {

    @Bean
    open fun objectMapper(): ObjectMapper {
        return ObjectMapper().registerKotlinModule()
    }

    @Bean("dynamo_credentials")
    open fun dynamoCredentials(
        @Value("\${parameter_names.db.dynamo.credentials}") dynamoDbCredentialsParameterName: String,
        objectMapper: ObjectMapper,
        parametersStoreService: ParametersStoreService
    ): DynamoCredentials {
        val credentials = parametersStoreService.getOrFail(dynamoDbCredentialsParameterName)
        return objectMapper.readValue(credentials, DynamoCredentials::class.java)
    }

    @Bean
    open fun dynamoDbClient(
        @Qualifier("aws_credentials") awsCredentials: AWSStaticCredentialsProvider,
        @Qualifier("dynamo_credentials") dynamoCredentials: DynamoCredentials
    ): DynamoDbClient? {
        val credentials = AwsBasicCredentials.create("accessKey", "secretKey")
        return DynamoDbClient.builder()
            .region(Region.of(dynamoCredentials.region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .endpointOverride(URI(dynamoCredentials.endpoint))
            .build()
    }

}

data class DynamoCredentials(
    @JsonProperty("endpoint")
    val endpoint: String,
    @JsonProperty("region")
    val region: String
)

