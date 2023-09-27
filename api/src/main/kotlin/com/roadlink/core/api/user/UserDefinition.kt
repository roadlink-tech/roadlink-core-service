package com.roadlink.core.api.user

import com.roadlink.application.command.CommandHandler
import com.roadlink.application.user.UserCreationCommand
import com.roadlink.application.user.UserCreationCommandHandler
import com.roadlink.application.user.UserCreationCommandResponse
import com.roadlink.core.domain.user.UserRepositoryPort
import com.roadlink.core.infrastructure.user.UserRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserDefinition {

    @Bean
    fun userRepository(): UserRepositoryPort {
        return UserRepositoryAdapter()
    }

    @Bean("user_creation_command_handler")
    fun userCreationCommandHandler(userRepositoryPort: UserRepositoryPort): CommandHandler<UserCreationCommand, UserCreationCommandResponse> {
        return UserCreationCommandHandler(userRepositoryPort)
    }
}