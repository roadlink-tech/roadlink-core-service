package com.roadlink.core.api.datasource

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
        config.jdbcUrl = "jdbc:mysql://localhost:3306/roadlink_core_db"//rdsDatabaseCredentials.url
        config.username = rdsDatabaseCredentials.user
        config.password = rdsDatabaseCredentials.password
        return HikariDataSource(config)
    }

}

data class RdsDatabaseCredentials(
    val user: String,
    val password: String,
    val url: String
)
