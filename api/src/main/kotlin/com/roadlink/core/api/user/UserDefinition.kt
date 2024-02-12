package com.roadlink.core.api.user

import com.roadlink.application.command.CommandHandler
import com.roadlink.application.user.*
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.user.DefaultUserNameGenerator
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.domain.user.UserNameGenerator
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

    @Bean("user_name_generator")
    open fun userNameGenerator(userRepositoryPort: RepositoryPort<User, UserCriteria>): UserNameGenerator {
        return DefaultUserNameGenerator(userRepository = userRepositoryPort)
    }

    @Bean("create_user_command_handler")
    open fun userCreationCommandHandler(
        userRepositoryPort: RepositoryPort<User, UserCriteria>,
        userNameGenerator: UserNameGenerator
    ): CommandHandler<CreateUserCommand, CreateUserCommandResponse> {
        return CreateUserCommandHandler(userRepositoryPort, userNameGenerator)
    }

    @Bean("retrieve_user_command_handler")
    open fun retrieveUserCommandHandler(userRepositoryPort: RepositoryPort<User, UserCriteria>): CommandHandler<RetrieveUserCommand, RetrieveUserCommandResponse> {
        return RetrieveUserCommandHandler(userRepositoryPort)
    }

    @Bean("search_user_command_handler")
    open fun searchUserCommandHandler(userRepositoryPort: RepositoryPort<User, UserCriteria>): CommandHandler<SearchUserCommand, SearchUserCommandResponse> {
        return SearchUserCommandHandler(userRepositoryPort)
    }

    @Bean("patch_user_command_handler")
    open fun patchUserCommandHandler(userRepositoryPort: RepositoryPort<User, UserCriteria>): CommandHandler<PatchUserCommand, PatchUserCommandResponse> {
        return PatchUserCommandHandler(userRepositoryPort)
    }
}