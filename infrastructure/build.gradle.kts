plugins {
    id("java")
}

group = "com.roadlink.core.infrastructure"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))
    //implementation("software.amazon.awssdk:dynamodb:2.20.68")

    // DynamoDB
    implementation("com.amazonaws:aws-java-sdk-dynamodb:1.11.573")
    implementation("com.github.derjust:spring-data-dynamodb:5.1.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}