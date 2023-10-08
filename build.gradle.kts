import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.22"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val awsSdkVersion = "1.12.429"
val kotestRunnerVersion = "5.5.5"
val kotestExtensionSpring = "1.1.2"

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "idea")

    group = "com.roadlink"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        testImplementation("io.kotest:kotest-runner-junit5:$kotestRunnerVersion")
        testImplementation("io.kotest.extensions:kotest-extensions-spring:$kotestExtensionSpring")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            showExceptions = true
            showStandardStreams = true
            events("passed", "skipped", "failed")
        }
    }
}

project(":api") {
    dependencies {
        // AWS
        implementation("com.amazonaws:aws-java-sdk-core:$awsSdkVersion")
        implementation("com.amazonaws:aws-java-sdk-ssm:$awsSdkVersion")
    }
}

project(":application") {
    dependencies {
        // AWS
        implementation("com.amazonaws:aws-java-sdk-core:$awsSdkVersion")
        implementation("com.amazonaws:aws-java-sdk-ssm:$awsSdkVersion")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}