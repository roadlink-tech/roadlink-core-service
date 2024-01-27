package com.roadlink.core.api.user

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.parameterstore.ParametersStoreService
import com.roadlink.application.user.GoogleLoginCommand
import com.roadlink.application.user.GoogleLoginCommandHandler
import com.roadlink.application.user.GoogleLoginCommandResponse
import com.roadlink.core.domain.DefaultIdGenerator
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.JwtGenerator
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.google.GoogleIdTokenValidator
import com.roadlink.core.domain.user.google.GoogleUser
import com.roadlink.core.domain.user.google.GoogleUserCriteria
import com.roadlink.core.infrastructure.dynamodb.RepositoryAdapter
import com.roadlink.core.infrastructure.user.GoogleUserDynamoDbEntityMapper
import com.roadlink.core.infrastructure.user.GoogleUserDynamoDbQueryMapper
import com.roadlink.core.infrastructure.user.HttpGoogleIdTokenValidator
import com.roadlink.core.infrastructure.user.JwtJJWTGenerator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.security.KeyPairGenerator
import java.time.Clock
import java.util.*
import java.util.concurrent.TimeUnit

@Configuration
open class GoogleUserRepositoryDefinition {

    @Bean
    open fun googleUserRepository(dynamoDbClient: DynamoDbClient): RepositoryPort<GoogleUser, GoogleUserCriteria> {
        val dynamoEntityMapper = GoogleUserDynamoDbEntityMapper()
        val dynamoQueryMapper = GoogleUserDynamoDbQueryMapper()
        return RepositoryAdapter(
            dynamoDbClient,
            "RoadlinkCore",
            dynamoEntityMapper,
            dynamoQueryMapper
        )
    }
}

@Configuration
open class GoogleIdTokenValidatorDefinition {
    @Bean
    open fun googleCredentials(
        @Value("\${parameter_names.google.credentials}") googleCredentialsParameterName: String,
        objectMapper: ObjectMapper,
        parametersStoreService: ParametersStoreService
    ): GoogleCredentials {
        val credentials = parametersStoreService.getOrFail(googleCredentialsParameterName)
        return objectMapper.readValue(credentials, GoogleCredentials::class.java)
    }

    @Bean
    open fun googleIdTokenVerifier(
        googleCredentials: GoogleCredentials
    ): GoogleIdTokenVerifier {
        val transport = NetHttpTransport()
        val jsonFactory = GsonFactory()

        return GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(Collections.singletonList(googleCredentials.clientId))
            .build()
    }

    @Bean
    open fun googleIdTokenValidator(verifier: GoogleIdTokenVerifier): GoogleIdTokenValidator {
        return HttpGoogleIdTokenValidator(verifier)
    }
}

@Configuration
open class GoogleLoginDefinition {

    @Bean
    /**
     * TODO: documentar que es este clock, para que sirve
     */
    open fun clock(): Clock {
        return Clock.systemUTC()
    }

    @Bean
    open fun jwtGenerator(clock: Clock): JwtGenerator {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            .apply { initialize(2048) }

        val keyPair = keyPairGenerator.genKeyPair()

        val privateKey = keyPair.private

        return JwtJJWTGenerator(
            privateKey = privateKey,
            ttlTokenMillis = TimeUnit.HOURS.toMillis(2),
            clock = clock,
        )
    }

    @Bean("google_login_command_handler")
    open fun googleLoginCommandHandler(
        googleIdTokenValidator: GoogleIdTokenValidator,
        userRepositoryPort: RepositoryPort<User, UserCriteria>,
        googleUserRepositoryPort: RepositoryPort<GoogleUser, GoogleUserCriteria>,
        jwtGenerator: JwtGenerator
    ): CommandHandler<GoogleLoginCommand, GoogleLoginCommandResponse> {
        return GoogleLoginCommandHandler(googleIdTokenValidator, userRepositoryPort, googleUserRepositoryPort, DefaultIdGenerator(), jwtGenerator)
    }
}

data class GoogleCredentials(
    @JsonProperty("client_id")
    val clientId: String,
)
