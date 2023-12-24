package com.roadlink.core.api.command

import com.roadlink.application.command.CommandBus
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.SimpleCommandBus
import com.roadlink.application.feedback.FeedbackCreationCommand
import com.roadlink.application.feedback.FeedbackCreationCommandResponse
import com.roadlink.application.user.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CommandBusDefinition {

    @Bean
    internal open fun commandBus(
        @Qualifier("user_creation_command_handler") userCreationCommandHandler: CommandHandler<UserCreationCommand, UserCreationCommandResponse>,
        @Qualifier("retrieve_user_command_handler") retrieveUserCommandHandler: CommandHandler<RetrieveUserCommand, RetrieveUserCommandResponse>,
        @Qualifier("feedback_creation_command_handler") feedbackCreationCommandHandler: CommandHandler<FeedbackCreationCommand, FeedbackCreationCommandResponse>
    ): CommandBus {
        return SimpleCommandBus().also {
            it.registerHandler(userCreationCommandHandler)
            it.registerHandler(feedbackCreationCommandHandler)
            it.registerHandler(retrieveUserCommandHandler)
        }
    }
}