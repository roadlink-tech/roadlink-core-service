group = "com.roadlink.core.infrastructure"

val kotestExtensionsTestcontainersVersion = "2.0.2"
val localstackVersion = "1.19.3"
val springDataDynamodbVersion = "5.1.0"
val awsJavaSdkDynamodbVersion = "2.22.5"

dependencies {
    implementation(project(":domain"))
    // DynamoDB
    implementation("software.amazon.awssdk:dynamodb:$awsJavaSdkDynamodbVersion")
    implementation("software.amazon.awssdk:sts:$awsJavaSdkDynamodbVersion")

    // Test
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:$kotestExtensionsTestcontainersVersion")
    testImplementation("org.testcontainers:localstack:$localstackVersion")
}