package com.roadlink.core.api.friend

import com.roadlink.application.command.CommandHandler
import com.roadlink.application.friend.*
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.RepositoryAdapter
import com.roadlink.core.infrastructure.friend.FriendshipSolicitudeDynamoDbEntityMapper
import com.roadlink.core.infrastructure.friend.FriendshipSolicitudeDynamoDbQueryMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
open class FriendDefinition {

    @Bean
    open fun friendshipSolicitudeRepository(dynamoDbClient: DynamoDbClient): RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria> {
        val dynamoEntityMapper = FriendshipSolicitudeDynamoDbEntityMapper()
        val dynamoQueryMapper = FriendshipSolicitudeDynamoDbQueryMapper()
        return RepositoryAdapter(
            dynamoDbClient,
            "RoadlinkCore",
            dynamoEntityMapper,
            dynamoQueryMapper
        )
    }

    @Bean("create_friendship_solicitude_command_handler")
    open fun createFriendshipSolicitudeCommandHandler(
        userRepository: RepositoryPort<User, UserCriteria>,
        friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>
    ): CommandHandler<CreateFriendshipSolicitudeCommand, CreateFriendshipSolicitudeCommandResponse> {
        return CreateFriendshipSolicitudeCommandHandler(
            userRepository,
            friendshipSolicitudeRepository
        )
    }

    @Bean("accept_friendship_solicitude_command_handler")
    open fun acceptFriendshipSolicitudeCommandHandler(
        userRepository: RepositoryPort<User, UserCriteria>,
        friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>
    ): CommandHandler<AcceptFriendshipSolicitudeCommand, AcceptFriendshipSolicitudeCommandResponse> {
        return AcceptFriendshipSolicitudeCommandHandler(
            userRepository,
            friendshipSolicitudeRepository
        )
    }

    @Bean("reject_friendship_solicitude_command_handler")
    open fun rejectFriendshipSolicitudeCommandHandler(
        userRepository: RepositoryPort<User, UserCriteria>,
        friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>
    ): CommandHandler<RejectFriendshipSolicitudeCommand, RejectFriendshipSolicitudeCommandResponse> {
        return RejectFriendshipSolicitudeCommandHandler(
            userRepository,
            friendshipSolicitudeRepository
        )
    }

    @Bean("list_friendship_solicitudes_command_handler")
    open fun listFriendshipSolicitudeCommandHandler(
        userRepository: RepositoryPort<User, UserCriteria>,
        friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>
    ): CommandHandler<ListFriendshipSolicitudesCommand, ListFriendshipSolicitudesCommandResponse> {
        return ListFriendshipSolicitudesCommandHandler(
            userRepository,
            friendshipSolicitudeRepository
        )
    }

    @Bean("list_friends_command_handler")
    open fun listFriendsCommandHandler(userRepository: RepositoryPort<User, UserCriteria>): CommandHandler<ListFriendsCommand, ListFriendsCommandResponse> {
        return ListFriendsCommandHandler(userRepository)
    }

    @Bean("delete_friend_command_handler")
    open fun deleteFriendsCommandHandler(userRepository: RepositoryPort<User, UserCriteria>): CommandHandler<DeleteFriendCommand, DeleteFriendCommandResponse> {
        return DeleteFriendCommandHandler(userRepository)
    }
}
