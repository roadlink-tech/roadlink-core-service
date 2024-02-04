group = "com.roadlink.core.infrastructure"

val kotestExtensionsTestcontainersVersion = "2.0.2"
val localstackVersion = "1.19.3"
val springDataDynamodbVersion = "5.1.0"
val awsJavaSdkDynamodbVersion = "2.22.5"
val googleApiClientVersion = "1.33.0"
val jjwtVersion = "0.12.3"

dependencies {
    implementation(project(":domain"))
    // DynamoDB
    implementation("software.amazon.awssdk:dynamodb:$awsJavaSdkDynamodbVersion")
    implementation("software.amazon.awssdk:sts:$awsJavaSdkDynamodbVersion")

    // Google sdk
    implementation("com.google.api-client:google-api-client:$googleApiClientVersion")
    // Jwt
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    // Test
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:$kotestExtensionsTestcontainersVersion")
    testImplementation("org.testcontainers:localstack:$localstackVersion")
}