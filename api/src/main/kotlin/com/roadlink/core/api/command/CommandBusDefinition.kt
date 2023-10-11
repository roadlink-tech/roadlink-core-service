package com.roadlink.core.api.command

import com.roadlink.application.command.CommandBus
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.SimpleCommandBus
import com.roadlink.application.user.UserCreationCommand
import com.roadlink.application.user.UserCreationCommandHandler
import com.roadlink.application.user.UserCreationCommandResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CommandBusDefinition {

    @Bean
    internal open fun commandBus(
        @Qualifier("user_creation_command_handler") userCreationCommandHandler: CommandHandler<UserCreationCommand, UserCreationCommandResponse>
    ): CommandBus {
        return SimpleCommandBus().also {
            it.registerHandler(userCreationCommandHandler)
        }
    }
}