plugins {
    id("java")
    id("org.jetbrains.kotlin.plugin.noarg") version "1.4.10"

}
noArg {
    annotation("software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean")
}

group = "com.roadlink.core.infrastructure"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))

    // DynamoDB
    implementation("com.amazonaws:aws-java-sdk-dynamodb:1.11.573")
    implementation("com.github.derjust:spring-data-dynamodb:5.1.0")

    // Test
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    testImplementation("org.testcontainers:localstack:1.17.6")
    implementation("com.github.derjust:spring-data-dynamodb:5.1.0")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}