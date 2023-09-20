package com.roadlink.core.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory
import org.springframework.boot.web.server.Compression
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*


@SpringBootApplication
class RoadlinkCoreServiceApplication

fun main(args: Array<String>) {

    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    SpringApplicationBuilder(RoadlinkCoreServiceApplication::class.java)
        .build()
        .run(*args)

}

@Configuration
internal class ServerDefinition {
    @Bean
    fun webServerFactory(
        @Value("\${server.port}") port: Int,
        @Value("\${server.netty.compression_enabled}") compressionEnabled: Boolean
    ): ConfigurableServletWebServerFactory {
        return JettyServletWebServerFactory().apply {
            this.port = 8080
            this.compression = Compression().apply {
                enabled = compressionEnabled
            }
        }
    }
}