package com.roadlink.core.api.datasource

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.roadlink.application.parameterstore.ParametersStoreService
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class HikariConnectionPoolDefinition {


    @Bean
    fun rdsDatabaseCredentials(
        @Value("\${parameter_names.db.rds.credentials}") dbRdsCredentialsParameterName: String,
        parametersStoreService: ParametersStoreService
    ): RdsDatabaseCredentials {
        val objectMapper = ObjectMapper()
        val json = parametersStoreService.getOrFail(dbRdsCredentialsParameterName)
        return objectMapper.readValue(json, RdsDatabaseCredentials::class.java)
    }

    @Bean
    @Qualifier("customDataSource")
    fun customDataSource(rdsDatabaseCredentials: RdsDatabaseCredentials): DataSource {
        val config = HikariConfig()
        config.jdbcUrl = rdsDatabaseCredentials.url
        config.username = rdsDatabaseCredentials.user
        config.password = rdsDatabaseCredentials.password
        config.maximumPoolSize = 50
        config.minimumIdle = 50
        config.idleTimeout = 15000  //(0.25 min) maximum idle time for connection
        config.maxLifetime =
            30000  //(30 seg) maximum lifetime in milliseconds of a connection in the pool after it is closed.
        config.connectionTimeout = 2000 // timeout for waiting a connection from the pool
        return HikariDataSource(config)
    }
}

data class RdsDatabaseCredentials(
    @JsonProperty("user")
    val user: String,
    @JsonProperty("password")
    val password: String,
    @JsonProperty("url")
    val url: String
)
