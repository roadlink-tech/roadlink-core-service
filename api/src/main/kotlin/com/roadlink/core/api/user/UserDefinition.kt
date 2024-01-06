package com.roadlink.core.api.user

import com.roadlink.application.command.CommandHandler
import com.roadlink.application.user.*
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.user.UserRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
open class UserDefinition {

    @Bean
    open fun userRepository(dynamoDbClient: DynamoDbClient): RepositoryPort<User, UserCriteria> {
        return UserRepositoryAdapter(dynamoDbClient)
    }

    @Bean("user_creation_command_handler")
    open fun userCreationCommandHandler(userRepositoryPort: RepositoryPort<User, UserCriteria>): CommandHandler<UserCreationCommand, UserCreationCommandResponse> {
        return UserCreationCommandHandler(userRepositoryPort)
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