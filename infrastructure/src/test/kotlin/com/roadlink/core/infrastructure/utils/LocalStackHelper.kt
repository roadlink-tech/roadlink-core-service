package com.roadlink.core.infrastructure.utils


import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import org.testcontainers.containers.BindMode.READ_WRITE
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB
import org.testcontainers.utility.DockerImageName

private const val BASE_CONTAINER_CLASSPATH = "/opt/code/localstack/"
private const val USER_DYNAMO_TABLE_CLASSPATH =
    "${BASE_CONTAINER_CLASSPATH}user-dynamo-table.yml"


object LocalStackHelper {
    private const val LOCALSTACK_IMAGE_VERSION = "localstack/localstack:1.3.0"

    fun containerWithDynamoDb(): LocalStackContainer {
        val localstackImage = DockerImageName.parse(LOCALSTACK_IMAGE_VERSION)
        return LocalStackContainer(localstackImage)
            .withServices(DYNAMODB)
            .withClasspathResourceMapping(
                "/cloudformation/user-dynamo-table.yml",
                USER_DYNAMO_TABLE_CLASSPATH,
                READ_WRITE
            )
    }

    fun createUserTableIn(
        container: LocalStackContainer,
    ) {
        container.execInContainer(
            "awslocal",
            "cloudformation",
            "create-stack",
            "--stack-name",
            "user-dynamo-table-stack",
            "--template-body",
            "file://$USER_DYNAMO_TABLE_CLASSPATH",
            "--region",
            container.region
        )
    }

    private fun awsStaticCredentialsProvider(container: LocalStackContainer): AWSStaticCredentialsProvider {
        val credentials = BasicAWSCredentials(container.accessKey, container.secretKey)
        return AWSStaticCredentialsProvider(credentials)
    }

    private fun endpointConfiguration(container: LocalStackContainer): AwsClientBuilder.EndpointConfiguration {
        val endpoint = "http://${container.host}:${container.firstMappedPort}"
        return AwsClientBuilder.EndpointConfiguration(endpoint, container.region)
    }

    fun dynamoMapper(container: LocalStackContainer): DynamoDBMapper {
        val config = endpointConfiguration(container)
        val credentials = awsStaticCredentialsProvider(container)
        return DynamoDBMapper(
            AmazonDynamoDBClientBuilder
                .standard()
                .withCredentials(credentials)
                .withEndpointConfiguration(
                    config
                )
                .build()
        )
    }
}