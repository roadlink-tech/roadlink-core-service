package com.roadlink.core.api.command

import com.roadlink.application.command.CommandBus
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.SimpleCommandBus
import com.roadlink.application.feedback.FeedbackCreationCommand
import com.roadlink.application.feedback.FeedbackCreationCommandResponse
import com.roadlink.application.feedback.RetrieveFeedbacksCommand
import com.roadlink.application.feedback.RetrieveFeedbacksCommandHandler
import com.roadlink.application.feedback.RetrieveFeedbacksCommandResponse
import com.roadlink.application.friend.FriendshipSolicitudeCreationCommand
import com.roadlink.application.friend.FriendshipSolicitudeCreationCommandResponse
import com.roadlink.application.user.*
import com.roadlink.application.usertrustscore.RetrieveUserTrustScoreCommand
import com.roadlink.application.usertrustscore.RetrieveUserTrustScoreCommandResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CommandBusDefinition {

    @Bean
    internal open fun commandBus(
        @Qualifier("user_creation_command_handler") userCreationCommandHandler: CommandHandler<UserCreationCommand, UserCreationCommandResponse>,
        @Qualifier("retrieve_user_command_handler") retrieveUserCommandHandler: CommandHandler<RetrieveUserCommand, RetrieveUserCommandResponse>,
        @Qualifier("search_user_command_handler") searchUserCommandHandler: CommandHandler<SearchUserCommand, SearchUserCommandResponse>,
        @Qualifier("feedback_creation_command_handler") feedbackCreationCommandHandler: CommandHandler<FeedbackCreationCommand, FeedbackCreationCommandResponse>,
        @Qualifier("retrieve_feedbacks_command_handler") retrieveFeedbacksCommandHandler: CommandHandler<RetrieveFeedbacksCommand, RetrieveFeedbacksCommandResponse>,
        @Qualifier("user_trust_score_command_handler") retrieveUserTrustScoreCommandHandler: CommandHandler<RetrieveUserTrustScoreCommand, RetrieveUserTrustScoreCommandResponse>,
        @Qualifier("friendship_solicitude_creation_command_handler") friendshipSolicitudeCreationCommandHandler: CommandHandler<FriendshipSolicitudeCreationCommand, FriendshipSolicitudeCreationCommandResponse>

    ): CommandBus {
        return SimpleCommandBus().also {
            it.registerHandler(userCreationCommandHandler)
            it.registerHandler(feedbackCreationCommandHandler)
            it.registerHandler(retrieveUserCommandHandler)
            it.registerHandler(searchUserCommandHandler)
            it.registerHandler(retrieveFeedbacksCommandHandler)
            it.registerHandler(retrieveUserTrustScoreCommandHandler)
            it.registerHandler(friendshipSolicitudeCreationCommandHandler)
        }
    }
}