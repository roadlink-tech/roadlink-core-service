package com.roadlink.core.infrastructure.utils


import org.testcontainers.containers.BindMode.READ_WRITE
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.localstack.LocalStackContainer.Service.CLOUDFORMATION
import org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

private const val BASE_CONTAINER_CLASSPATH = "/opt/code/localstack/"
private const val USER_DYNAMO_TABLE_CLASSPATH =
    "${BASE_CONTAINER_CLASSPATH}user-dynamo-table.yml"
private const val CLEAR_DYNAMO_TABLE_CLASSPATH = "${BASE_CONTAINER_CLASSPATH}clear-dynamo-table.sh"


object LocalStackHelper {
    private const val LOCALSTACK_IMAGE_VERSION = "localstack/localstack:1.3.0"

    fun containerWithDynamoDb(): LocalStackContainer {
        val localstackImage = DockerImageName.parse(LOCALSTACK_IMAGE_VERSION)
        return LocalStackContainer(localstackImage)
            .withServices(DYNAMODB, CLOUDFORMATION)
            .withClasspathResourceMapping(
                "/cloudformation/user-dynamo-table.yml",
                USER_DYNAMO_TABLE_CLASSPATH,
                READ_WRITE
            )
            .withClasspathResourceMapping(
                "/scripts/clear-dynamo-table.sh",
                CLEAR_DYNAMO_TABLE_CLASSPATH,
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

    fun clearDynamoTableIn(
        container: LocalStackContainer
    ) {
        container.execInContainer(
            "sh",
            "clear-dynamo-table.sh"
        )
        Thread.sleep(500)
    }

    private fun awsStaticCredentialsProvider(container: LocalStackContainer): StaticCredentialsProvider? {
        val credentials = AwsBasicCredentials.create(container.accessKey, container.secretKey)
        return StaticCredentialsProvider.create(credentials)
    }

    fun dynamoDbClient(container: LocalStackContainer): DynamoDbClient {
        val credentials = awsStaticCredentialsProvider(container)
        val endpoint = "http://${container.host}:${container.firstMappedPort}"
        return DynamoDbClient.builder()
            .region(Region.of(container.region))
            .credentialsProvider(credentials)
            .endpointOverride(URI(endpoint))
            .build()
    }
}