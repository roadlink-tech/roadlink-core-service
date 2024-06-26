import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val awsSdkVersion = "1.12.429"
val kotestRunnerVersion = "5.5.5"
val kotestExtensionSpringVersion = "1.1.2"
val mockkVersion = "1.13.4"
val coroutinesVersion = "1.6.0"
allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")

    group = "com.roadlink"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }

    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        testImplementation("io.kotest:kotest-runner-junit5:$kotestRunnerVersion")
        testImplementation("io.kotest.extensions:kotest-extensions-spring:$kotestExtensionSpringVersion")
        testImplementation("io.mockk:mockk:$mockkVersion")
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
        implementation("com.amazonaws:aws-java-sdk-core:$awsSdkVersion")
        implementation("com.amazonaws:aws-java-sdk-ssm:$awsSdkVersion")
    }
}

project(":application") {
    dependencies {
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