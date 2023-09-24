plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "1.8.22"
    application
}

group = "com.roadlink.core.api"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}


ext["jakarta-servlet.version"] =
    "5.0.0" // This is needed if you want to use jetty instead of tomcat

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(project(":application"))
    implementation(project(":domain"))
    implementation(project(":infrastructure"))

    // Junit
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-jetty")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

application {
    mainClass.set("com.roadlink.core.api.RoadlinkCoreServiceApplicationKt")  // Cambia esto a la clase principal de tu aplicación Spring
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}