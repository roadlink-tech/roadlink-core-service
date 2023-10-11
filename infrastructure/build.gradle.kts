group = "com.roadlink.core.infrastructure"

val kotestExtensionsTestcontainersVersion = "2.0.2"
val localstackVersion = "1.17.6"
val springDataDynamodbVersion = "5.1.0"
val awsJavaSdkDynamodbVersion = "1.12.472"

dependencies {
    implementation(project(":domain"))
    // DynamoDB
    implementation("com.amazonaws:aws-java-sdk-dynamodb:$awsJavaSdkDynamodbVersion")
    implementation("com.github.derjust:spring-data-dynamodb:$springDataDynamodbVersion")
    // Test
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:$kotestExtensionsTestcontainersVersion")
    testImplementation("org.testcontainers:localstack:$localstackVersion")
}