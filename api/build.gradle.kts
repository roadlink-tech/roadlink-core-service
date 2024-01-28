plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "1.8.22"
    application
}

group = "com.roadlink.core.api"

val springDataDynamodbVersion = "5.1.0"
val awsJavaSdkDynamodbVersion = "2.22.5"
val jakartaServletApiVersion = "6.0.0"
val springmokkVersion = "4.0.2"
val validationApi = "2.0.1.Final"
val googleApiClientVersion = "1.33.0"
val jjwtVersion = "0.12.3"

ext["jakarta-servlet.version"] =
    "5.0.0" // This is needed if you want to use jetty instead of tomcat

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(project(":application"))
    implementation(project(":domain"))
    implementation(project(":infrastructure"))

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-jetty")

    // Datasource
    implementation("software.amazon.awssdk:dynamodb:$awsJavaSdkDynamodbVersion")
    implementation("software.amazon.awssdk:sts:$awsJavaSdkDynamodbVersion")
    // Google sdk
    implementation("com.google.api-client:google-api-client:$googleApiClientVersion")
    // Jwt
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.ninja-squad:springmockk:$springmokkVersion")
    testImplementation("jakarta.servlet:jakarta.servlet-api:$jakartaServletApiVersion")
}

application {
    mainClass.set("com.roadlink.core.api.RoadlinkCoreServiceApplicationKt")
}