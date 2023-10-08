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
    //"1.12.472"

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

    tasks.test {
        useJUnitPlatform {
            includeEngines("junit-jupiter")
        }
        testLogging {
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

tasks.withType<Test> {
    useJUnitPlatform()
}
