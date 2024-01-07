package com.roadlink.core.api.friend

import com.roadlink.application.command.CommandHandler
import com.roadlink.application.friend.*
import com.roadlink.core.domain.RepositoryPort
import com.roadlink.core.domain.friend.FriendshipSolicitude
import com.roadlink.core.domain.friend.FriendshipSolicitudeCriteria
import com.roadlink.core.domain.user.User
import com.roadlink.core.domain.user.UserCriteria
import com.roadlink.core.infrastructure.dynamodb.BaseDynamoRepository
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
        return RepositoryAdapter(dynamoDbClient, "RoadlinkCore", dynamoEntityMapper, dynamoQueryMapper)
    }

    @Bean("friendship_solicitude_creation_command_handler")
    open fun friendshipSolicitudeCreationCommandHandler(
        userRepository: RepositoryPort<User, UserCriteria>,
        friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>
    ): CommandHandler<FriendshipSolicitudeCreationCommand, FriendshipSolicitudeCreationCommandResponse> {
        return FriendshipSolicitudeCreationCommandHandler(userRepository, friendshipSolicitudeRepository)
    }

    @Bean("friendship_solicitude_acceptance_command_handler")
    open fun friendshipSolicitudeAcceptanceCommandHandler(
        userRepository: RepositoryPort<User, UserCriteria>,
        friendshipSolicitudeRepository: RepositoryPort<FriendshipSolicitude, FriendshipSolicitudeCriteria>
    ): CommandHandler<FriendshipSolicitudeAcceptanceCommand, FriendshipSolicitudeAcceptanceCommandResponse> {
        return FriendshipSolicitudeAcceptanceCommandHandler(userRepository, friendshipSolicitudeRepository)
    }
}
