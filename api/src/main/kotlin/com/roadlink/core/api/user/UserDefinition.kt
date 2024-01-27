package com.roadlink.core.api.user

import com.roadlink.application.command.CommandHandler
import com.roadlink.application.user.*
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.RepositoryAdapter
import com.roadlink.core.infrastructure.user.UserDynamoDbEntityMapper
import com.roadlink.core.infrastructure.user.UserDynamoDbQueryMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
open class UserRepositoryDefinition {
    @Bean
    open fun userRepository(dynamoDbClient: DynamoDbClient): RepositoryPort<User, UserCriteria> {
        val dynamoEntityMapper = UserDynamoDbEntityMapper()
        val dynamoQueryMapper = UserDynamoDbQueryMapper()
        return RepositoryAdapter(
            dynamoDbClient,
            "RoadlinkCore",
            dynamoEntityMapper,
            dynamoQueryMapper
        )
    }
}

@Configuration
open class UserHandlerDefinition {

    @Bean("create_user_command_handler")
    open fun userCreationCommandHandler(userRepositoryPort: RepositoryPort<User, UserCriteria>): CommandHandler<CreateUserCommand, CreateUserCommandResponse> {
        return CreateUserCommandHandler(userRepositoryPort)
    }

    @Bean("retrieve_user_command_handler")
    open fun retrieveUserCommandHandler(userRepositoryPort: RepositoryPort<User, UserCriteria>): CommandHandler<RetrieveUserCommand, RetrieveUserCommandResponse> {
        return RetrieveUserCommandHandler(userRepositoryPort)
    }

    @Bean("search_user_command_handler")
    open fun searchUserCommandHandler(userRepositoryPort: RepositoryPort<User, UserCriteria>): CommandHandler<SearchUserCommand, SearchUserCommandResponse> {
        return SearchUserCommandHandler(userRepositoryPort)
    }
}