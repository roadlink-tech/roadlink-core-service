package com.roadlink.core.api.command

import com.roadlink.application.command.CommandBus
import com.roadlink.application.command.CommandHandler
import com.roadlink.application.command.SimpleCommandBus
import com.roadlink.application.feedback.CreateFeedbackCommand
import com.roadlink.application.feedback.CreateFeedbackCommandResponse
import com.roadlink.application.feedback.ListFeedbacksCommand
import com.roadlink.application.feedback.ListFeedbacksCommandResponse
import com.roadlink.application.friend.*
import com.roadlink.application.user.*
import com.roadlink.application.usertrustscore.RetrieveUserTrustScoreCommand
import com.roadlink.application.usertrustscore.RetrieveUserTrustScoreCommandResponse
import com.roadlink.application.vehicle.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CommandBusDefinition {

    @Bean
    internal open fun commandBus(
        // User
        @Qualifier("create_user_command_handler") createUserCommandHandler: CommandHandler<CreateUserCommand, CreateUserCommandResponse>,
        @Qualifier("patch_user_command_handler") patchUserCommandHandler: CommandHandler<PatchUserCommand, PatchUserCommandResponse>,
        @Qualifier("retrieve_user_command_handler") retrieveUserCommandHandler: CommandHandler<RetrieveUserCommand, RetrieveUserCommandResponse>,
        @Qualifier("search_user_command_handler") searchUserCommandHandler: CommandHandler<SearchUserCommand, SearchUserCommandResponse>,
        @Qualifier("retrieve_user_trust_score_command_handler") retrieveUserTrustScoreCommandHandler: CommandHandler<RetrieveUserTrustScoreCommand, RetrieveUserTrustScoreCommandResponse>,
        @Qualifier("google_login_command_handler") googleLoginCommandHandler: CommandHandler<GoogleLoginCommand, GoogleLoginCommandResponse>,
        // Feedback
        @Qualifier("create_feedback_command_handler") createFeedbackCommandHandler: CommandHandler<CreateFeedbackCommand, CreateFeedbackCommandResponse>,
        @Qualifier("retrieve_feedbacks_command_handler") listFeedbacksCommandHandler: CommandHandler<ListFeedbacksCommand, ListFeedbacksCommandResponse>,
        // Friend
        @Qualifier("create_friendship_solicitude_command_handler") createFriendshipSolicitudeCommandHandler: CommandHandler<CreateFriendshipSolicitudeCommand, CreateFriendshipSolicitudeCommandResponse>,
        @Qualifier("accept_friendship_solicitude_command_handler") acceptFriendshipSolicitudeCommandHandler: CommandHandler<AcceptFriendshipSolicitudeCommand, AcceptFriendshipSolicitudeCommandResponse>,
        @Qualifier("list_friendship_solicitudes_command_handler") listFriendshipSolicitudesCommandHandler: CommandHandler<ListFriendshipSolicitudesCommand, ListFriendshipSolicitudesCommandResponse>,
        @Qualifier("reject_friendship_solicitude_command_handler") rejectFriendshipSolicitudeCommandHandler: CommandHandler<RejectFriendshipSolicitudeCommand, RejectFriendshipSolicitudeCommandResponse>,
        @Qualifier("list_friends_command_handler") listFriendsCommandHandler: CommandHandler<ListFriendsCommand, ListFriendsCommandResponse>,
        @Qualifier("delete_friend_command_handler") deleteFriendsCommandHandler: CommandHandler<DeleteFriendCommand, DeleteFriendCommandResponse>,
        // Vehicle
        @Qualifier("create_vehicle_command_handler") createVehicleCommandHandler: CommandHandler<CreateVehicleCommand, CreateVehicleCommandResponse>,
        @Qualifier("list_vehicles_command_handler") listVehiclesCommandHandler: CommandHandler<ListVehiclesCommand, ListVehiclesCommandResponse>,
        @Qualifier("delete_vehicle_command_handler") deleteVehicleCommandHandler: CommandHandler<DeleteVehicleCommand, DeleteVehicleCommandResponse>
    ): CommandBus {
        return SimpleCommandBus().also {
            // User
            it.registerHandler(createUserCommandHandler)
            it.registerHandler(patchUserCommandHandler)
            it.registerHandler(retrieveUserCommandHandler)
            it.registerHandler(searchUserCommandHandler)
            it.registerHandler(retrieveUserTrustScoreCommandHandler)
            it.registerHandler(googleLoginCommandHandler)
            // Feedback
            it.registerHandler(createFeedbackCommandHandler)
            it.registerHandler(listFeedbacksCommandHandler)
            // Friend
            it.registerHandler(createFriendshipSolicitudeCommandHandler)
            it.registerHandler(acceptFriendshipSolicitudeCommandHandler)
            it.registerHandler(listFriendshipSolicitudesCommandHandler)
            it.registerHandler(rejectFriendshipSolicitudeCommandHandler)
            it.registerHandler(listFriendsCommandHandler)
            it.registerHandler(deleteFriendsCommandHandler)
            // Vehicle
            it.registerHandler(createVehicleCommandHandler)
            it.registerHandler(listVehiclesCommandHandler)
            it.registerHandler(deleteVehicleCommandHandler)
        }
    }
}