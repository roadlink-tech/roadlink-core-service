package com.roadlink.core.api

/**
 * This enum represents the different environments in which the application can be run.
 *
 * The {@code LOCAL} environment is used for running the application on a developer's machine, typically for local testing and debugging.
 *
 * The {@code CLOUD} environment is used for running the application with the app and external infrastructure.
 *
 * @see <a href="src/main/resources/logging/logback-spring.xml">logback-spring.xml</a>
 */
object Environment {

    const val cloud = "cloud"
    const val local = "local"
}