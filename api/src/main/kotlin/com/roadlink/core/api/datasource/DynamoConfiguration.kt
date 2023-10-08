package com.roadlink.core.api.datasource

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.roadlink.application.parameterstore.ParametersStoreService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DynamoConfiguration {

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }

    @Bean("dynamo_credentials")
    fun dynamoCredentials(
        @Value("\${parameter_names.db.dynamo.credentials}") dynamoDbCredentialsParameterName: String,
        objectMapper: ObjectMapper,
        parametersStoreService: ParametersStoreService
    ): DynamoCredentials {
        val credentials = parametersStoreService.getOrFail(dynamoDbCredentialsParameterName)
        return objectMapper.readValue(credentials, DynamoCredentials::class.java)
    }

    @Bean
    fun amazonDynamoDB(
        @Qualifier("aws_credentials") amazonAWSCredentials: AWSStaticCredentialsProvider,
        @Qualifier("dynamo_credentials") dynamoCredentials: DynamoCredentials
    ): AmazonDynamoDB? {
        return AmazonDynamoDBClientBuilder
            .standard()
            .withCredentials(amazonAWSCredentials)
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(dynamoCredentials.endpoint, dynamoCredentials.region)
            )
            .build()
    }

    @Bean
    fun dynamoDBMapper(amazonDynamoDB: AmazonDynamoDB): DynamoDBMapper? {
        return DynamoDBMapper(amazonDynamoDB)
    }
}

data class DynamoCredentials(
    @JsonProperty("endpoint")
    val endpoint: String,
    @JsonProperty("region")
    val region: String
)

