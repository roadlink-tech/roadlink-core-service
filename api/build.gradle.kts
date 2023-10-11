plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "1.8.22"
    application
}

group = "com.roadlink.core.api"

val springDataDynamodbVersion = "5.1.0"

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
    implementation("com.github.derjust:spring-data-dynamodb:$springDataDynamodbVersion")
    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

application {
    mainClass.set("com.roadlink.core.api.RoadlinkCoreServiceApplicationKt")
}