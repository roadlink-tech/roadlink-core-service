package com.roadlink.core.api.command

import com.roadlink.application.command.CommandBus
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.SimpleCommandBus
import com.roadlink.application.feedback.CreateFeedbackCommand
import com.roadlink.application.feedback.CreateFeedbackCommandResponse
import com.roadlink.application.feedback.RetrieveFeedbacksCommand
import com.roadlink.application.feedback.RetrieveFeedbacksCommandResponse
import com.roadlink.application.friend.*
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
        @Qualifier("create_user_command_handler") createUserCommandHandler: CommandHandler<CreateUserCommand, CreateUserCommandResponse>,
        @Qualifier("retrieve_user_command_handler") retrieveUserCommandHandler: CommandHandler<RetrieveUserCommand, RetrieveUserCommandResponse>,
        @Qualifier("search_user_command_handler") searchUserCommandHandler: CommandHandler<SearchUserCommand, SearchUserCommandResponse>,
        @Qualifier("feedback_creation_command_handler") createFeedbackCommandHandler: CommandHandler<CreateFeedbackCommand, CreateFeedbackCommandResponse>,
        @Qualifier("retrieve_feedbacks_command_handler") retrieveFeedbacksCommandHandler: CommandHandler<RetrieveFeedbacksCommand, RetrieveFeedbacksCommandResponse>,
        @Qualifier("user_trust_score_command_handler") retrieveUserTrustScoreCommandHandler: CommandHandler<RetrieveUserTrustScoreCommand, RetrieveUserTrustScoreCommandResponse>,
        @Qualifier("create_friendship_solicitude_command_handler") createFriendshipSolicitudeCommandHandler: CommandHandler<CreateFriendshipSolicitudeCommand, CreateFriendshipSolicitudeCommandResponse>,
        @Qualifier("accept_friendship_solicitude_command_handler") acceptFriendshipSolicitudeCommandHandler: CommandHandler<AcceptFriendshipSolicitudeCommand, AcceptFriendshipSolicitudeCommandResponse>,
        @Qualifier("list_friendship_solicitudes_command_handler") listFriendshipSolicitudesCommandHandler: CommandHandler<ListFriendshipSolicitudesCommand, ListFriendshipSolicitudesCommandResponse>,
        @Qualifier("reject_friendship_solicitude_command_handler") rejectFriendshipSolicitudeCommandHandler: CommandHandler<RejectFriendshipSolicitudeCommand, RejectFriendshipSolicitudeCommandResponse>,
        @Qualifier("list_friends_command_handler") listFriendsCommandHandler: CommandHandler<ListFriendsCommand, ListFriendsCommandResponse>,
        @Qualifier("delete_friend_command_handler") deleteFriendsCommandHandler: CommandHandler<DeleteFriendCommand, DeleteFriendCommandResponse>
    ): CommandBus {
        return SimpleCommandBus().also {
            it.registerHandler(createUserCommandHandler)
            it.registerHandler(createFeedbackCommandHandler)
            it.registerHandler(retrieveUserCommandHandler)
            it.registerHandler(searchUserCommandHandler)
            it.registerHandler(retrieveFeedbacksCommandHandler)
            it.registerHandler(retrieveUserTrustScoreCommandHandler)
            it.registerHandler(createFriendshipSolicitudeCommandHandler)
            it.registerHandler(acceptFriendshipSolicitudeCommandHandler)
            it.registerHandler(listFriendshipSolicitudesCommandHandler)
            it.registerHandler(rejectFriendshipSolicitudeCommandHandler)
            it.registerHandler(listFriendsCommandHandler)
            it.registerHandler(deleteFriendsCommandHandler)
        }
    }
}