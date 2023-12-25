package com.roadlink.core.api.user

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.user.*
import com.roadlink.core.domain.user.UserRepositoryPort
import com.roadlink.core.infrastructure.user.UserRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UserDefinition {

    @Bean
    open fun userRepository(dynamoDBMapper: DynamoDBMapper): UserRepositoryPort {
        return UserRepositoryAdapter(dynamoDBMapper)
    }

    @Bean("user_creation_command_handler")
    open fun userCreationCommandHandler(userRepositoryPort: UserRepositoryPort): CommandHandler<UserCreationCommand, UserCreationCommandResponse> {
        return UserCreationCommandHandler(userRepositoryPort)
    }

    @Bean("retrieve_user_command_handler")
    open fun retrieveUserCommandHandler(userRepositoryPort: UserRepositoryPort): CommandHandler<RetrieveUserCommand, RetrieveUserCommandResponse> {
        return RetrieveUserCommandHandler(userRepositoryPort)
    }

    @Bean("search_user_command_handler")
    open fun searchUserCommandHandler(userRepositoryPort: UserRepositoryPort): CommandHandler<SearchUserCommand, SearchUserCommandResponse> {
        return SearchUserCommandHandler(userRepositoryPort)
    }
}